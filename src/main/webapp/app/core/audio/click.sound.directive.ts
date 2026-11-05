import { Directive, HostListener, Input, inject } from '@angular/core';
import { AudioService } from 'app/core/audio/audio.service';

@Directive({
  selector: '[appClickSound]',
  standalone: true,
})
export class ClickSoundDirective {
  private audioService = inject(AudioService);

  @Input('appClickSound') soundPath = '/content/sounds/select_neutral.wav';
  @HostListener('click')
  onClick(): void {
    this.audioService.play(this.soundPath);
  }
}
