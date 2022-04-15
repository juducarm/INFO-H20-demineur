package be.julien.info_h20_demineur

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.*

class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr){

    lateinit var canvas: Canvas
    val drawing = false


}