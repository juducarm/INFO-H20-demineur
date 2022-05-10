package be.julien.info_h20_demineur

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.fragment_field.view.*


class TimeBarView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr) , SurfaceHolder.Callback, Runnable, DrawAnimation {

    private var drawing = true
    private var threadRunning = false
    private lateinit var thread: Thread
    private lateinit var canvas: Canvas

    //réglages des positions
    private val xRes = resources.getInteger(R.integer.xResolution)
    private val yRes = resources.getInteger(R.integer.yResolution)
    private val marginLeft = resources.getInteger(R.integer.marginLeftTimeBar)
    private val marginTop = resources.getInteger(R.integer.marginTopTimeBar)
    private val heightBar = resources.getInteger(R.integer.heightTimeBar)
    private val widthBar = resources.getInteger(R.integer.widthTimeBar)
    private val barArea = Rect(marginLeft, marginTop, marginLeft + widthBar, marginTop + heightBar)
    private val backgroundArea = Rect(0, 0, xRes, yRes)
    private val padding = resources.getInteger(R.integer.paddingtTimeBar)

    private val barPaint = Paint()
    private val backgroundPaint = Paint()
    private val timePaint = Paint()

    private var timeWidth = 0
    private var timeArea = Rect(marginLeft + padding, marginTop + padding,
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
            drawAnim()
        }
    }

    override fun drawAnim() {
        super.drawAnim()
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas.drawRect(backgroundArea, backgroundPaint)
            canvas.drawRect(barArea, barPaint)
            canvas.drawRect(timeArea, timePaint)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    fun start() {
        drawing = true
        threadRunning = true
        thread = Thread(this)
        thread.start()
    }

    fun stop() {
        if (threadRunning) thread.interrupt()
        drawing = false
    }

    fun updateBar(timeLeft: Long) {
        if (timeLeft > timeMax)  timeMax = timeLeft
        var timePourcentage = timeLeft.toFloat() / timeMax.toFloat()
        timeWidth = ((widthBar - 2*padding) * timePourcentage).toInt()
        timeArea = Rect(marginLeft + padding, marginTop + padding,
            marginLeft + padding + timeWidth,
            marginTop + heightBar - padding)
    }


    override fun surfaceCreated(p0: SurfaceHolder) {}
    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
    override fun surfaceDestroyed(p0: SurfaceHolder) {}
}
