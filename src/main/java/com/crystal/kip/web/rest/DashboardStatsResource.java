package com.crystal.kip.web.rest;

import com.crystal.kip.service.DashboardStatsService;
import com.crystal.kip.service.DashboardStatsService.DashboardSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard data for the signed-in user. Protected by the "/api/**" authenticated rule in SecurityConfiguration.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardStatsResource {

    private final DashboardStatsService dashboardStatsService;

    public DashboardStatsResource(DashboardStatsService dashboardStatsService) {
        this.dashboardStatsService = dashboardStatsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary() {
        return ResponseEntity.ok(dashboardStatsService.getSummary());
    }
}
