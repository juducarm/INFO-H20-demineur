package be.julien.info_h20_demineur

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.*
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.*
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.SparseIntArray
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.collections.ArrayList


class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr) {

    var random = Random()
    var canvas: Canvas? = null

    //listes d'objets
    val theBoxes = ArrayList<Box>()
    val theBombs = ArrayList<Bomb>()
    val theEmptyBoxes = ArrayList<EmptyBox>()

    //réglages
    val nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth)
    val nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight)
    val resolution = PointF(1080f, 1920f) //nombre de pixels sur le fragment
    val pixelsTopBar = resources.getDimension(R.dimen.heightTopBar) //hauteur en pixel de la TopBar (Float)
    val boxSize = minOf(resolution.x / nbrBoxesWidth, resolution.y / nbrBoxesHeight)
    val gameDifficulty = resources.getInteger(R.integer.gameDifficutly).toFloat() / 100


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //theEmptyBoxes.forEach { box -> box.findBombsAround(theBombs) }
        theBombs.forEach { bomb -> bomb.warningBomb(theEmptyBoxes) }
        theBoxes.forEach { box -> box.DrawHidden(canvas) }
        println("dessine")
    }

    //création des boxes
    fun boxCreation() {
        (1..nbrBoxesWidth).forEach { x ->
            (1..nbrBoxesHeight).forEach { y ->

                lateinit var box: Box

                if (random.nextDouble() <= gameDifficulty) {
                    box = Bomb(Point(x - 1, y - 1), boxSize, this)
                    theBombs.add(box)
                }
                else {
                    box = EmptyBox(Point(x - 1, y - 1), boxSize, this)
                    theEmptyBoxes.add(box)
                }
                theBoxes.add(box)
                }
            }
        }


    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {MotionEvent.ACTION_DOWN -> {
            //eventOnField : position du clic sur le field
            val eventOnField = Point((e.rawX / boxSize).toInt(),((e.rawY - pixelsTopBar)/ boxSize).toInt())
            println("position du clic : $eventOnField")
            //repere la case sous le clic
            val BoxUnderEvent = theBoxes.filter{ it.fieldPosition == eventOnField}.single()
            println(BoxUnderEvent)
            BoxUnderEvent.DrawDiscover(canvas)
            theBoxes.forEach { box -> box.DrawHidden(canvas) }
            }
        }
        return true
    }


}



