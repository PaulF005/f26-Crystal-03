package com.crystal.kip.game;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.repository.LinkRepo.LinkScenarioRepository;
import com.crystal.kip.repository.StageAttemptCurationRepository;
import com.crystal.kip.service.curation.QuestionCurator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScenarioSelector {

    private final LinkScenarioRepository scenarioRepository;
    private final StageAttemptCurationRepository attemptRepository;
    private final QuestionCurator curator;

    public ScenarioSelector(
        LinkScenarioRepository scenarioRepository,
        StageAttemptCurationRepository attemptRepository,
        QuestionCurator curator
    ) {
        this.scenarioRepository = scenarioRepository;
        this.attemptRepository = attemptRepository;
        this.curator = curator;
    }

    public Scenario selectNew(GameActive active) {
        // Return the next scenario from the selection prepared at session startup.

        List<Scenario> selected = active.getSelectedScenarios();

        if (selected == null) {
            //TODO: Select chosen Branching Narrative story.
            return null;
        }

        int nextIndex = active.getScenariosPlayed().size();

        return nextIndex < selected.size() ? selected.get(nextIndex) : null;
    }

    @Transactional(readOnly = true)
    public void prepareFactFictionSession(GameActive active) {
        Game game = active.getGame();
        Topic topic = active.getTopic();

        var available = scenarioRepository.findByGameIdAndTopicId(game.getId(), topic.getId());

        var attempts = attemptRepository.findCurationHistory(active.getUser().getId(), game.getId(), topic.getId());

        active.setSelectedScenarios(curator.select(available, attempts));
    }
}
