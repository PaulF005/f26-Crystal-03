import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AudioService {
  masterVolume = signal<number>(0.8);

  soundEffectsEnabled = signal<boolean>(localStorage.getItem('soundEffectsEnabled') !== 'false');

  play(assetPath: string): void {
    if (!this.soundEffectsEnabled()) {
      return;
    }

    const audio = new Audio(assetPath);
    audio.volume = this.masterVolume();
    audio.play().catch(() => {});
  }
}
