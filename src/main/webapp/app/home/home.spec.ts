import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';

import { Account, AccountService } from 'app/core/auth';
import { serverApiUrl } from 'app/config';

import Home from './home';

const SUMMARY_URL = `${serverApiUrl}api/dashboard/summary`;

function makeAccount(login: string): Account {
  return {
    activated: true,
    authorities: ['ROLE_USER'],
    email: `${login}@example.com`,
    firstName: null,
    langKey: 'en',
    lastName: null,
    login,
    imageUrl: null,
  };
}

describe('Home Component', () => {
  let comp: Home;
  let fixture: ComponentFixture<Home>;
  let mockRouter: Router;
  let accountService: AccountService;
  let httpMock: HttpTestingController;

  // Runs the signal effect, then re-renders the template.
  function render(): void {
    fixture.detectChanges();
    TestBed.tick();
    fixture.detectChanges();
  }

  function pageText(): string {
    return fixture.nativeElement.textContent as string;
  }

  // Finds a Profile Summary box by its label and returns the value shown in it.
  function statValue(label: string): string | undefined {
    const container = fixture.nativeElement as HTMLElement;
    const stats = Array.from(container.querySelectorAll<HTMLElement>('.profile-stat'));
    const stat = stats.find(element => element.querySelector('.profile-stat-label')?.textContent.trim() === label);
    return stat?.querySelector('.profile-stat-value')?.textContent.trim();
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      // provideRouter([]) is needed because the template now renders routerLink.
      providers: [provideHttpClientTesting(), provideRouter([])],
    });

    fixture = TestBed.createComponent(Home);
    comp = fixture.componentInstance;
    mockRouter = TestBed.inject(Router);
    accountService = TestBed.inject(AccountService);
    httpMock = TestBed.inject(HttpTestingController);
    vi.spyOn(mockRouter, 'navigate');
  });

  afterEach(() => httpMock.verify());

  it('should navigate to /login on login', () => {
    comp.login();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should not request private stats for a guest', () => {
    render();

    httpMock.expectNone(SUMMARY_URL);
    expect(pageText()).toContain('Sign in to see your profile and progress.');
  });

  it('should load live dashboard statistics for a logged-in user', () => {
    accountService.authenticate(makeAccount('tester'));
    render();
    expect(pageText()).toContain('Loading your progress...');

    const req = httpMock.expectOne(SUMMARY_URL);
    expect(req.request.method).toBe('GET');
    req.flush({
      gamesPlayed: 3,
      averagePerformance: 80,
      currentStreak: 2,
      weeklyPlays: 3,
      favoriteGame: 'Fact or Fiction?',
      recentActivity: [{ id: 1, gameName: 'Fact or Fiction?', completedAt: '2026-10-29T12:00:00Z' }],
    });
    render();

    expect(comp.summary()?.gamesPlayed).toBe(3);
    expect(pageText()).toContain('80%');
    expect(statValue('Weekly Plays')).toBe('3');
    expect(statValue('Favorite Game')).toBe('Fact or Fiction?');
    expect(pageText()).toContain('Fact or Fiction?');
  });

  it('should show the empty message when the user has no activity yet', () => {
    accountService.authenticate(makeAccount('newuser'));
    render();

    httpMock.expectOne(SUMMARY_URL).flush({
      gamesPlayed: 0,
      averagePerformance: null,
      currentStreak: 0,
      weeklyPlays: 0,
      favoriteGame: null,
      recentActivity: [],
    });
    render();

    expect(pageText()).toContain("You haven't completed any games yet.");
    expect(pageText()).not.toContain('null%');
    expect(statValue('Weekly Plays')).toBe('0');
    expect(statValue('Favorite Game')).toBe('—');
    expect(comp.summary()?.favoriteGame).toBeNull();
  });

  it('should show an error message when the request fails', () => {
    accountService.authenticate(makeAccount('tester'));
    render();

    httpMock.expectOne(SUMMARY_URL).flush('Server error', { status: 500, statusText: 'Server Error' });
    render();

    expect(comp.loadError()).toBe(true);
    expect(comp.loading()).toBe(false);
    expect(pageText()).toContain("Couldn't load your progress right now.");
  });

  it('should clear the stats on logout', () => {
    accountService.authenticate(makeAccount('tester'));
    render();
    httpMock.expectOne(SUMMARY_URL).flush({
      gamesPlayed: 3,
      averagePerformance: 80,
      currentStreak: 2,
      weeklyPlays: 3,
      favoriteGame: 'Fact or Fiction?',
      recentActivity: [{ id: 1, gameName: 'Fact or Fiction?', completedAt: '2026-10-29T12:00:00Z' }],
    });
    render();

    accountService.authenticate(null);
    render();

    httpMock.expectNone(SUMMARY_URL);
    expect(comp.summary()).toBeNull();
    expect(pageText()).not.toContain('Fact or Fiction?');
  });

  it("should cancel the first user's request when the account switches", () => {
    accountService.authenticate(makeAccount('userA'));
    render();
    const firstReq = httpMock.expectOne(SUMMARY_URL);

    accountService.authenticate(makeAccount('userB'));
    render();
    const secondReq = httpMock.expectOne(SUMMARY_URL);

    // userA's response must never show up for userB.
    expect(firstReq.cancelled).toBe(true);
    secondReq.flush({
      gamesPlayed: 1,
      averagePerformance: 50,
      currentStreak: 1,
      weeklyPlays: 1,
      favoriteGame: 'Choose the Outcome',
      recentActivity: [],
    });
    render();

    expect(comp.summary()?.gamesPlayed).toBe(1);
    expect(comp.summary()?.weeklyPlays).toBe(1);
    expect(comp.summary()?.favoriteGame).toBe('Choose the Outcome');
    expect(statValue('Favorite Game')).toBe('Choose the Outcome');
  });
});
