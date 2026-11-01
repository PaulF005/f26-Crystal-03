import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AudioService } from 'app/core/audio/audio.service';

@Component({
  selector: 'jhi-settings',
  templateUrl: './settings.html',
  imports: [RouterLink, FormsModule],
})
export default class Settings implements OnInit {
  private audioService = inject(AudioService);

  soundEffectsEnabled: boolean = true;

  ngOnInit(): void {
    const savedSound = localStorage.getItem('soundEffectsEnabled');

    if (savedSound !== null) {
      this.soundEffectsEnabled = savedSound === 'true';
    }

    this.audioService.soundEffectsEnabled.set(this.soundEffectsEnabled);
  }

  onSoundToggleChange(): void {
    localStorage.setItem('soundEffectsEnabled', this.soundEffectsEnabled.toString());

    this.audioService.soundEffectsEnabled.set(this.soundEffectsEnabled);
  }
}
