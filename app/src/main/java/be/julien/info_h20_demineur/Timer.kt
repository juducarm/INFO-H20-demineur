package be.julien.info_h20_demineur

import android.os.CountDownTimer

class Timer(millisInFuture: Long, countDownInterval: Long, var view: FieldView): CountDownTimer(millisInFuture, countDownInterval) {
    override fun onFinish() {
        view.gameLost()
    }

    override fun onTick(timeLeft: Long) {
        view.displayTimer( timeLeft )
        view.countElapsedTime()
    }
}
