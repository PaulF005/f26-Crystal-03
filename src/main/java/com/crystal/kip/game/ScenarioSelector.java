package com.crystal.kip.game;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.repository.LinkRepo.LinkScenarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScenarioSelector {

    private final LinkScenarioRepository scenarioRepository;

    public ScenarioSelector(LinkScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    public Scenario selectNew(GameActive active) {
        Game game = active.getGame();
        Topic topic = active.getTopic();
        List<Scenario> scenarios = active.getScenariosPlayed();

        // TODO: Select new Scenario from repository (Curation) using current game, current topic, and scenarios already played
        // Should also take into consideration the user's performance in the game session so far
        // That is, select Scenarios that cover concepts they've been struggling with
        // This is why each proceeding Scenario is selected upon each Scenario's completion during runtime rather than in advance, all at once

        return null;
    }
}
