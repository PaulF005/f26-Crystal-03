import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AudioService } from 'app/core/audio/audio.service';
import { ClickSoundDirective } from 'app/core/audio/click.sound.directive';

@Component({
  selector: 'jhi-settings',
  templateUrl: './settings.html',
  imports: [RouterLink, FormsModule, ClickSoundDirective],
})
export default class Settings implements OnInit {
  private audioService = inject(AudioService);

  soundEffectsEnabled: boolean = true;

  ngOnInit(): void {
    this.soundEffectsEnabled = this.audioService.soundEffectsEnabled();

    console.log('Initial sound setting:', this.soundEffectsEnabled);
  }

  onSoundToggleChange(): void {
    localStorage.setItem('soundEffectsEnabled', this.soundEffectsEnabled.toString());

    this.audioService.soundEffectsEnabled.set(this.soundEffectsEnabled);

    console.log('Sound effects enabled:', this.soundEffectsEnabled);
  }
}
