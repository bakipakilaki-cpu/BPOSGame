package com.example.ui

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

object TerminalSoundPlayer {
    fun playSineWave(frequency: Double, durationMs: Int, volume: Double = 0.8) {
        Thread {
            var audioTrack: AudioTrack? = null
            try {
                val sampleRate = 44100
                val numSamples = (durationMs * sampleRate / 1000)
                val sample = DoubleArray(numSamples)
                val generatedSnd = ByteArray(2 * numSamples)

                for (i in 0 until numSamples) {
                    sample[i] = Math.sin(2.0 * Math.PI * i / (sampleRate / frequency))
                }

                var idx = 0
                for (dVal in sample) {
                    val valShort = (dVal * 32767 * volume).toInt().toShort()
                    generatedSnd[idx++] = (valShort.toInt() and 0x00ff).toByte()
                    generatedSnd[idx++] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
                }

                audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    generatedSnd.size,
                    AudioTrack.MODE_STATIC
                )
                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.play()
                Thread.sleep(durationMs.toLong() + 50)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }.start()
    }

    // Visa: double beep (high pitch, short duration)
    fun playVisaSound() {
        Thread {
            playSineWave(1500.0, 80, 0.7)
            Thread.sleep(120)
            playSineWave(1500.0, 80, 0.7)
        }.start()
    }

    // Mastercard: triple beep melody (increasing pitch)
    fun playMastercardSound() {
        Thread {
            playSineWave(987.77, 60, 0.6) // B5
            Thread.sleep(90)
            playSineWave(1318.51, 60, 0.6) // E6
            Thread.sleep(90)
            playSineWave(1567.98, 100, 0.6) // G6
        }.start()
    }

    // Bcard: customized rich playful sequence
    fun playBCardSound() {
        Thread {
            playSineWave(1318.51, 50, 0.5) // E6
            Thread.sleep(60)
            playSineWave(1567.98, 50, 0.5) // G6
            Thread.sleep(60)
            playSineWave(2093.00, 50, 0.5) // C7
            Thread.sleep(60)
            playSineWave(2637.02, 100, 0.7) // E7
        }.start()
    }

    // Apple Pay Chime: two-tone ascending payment chime
    fun playApplePaySound() {
        Thread {
            playSineWave(783.99, 90, 0.7) // G5
            Thread.sleep(110)
            playSineWave(1567.98, 160, 0.8) // G6
        }.start()
    }

    // Success Beep: single clean tone
    fun playSuccessBeep() {
        playSineWave(1000.0, 200, 0.7)
    }

    // Error Buzzer: low pitch drone
    fun playErrorBuzzer() {
        playSineWave(220.0, 350, 0.8)
    }
}
