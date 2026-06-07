export class SoundManager {
    private sounds: Record<string, HTMLAudioElement>;

    constructor() {
        this.sounds = {
            move: new Audio('../sounds/public_sound_standard_Move.mp3'),
            capture: new Audio('../sounds/public_sound_standard_Capture.mp3'),
            check: new Audio('../sounds/public_sound_standard_Check.wav'),
            end: new Audio('../sounds/public_sound_standard_End.mp3'),
        };

        for (const sound of Object.values(this.sounds)) {
            sound.preload = "auto";
        }
    }
    play(name: keyof SoundManager["sounds"]) {
        const sound = this.sounds[name];
        if(!sound) return;

        sound.currentTime = 0;
        sound.play();
    }
}

