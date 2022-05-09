package be.julien.info_h20_demineur

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.fragment_field.view.*


class TimeBarView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr) , SurfaceHolder.Callback, Runnable {

    var drawing = true
    var threadRunning = false
    lateinit var thread: Thread
    lateinit var canvas: Canvas

    //réglages des positions
    val xRes = resources.getInteger(R.integer.xResolution)
    val yRes = resources.getInteger(R.integer.yResolution)
    val marginLeft = resources.getInteger(R.integer.marginLeftTimeBar)
    val marginTop = resources.getInteger(R.integer.marginTopTimeBar)
    val heightBar = resources.getInteger(R.integer.heightTimeBar)
    val widthBar = resources.getInteger(R.integer.widthTimeBar)
    val barArea = Rect(marginLeft, marginTop, marginLeft + widthBar, marginTop + heightBar)
    val backgroundArea = Rect(0, 0, xRes, yRes)
    val padding = 5

    val barPaint = Paint()
    val backgroundPaint = Paint()
    val timePaint = Paint()

    var timeWidth = 0
    var timeArea = Rect(marginLeft + padding, marginTop + padding,
        marginLeft + padding + timeWidth,
        marginTop + heightBar - padding)
    var timeMax = 0L


    init {
        barPaint.color = resources.getColor(R.color.white)
        backgroundPaint.color = resources.getColor(R.color.backgroundPaint_color)
        timePaint.color = resources.getColor(R.color.timerBarColor)
    }

    override fun run() {
        while (drawing) {
            draw()
        }
    }

    fun draw() {
        if (holder.surface.isValid) {
                canvas = holder.lockCanvas()
                canvas.drawRect(backgroundArea, backgroundPaint)
                canvas.drawRect(barArea, barPaint)
                canvas.drawRect(timeArea, timePaint)
                holder.unlockCanvasAndPost(canvas)
        }
    }

    fun start() {
        threadRunning = true
        thread = Thread(this)
        thread.start()
    }

    fun stop() {
        if (threadRunning) thread.interrupt()
    }

    fun updateBar(timeLeft: Long) {
        if (timeLeft > timeMax)  timeMax = timeLeft
        var timePourcentage = timeLeft.toFloat() / timeMax.toFloat()
        timeWidth = ((widthBar - 2*padding) * timePourcentage).toInt()
        timeArea = Rect(marginLeft + padding, marginTop + padding,
            marginLeft + padding + timeWidth,
            marginTop + heightBar - padding)
    }

    fun startDrawing() {
        drawing = true
    }

    fun stopDrawing() {
        drawing = false
    }

    override fun surfaceCreated(p0: SurfaceHolder) {}
    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
    override fun surfaceDestroyed(p0: SurfaceHolder) {}
}
