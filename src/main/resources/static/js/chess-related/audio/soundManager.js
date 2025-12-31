export class SoundManager {
    constructor() {
        this.sounds = {
            move:new Audio('../sounds/public_sound_standard_Move.mp3'),
            capture:new Audio('../sounds/public_sound_standard_Capture.mp3'),
            check:new Audio('../sounds/public_sound_standard_Check.mp3')
        };

        for(const sound of Object.values(this.sounds)) {
            sound.preload = "auto";
        }
    }

    play(name)
    {
        const sound = this.sounds[name];
        if(!sound) return;

        sound.currentTime = 0;
        sound.play();
    }
}

