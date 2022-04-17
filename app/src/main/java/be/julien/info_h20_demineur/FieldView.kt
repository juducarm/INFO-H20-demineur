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
import java.util.*
import kotlin.collections.ArrayList

/* LISTE DES CHANGEMENTS QUE J'AI FAIT (tu peux biensur rechanger si tu trouves que c'était mieux avant)
J'ai mis 2 paramètres pour le nbr de box, comme ça on est pas obligé que ce soit un carré
j'ai enlevé le truc "onSizedChanged", parce que ça sert juste à ce qu'on puisse faire fonctionner l'app
en écran portrait et paysage, mais notre app elle reste en portrait
j'ai changé la variable position en fieldPosition. Elle indique les coordonnées de la case sur le plan des cases
 */


class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr), SurfaceHolder.Callback, Runnable {

    var random = Random()
    var canvas: Canvas? = null
    var drawing = false
    val nbrBoxesWidth = 10
    val nbrBoxesHeight = 10
    val theBoxes = ArrayList<Box>()
    val theBombs = ArrayList<Bomb>()
    val theSafeBoxes = ArrayList<SafeBox>()
    val theCloseBoxes = ArrayList<CloseBox>()
    var pixelPosition = PointF() //position sur l'écran
    var fieldPosition = PointF() //position sur la plan des cases
    lateinit var thread: Thread
    val resolution = PointF(1080f, 1920f) //nombre de pixels sur le fragment
    val boxSize = minOf(resolution.x / nbrBoxesWidth, resolution.y / nbrBoxesHeight)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (box in theBoxes) {
            box.DrawHidden(canvas)
        }
    }
    //création des boxes
    fun boxCreation() {
        (0..nbrBoxesWidth).forEach { x ->
            (0..nbrBoxesHeight).forEach { y ->
                val bomb = Bomb(Point(x - 1, y - 1), boxSize, this)
                val safeBox = SafeBox(Point(x - 1, y - 1), boxSize, this)
                val closeBox = CloseBox(Point(x - 1, y - 1), boxSize, this)
                if (random.nextDouble() < 0.1) theBombs.add(bomb)
                if (random.nextDouble() > 0.9) theSafeBoxes.add(safeBox)
                else theCloseBoxes.add(closeBox)
                }
            }
        }


    fun pause() {
        drawing = false
        thread.join()
    }

    fun resume() {
        drawing = true
        thread = Thread(this)
        thread.start()
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int,
        width: Int, height: Int
    ) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = Thread(this)
        thread.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread.join()
    }

    override fun run() {}

}


    /* utile pour tester le fonctionnement de onTouchEvent :
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
*/



