package com.crystal.kip.game;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import org.springframework.stereotype.Service;

@Service
public class StageSelector {

    public Stage selectFirst(Scenario scenario) {
        return scenario.getStartingStage();
    }

    public Stage selectNext(Answer selectedAnswer) {
        return selectedAnswer.getNextStage();
    }

    public boolean isTerminal(Answer selectedAnswer) {
        return selectedAnswer.getTerminalResolution() != null;
    }
}
