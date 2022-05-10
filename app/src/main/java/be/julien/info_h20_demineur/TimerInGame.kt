package be.julien.info_h20_demineur

import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_main.*

class TimerInGame(millisInFuture: Long, countDownInterval: Long, var view: FieldView): CountDownTimer(millisInFuture, countDownInterval) {
    override fun onFinish() {
        if (view.playing) view.gameLost()
    }

    override fun onTick(timeLeft: Long) {
        if (view.playing) {
            view.displayTimer(timeLeft)
            view.countElapsedTime()
            view.activity.timeBarView.updateBar(timeLeft)

        }
    }
}
