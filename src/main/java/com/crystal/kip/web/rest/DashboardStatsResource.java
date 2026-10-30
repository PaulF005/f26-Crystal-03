package com.crystal.kip.web.rest;

import com.crystal.kip.service.DashboardStatsService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardStatsResource {

    private final DashboardStatsService dashboardStatsService;

    public DashboardStatsResource(DashboardStatsService dashboardStatsService) {
        this.dashboardStatsService = dashboardStatsService;
    }

    @GetMapping("/current-streak")
    public ResponseEntity<Map<String, Integer>> getCurrentStreak() {
        int currentStreak = dashboardStatsService.getCurrentStreak();

        return ResponseEntity.ok(Map.of("currentStreak", currentStreak));
    }
}
