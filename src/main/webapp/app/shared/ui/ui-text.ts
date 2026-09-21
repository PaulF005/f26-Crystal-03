export const UI_TEXT = {
  brand: {
    nameShort: 'KIP',
    nameFull: 'Knowledge Is Power',
  },

  auth: {
    login: 'Log In',
    signup: 'Sign Up',
    username: 'Username',
    password: 'Password',
  },

  page: {
    dashboard: 'Dashboard',
    browser: 'Browser',
    progress: 'Progress',
    settings: 'Settings',
  },

  dashboard: {
    dailyRecommendation: 'Daily Recommendation',
    recentActivity: 'Recent Activity',
    profileSummary: 'Profile Summary',

    momentum: {
      currentStreak: 'Current Streak',
      weeklyPractice: 'Weekly Practice',
    },
  },

  topic: {
    onTheRoad: 'On the Road',
    creativeRights: 'Creative Rights',
  },

  game: {
    stat: {
      history: {
        label: 'History',

        level: {
          new: 'New',
        },
      },

      experience: {
        label: 'Experience',

        level: {
          unfamiliar: 'Unfamiliar',
          familiar: 'Familiar',
          experienced: 'Experienced',
        },
      },
    },

    common: {
      next: 'Next...',
    },

    factOrFiction: {
      label: 'Fact or Fiction?',
      intro: 'THE CLAIM:',
      question: 'Is this...',

      answer: {
        fact: 'Fact',
        fiction: 'Fiction',
        situational: 'Situational',
      },
    },

    branchingNarrative: {
      label: 'Branching Narrative',
      intro: 'THE SITUATION:',
      question: 'Do you...',
    },
  },

  time: {
    minute: 'minute',
    hour: 'hour',
    day: 'day',
    week: 'week',
    month: 'month',
    year: 'year',
    last: 'Last',
    yesterday: 'Yesterday',
  },
} as const;
