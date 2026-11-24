# Fact or Fiction system test results

**Project:** Knowledge Is Power (KIP)  
**Test date:** November 24, 2026  
**Build:** `main` at `8cbcb1d`  
**Environment:** Windows, Spring Boot `dev`, existing local MySQL database `kip`

## Results

| Check | Expected | Observed | Status |
|---|---|---|---|
| Application startup | KIP starts against local MySQL. | Spring Boot started and reported the app running on port 8080. | Pass |
| Admin session persistence | MySQL stores a completed Fact or Fiction round under the admin profile. | Latest completed session was `1503`. The query returned three completed sessions for this profile and game. | Pass |
| Admin progress persistence | The saved progress count matches the completed sessions. | `game_progress` showed 3 sessions and a stored average of `0.466667`, which rounds to 47%. The latest session completion and progress timestamps were about 0.14 seconds apart. | Pass |
| Games card update (#41) | After one full round, the Games card shows one additional session. | In the `rthomas` UI run, Experience changed from 50% average (2 sessions) to 47% average (3 sessions). History showed November 24 in both captures. | Pass for UI update |
| Completion screen (#43) | A full round opens the completion screen. | Tester confirmed the completion screen worked. Individual controls and destinations were not reported. | Pass for screen display |
| Browser refresh | The admin Games card retains the saved Experience after reload. | After refresh, the card showed **47% average (3 sessions)**. | Pass |
| Early exit | Leaving after one answer preserves the completed-session summary. | Returned to Progress and saw the same Experience. MySQL still showed 3 completed sessions, average `0.466667`, the same last-played timestamp, and session `1503` as the latest completed round. | Pass |

The `rthomas` observation covers the Games card only. Its account role and MySQL records were not verified in this run. Remaining tests use the local admin account. New-user and logged-out cases are deferred for this cycle.

## Admin database evidence

| Session ID | Started at | Completed at |
|---:|---|---|
| 1503 | 2026-11-24 17:24:37.970293 | 2026-11-24 17:24:55.769288 |
| 1502 | 2026-11-24 17:21:05.768970 | 2026-11-24 17:21:23.110531 |
| 1500 | 2026-11-19 19:25:41.733817 | 2026-11-19 19:26:50.488134 |

Timestamps are shown as returned by MySQL. The progress row recorded `last_played_at = 2026-11-24 17:24:55.905169`. Session `1500` is from the earlier test on `085adcb`.

## Reproduce the database check

Sign in to the local app as admin, record the Games card baseline, then complete a full Fact or Fiction round. Run these read-only queries in MySQL against `kip`.

```sql
SELECT gs.id AS session_id, gs.started_at, gs.completed_at
FROM game_session AS gs
JOIN user_profile AS up ON up.id = gs.user_id
JOIN game AS g ON g.id = gs.game_id
WHERE up.username = 'admin'
  AND g.name = 'Fact or Fiction'
ORDER BY gs.completed_at DESC, gs.id DESC
LIMIT 5;

SELECT gp.sessions_played,
       gp.performance AS stored_average,
       ROUND(gp.performance * 100) AS card_average_percent,
       gp.last_played_at
FROM game_progress AS gp
JOIN user_profile AS up ON up.id = gp.user_profile_id
JOIN game AS g ON g.id = gp.game_id
WHERE up.username = 'admin'
  AND g.name = 'Fact or Fiction';
```

## Remaining admin checks

- [ ] Verify the completion screen question count and both link destinations individually. View Progress should open Progress. Play Again should open Games under the current implementation.
- [ ] When the yellow X question appears, choose **False** and verify that the feedback says to leave the lane as soon as it is safe. The expected answer was verified against the [Virginia Driver’s Manual](https://www.dmv.virginia.gov/sites/default/files/forms/dmv39.pdf), page 6. In-game grading and feedback remain pending.
