package be.julien.info_h20_demineur

import android.os.CountDownTimer

class Timer(val view: FieldView): CountDownTimer(120000, 1000) {
    override fun onFinish() {
        view.gameLost()
    }

    override fun onTick(timeLeft: Long) {
        view.displayTimer( timeLeft / 1000 )
    }
}