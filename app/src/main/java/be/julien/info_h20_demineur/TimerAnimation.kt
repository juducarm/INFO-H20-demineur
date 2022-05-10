package be.julien.info_h20_demineur

import android.os.CountDownTimer

class TimerAnimation(millisInFuture: Long, countDownInterval: Long, var view: FieldView): CountDownTimer(millisInFuture, countDownInterval) {
    override fun onFinish() {
        view.drawAnim()
    }

    override fun onTick(timeLeft: Long) {
    }
}
