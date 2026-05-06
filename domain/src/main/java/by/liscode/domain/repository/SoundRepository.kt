package by.liscode.domain.repository

interface SoundRepository {
    fun playSound(soundId: Int)
    fun stopSound()
}