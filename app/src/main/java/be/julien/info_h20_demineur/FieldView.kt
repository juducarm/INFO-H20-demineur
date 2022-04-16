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


class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr){

    lateinit var canvas: Canvas
    val drawing = false
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

}