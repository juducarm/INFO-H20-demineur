package be.julien.info_h20_demineur

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.*
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.SparseIntArray
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity


class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0): SurfaceView(context, attributes,defStyleAttr), SurfaceHolder.Callback, Runnable {
    lateinit var canvas: Canvas
    var drawing = false
    var nbreboxes = width % 18
    var pos_x = 0.0
    var pos_y = 0.0
    lateinit var thread: Thread
    val resolution = PointF(2400f,1080f) //resolution de l'ecran



    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                println(e.rawX.toString())
                println(e.rawY.toString())
            }
        }
        return true
    }


    /*override fun onSizeChanged(w:Int, h:Int, oldw:Int, oldh:Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w.toFloat()
        screenHeight = h.toFloat()
        box.size = (screenWidth / 18f)
    }


    fun draw(){
        for (m in 1..nbreboxes) {
            for (n in 1..nbreboxes) {
                box.draw(pos_x, pos_y, size)
                pos_x += box.size
            }
            pos_y += box.size

        }
    }*/

    fun pause() {
        drawing = false
        thread.join()
    }

    fun resume() {
        drawing = true
        thread = Thread(this)
        thread.start()
    }


    override fun surfaceCreated(p0: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    override fun run() {
        TODO("Not yet implemented")
    }

}