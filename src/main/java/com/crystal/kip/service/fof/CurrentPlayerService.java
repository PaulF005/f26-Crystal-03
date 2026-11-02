package com.crystal.kip.service.fof;

import com.crystal.kip.repository.fof.FactFictionRepository;
import com.crystal.kip.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class CurrentPlayerService {

    private final FactFictionRepository repository;

    public CurrentPlayerService(FactFictionRepository repository) {
        this.repository = repository;
    }

    public long id() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("Sign in before practicing"));
        return repository.playerId(login);
    }
}
