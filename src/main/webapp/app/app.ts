import { registerLocaleData } from '@angular/common';
import locale from '@angular/common/locales/en';
import { Component, HostListener, inject, OnInit } from '@angular/core';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap/datepicker';
import dayjs from 'dayjs/esm';

import { fontAwesomeIcons } from './config/font-awesome-icons';
import Main from './layouts/main/main';

@Component({
  selector: 'jhi-app',
  template: '<jhi-main />',
  imports: [Main],
})
export default class App implements OnInit {
  private readonly iconLibrary = inject(FaIconLibrary);
  private readonly dpConfig = inject(NgbDatepickerConfig);

  private readonly LANDSCAPE_BASE_WIDTH = 768;
  private readonly LANDSCAPE_BASE_HEIGHT = 432;

  private readonly PORTRAIT_BASE_WIDTH = 176;
  private readonly PORTRAIT_BASE_HEIGHT = 336;

  constructor() {
    registerLocaleData(locale);

    this.iconLibrary.addIcons(...fontAwesomeIcons);

    this.dpConfig.minDate = {
      year: dayjs().subtract(100, 'year').year(),
      month: 1,
      day: 1,
    };
  }

  ngOnInit(): void {
    this.updatePixelScale();
  }

  @HostListener('window:resize')
  onResize(): void {
    this.updatePixelScale();
  }

  private updatePixelScale(): void {
    const screenWidth = window.screen.width;
    const screenHeight = window.screen.height;

    const portrait = screenHeight > screenWidth;

    const baseWidth = portrait ? this.PORTRAIT_BASE_WIDTH : this.LANDSCAPE_BASE_WIDTH;

    const baseHeight = portrait ? this.PORTRAIT_BASE_HEIGHT : this.LANDSCAPE_BASE_HEIGHT;

    const scale = Math.max(1, Math.floor(Math.min(screenWidth / baseWidth, screenHeight / baseHeight)));

    document.documentElement.style.setProperty('--scale', scale.toString());
  }
}
