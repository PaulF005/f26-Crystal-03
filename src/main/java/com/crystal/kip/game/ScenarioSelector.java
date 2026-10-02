package com.crystal.kip.game;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.repository.LinkScenarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScenarioSelector {

    private final LinkScenarioRepository scenarioRepository;

    public ScenarioSelector(LinkScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    public Scenario selectNext(GameSession session) {
        Game game = session.getGame();
        Topic topic = session.getTopic();

        List<Scenario> scenarios = scenarioRepository.findByGameIdAndTopicId(game.getId(), topic.getId());

        // choose from scenarios that fit some criteria tbd
        return null;
    }
}
