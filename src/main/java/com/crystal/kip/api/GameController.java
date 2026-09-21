package com.crystal.kip.api;

import com.crystal.kip.api.dto.RequestStartDTO;
import com.crystal.kip.domain.User;
import com.crystal.kip.game.GameEngine;
import com.crystal.kip.game.SessionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameEngine gameEngine;
    private final SessionManager sessionManager;

    public GameController(GameEngine gameEngine, SessionManager sessionManager) {
        this.gameEngine = gameEngine;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/start")
    public void /*SessionDTO*/ startGame(@RequestBody RequestStartDTO request, User user) {
        // TODO: Hookup to Content Repository
        // Get the content Game from the Content Repository via request.gameId()
        // Get the content Topic from the Content Repository via request.gameId()
        // GameSession session = gameEngine.startSession(user, game, topic);
        // sessionManager.add(session)
        // return SessionTDO.from(session);
    }
}
