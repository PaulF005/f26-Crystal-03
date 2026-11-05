package com.crystal.kip.api;

import com.crystal.kip.api.dto.AnswerDTO;
import com.crystal.kip.api.dto.AnswerResultDTO;
import com.crystal.kip.api.dto.ContinueDTO;
import com.crystal.kip.api.dto.RequestStartDTO;
import com.crystal.kip.api.dto.ResultDTO;
import com.crystal.kip.api.dto.ScenarioDTO;
import com.crystal.kip.api.dto.SessionDTO;
import com.crystal.kip.api.dto.StageDTO;
import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.game.GameActive;
import com.crystal.kip.game.GameEngine;
import com.crystal.kip.game.SessionManager;
import com.crystal.kip.game.StageResult;
import com.crystal.kip.repository.GameRepository;
import com.crystal.kip.repository.LinkRepo.LinkUserProfileRepository;
import com.crystal.kip.repository.TopicRepository;
import com.crystal.kip.security.SecurityUtils;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameEngine gameEngine;
    private final SessionManager sessionManager;

    private final GameRepository gameRepository;
    private final TopicRepository topicRepository;
    private final LinkUserProfileRepository userProfileRepository;

    public GameController(
        GameEngine gameEngine,
        SessionManager sessionManager,
        GameRepository gameRepository,
        TopicRepository topicRepository,
        LinkUserProfileRepository userProfileRepository
    ) {
        this.gameEngine = gameEngine;
        this.sessionManager = sessionManager;
        this.gameRepository = gameRepository;
        this.topicRepository = topicRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @PostMapping("/start")
    public SessionDTO startGame(@RequestBody RequestStartDTO request) {
        if (request.gameID() == null || request.topicID() == null) throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "gameID and topicID are required"
        );

        Game game = gameRepository
            .findById(request.gameID())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found: " + request.gameID()));

        Topic topic = topicRepository
            .findById(request.topicID())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found: " + request.topicID()));

        UserProfile user = getCurrentUserProfile();

        GameActive active = gameEngine.startActive(user, game, topic);

        return toSessionDTO(active);
    }

    @PostMapping("/{sessionId}/answer/{answerId}")
    public AnswerResultDTO selectAnswer(@PathVariable UUID sessionId, @PathVariable Long answerId) {
        GameActive active = getActiveSession(sessionId);

        gameEngine.selectAnswer(active, answerId);

        // A null current Scenario means the selected Answer completed it
        if (active.getScenarioCurrent() == null) {
            return new AnswerResultDTO(null, toResultDTO(active));
        }

        return new AnswerResultDTO(toStageDTO(active.getStageCurrent()), null);
    }

    @PostMapping("/{sessionId}/continue")
    public ContinueDTO continueGame(@PathVariable UUID sessionId) {
        GameActive active = getActiveSession(sessionId);

        gameEngine.continueSession(active);

        // The Session is removed by GameEngine when the entire Game completes
        if (sessionManager.get(sessionId) == null) {
            return new ContinueDTO(null, true);
        }

        return new ContinueDTO(toSessionDTO(active), false);
    }

    @PostMapping("/{sessionId}/abandon")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void abandonGame(@PathVariable UUID sessionId) {
        GameActive active = getActiveSession(sessionId);

        gameEngine.abandonSession(active);
    }

    private GameActive getActiveSession(UUID sessionId) {
        GameActive active = sessionManager.get(sessionId);

        if (active == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active Game Session not found: " + sessionId);

        UserProfile currentUser = getCurrentUserProfile();

        if (!currentUser.getId().equals(active.getUser().getId())) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "Game Session does not belong to the current user"
        );

        return active;
    }

    private UserProfile getCurrentUserProfile() {
        String username = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No current user logged in")
        );

        return userProfileRepository
            .findOneByDataUserLogin(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Profile not found for current user"));
    }

    private SessionDTO toSessionDTO(GameActive active) {
        return new SessionDTO(active.getId(), active.getGame().getName(), toScenarioDTO(active));
    }

    private ScenarioDTO toScenarioDTO(GameActive active) {
        Scenario scenario = active.getScenarioCurrent();

        if (scenario == null) {
            return null;
        }

        int stagesCompleted = (int) active
            .getStageResults()
            .stream()
            .filter(result -> scenario.equals(result.getStage().getScenario()))
            .count();

        return new ScenarioDTO(scenario.getId(), toStageDTO(active.getStageCurrent()), stagesCompleted + 1, scenario.getStages().size());
    }

    private StageDTO toStageDTO(Stage stage) {
        if (stage == null) {
            return null;
        }

        return new StageDTO(
            stage.getId(),
            stage.getQuestion().getQuestion(),
            stage.getQuestion().getAnswers().stream().map(this::toAnswerDTO).toList()
        );
    }

    private AnswerDTO toAnswerDTO(Answer answer) {
        return new AnswerDTO(answer.getId(), answer.getText());
    }

    private ResultDTO toResultDTO(GameActive active) {
        if (active.getStageResults().isEmpty()) throw new IllegalStateException("Cannot build a Scenario result without a Stage result");

        StageResult stageResult = active.getStageResults().get(active.getStageResults().size() - 1);

        Answer selectedAnswer = stageResult.getSelectedAnswer();

        if (selectedAnswer.getTerminalResolution() == null) throw new IllegalStateException(
            "Cannot build a Scenario result from a non-terminal Answer"
        );

        return new ResultDTO(
            null, // TODO: Feedback System evaluates this
            selectedAnswer.getOutcomeText()
        );
    }
}
