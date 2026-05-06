package by.liscode.data.repository

import android.app.Application
import android.media.MediaPlayer
import by.liscode.domain.repository.SoundRepository

class SoundRepositoryImpl(
    private val application: Application,
): SoundRepository {
    private var mediaPlayer: MediaPlayer? = null

    override fun playSound(soundId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(application, soundId).apply {
            setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }
            start()
        }
    }

    override fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}