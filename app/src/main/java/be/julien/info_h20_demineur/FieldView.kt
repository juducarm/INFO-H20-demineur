package be.julien.info_h20_demineur

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Bundle
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.SurfaceHolder
import android.graphics.*
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.collections.ArrayList
import android.os.CountDownTimer as CountDownTimer


class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr) , SurfaceHolder.Callback {


    //réglages du jeu
    var nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth_HARD)
    var nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight_HARD)
    var nbrBombs = resources.getInteger(R.integer.nbrBombs_HARD)
    val resolution = PointF(1080f, 1920f) //nombre de pixels sur le fragment
    val pixelsTopBar =
        resources.getDimension(R.dimen.heightTopBar) + resources.getDimension(R.dimen.heightStatusBar)//hauteur en pixel de la TopBar (Float)
    var boxSize = minOf(resolution.x / nbrBoxesWidth, resolution.y / nbrBoxesHeight)
    val textPaint = Paint()

    //réglages graphiques
    val imageBomb = resources.getDrawable(R.drawable.ic_bomb)
    val imageFlag = resources.getDrawable(R.drawable.ic_flag)
    val hiddenBoxColor1 = resources.getColor(R.color.hiddenBox_color1)
    val hiddenBoxColor2 = resources.getColor(R.color.hiddenBox_color2)
    val safeBoxColor1 = resources.getColor(R.color.safeBox_color1)
    val safeBoxColor2 = resources.getColor(R.color.safeBox_color2)
    val closeBoxColor = resources.getColor(R.color.closeBox_color)
    val numberColor = resources.getColor(R.color.number_color)
    val backgroundPaint = Paint()

    //variables et valeurs pour le jeu
    var flagWitness = "Off "
    var gameOver = false
    var discoveredBoxes = 0
    var nbrFlagsLeft = nbrBombs
    var random = Random()
    var flagModeOn = false
    var firstClick = true
    val activity = context as FragmentActivity
    var drawing = true
    lateinit var thread: Thread
    lateinit var canvasSVP: Canvas
    lateinit var textViewFlag: com.google.android.material.textview.MaterialTextView
    lateinit var textViewTimer: com.google.android.material.textview.MaterialTextView

    //listes d'objets
    val theBoxes = ArrayList<Box>()
    val theBombs = ArrayList<Bomb>()
    val theEmptyBoxes = ArrayList<EmptyBox>()
    val theDiscoveredBoxes = ArrayList<Box>()


    init {
        textPaint.textSize = width / 20f
        textPaint.color = Color.BLACK
    }

    //création des boxes
    fun boxCreation() {
        (1..nbrBoxesWidth).forEach { x ->
            (1..nbrBoxesHeight).forEach { y ->

                lateinit var box: Box
                val aléatoire = random.nextInt(nbrBoxesHeight * nbrBoxesWidth)

                if (aléatoire <= nbrBombs.toFloat()) {
                    box = Bomb(Point(x - 1, y - 1), this)
                    box.isSafe = false //la case n'est pas safe (cf. classe Box)
                    theBombs.add(box)
                } else {
                    box = EmptyBox(Point(x - 1, y - 1), this)
                    theEmptyBoxes.add(box)
                }
                theBoxes.add(box)
                nbrFlagsLeft = theBombs.size
            }
        }
    }

    //gestion du clic du joueur
    override fun onTouchEvent(e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    //clickPosition : position du clic sur le field
                    val clickPosition =
                        Point(
                            (e.rawX / boxSize).toInt(),
                            ((e.rawY - pixelsTopBar) / boxSize).toInt()
                        )

                    //repere la case sous le clic
                    if (theBoxes.any { it.fieldPosition == clickPosition }) {//vérifie que le clic est sur le champ de case
                        var boxUnderClick = theBoxes.single { it.fieldPosition == clickPosition }

                        if (flagModeOn) {
                            boxUnderClick.plantFlag()
                        } else {
                            if (firstClick) { //fait en sorte que le premier clic soit toujours sur une case safe
                                firstClick = false
                                while (!boxUnderClick.isSafe) {
                                    theBombs.clear()
                                    theEmptyBoxes.clear()
                                    theBoxes.clear()
                                    boxCreation()
                                    theBombs.forEach { it.warningBomb(theEmptyBoxes) }
                                    textViewFlag.text = flagWitness + theBombs.size.toString()
                                    boxUnderClick =
                                        theBoxes.single { it.fieldPosition == clickPosition }
                                }
                            }
                            boxUnderClick.discover()
                            if (boxUnderClick.isSafe) {
                                val emptyBox: EmptyBox =
                                    boxUnderClick.invoke()   //change la classe de l'objet de Box à EmptyBox pour utiliser cleanField()
                                emptyBox.cleanField() //devoile toute la partie safe autours de la case
                                emptyBox.showAround()
                            }
                            if (!theDiscoveredBoxes.contains(boxUnderClick) && !theBombs.contains(
                                    boxUnderClick
                                )
                            ) {
                                theDiscoveredBoxes.add(boxUnderClick)
                                winCondition()
                            }

                        }
                        invalidate() //appel à la méthode onDraw
                    }
                }


        }
        return true
    }

    //gestion de du mode drapeau
    fun flagMode() {
        if (flagModeOn) {
            flagModeOn = false
            flagWitness = "Off "
        } else {
            flagModeOn = true
            flagWitness = "On "
        }
        textViewFlag.text = flagWitness + nbrFlagsLeft.toString()
    }

    fun countFlagsLeft(flagMode: Boolean) {
        if (flagMode) {
            nbrFlagsLeft--
        } else {
            nbrFlagsLeft++
        }
        textViewFlag.text = flagWitness + nbrFlagsLeft.toString()
    }

    //gestion du dessin (ressine tout le plan du jeu à chaque modif
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        theBoxes.forEach { it.draw(canvas) }

    }


    fun showGameOverDialog(messageId: Int) {
        class GameResult : DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(resources.getString(messageId))
                //builder.setMessage(resources.getString(R.string.results_format, discoveredBoxes, totalElapsedTime))
                builder.setPositiveButton(R.string.reset_game,
                    DialogInterface.OnClickListener { _, _ -> newGame() }
                )
                return builder.create()
            }
        }
        activity.runOnUiThread(
            Runnable {
                val ft = activity.supportFragmentManager.beginTransaction()
                val prev =
                    activity.supportFragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val gameResult = GameResult()
                gameResult.setCancelable(false)
                gameResult.show(ft, "dialog")
            }
        )
    }

    //gestion de fin du jeu
    fun winCondition() {
        if (theDiscoveredBoxes.size == (nbrBoxesHeight * nbrBoxesWidth - theBombs.size)) {
            gameWon()
        }
    }

    fun newGame() {
        discoveredBoxes = 0
        flagModeOn = false
        firstClick = true
        drawing = true
        gameOver = false
        theDiscoveredBoxes.clear()
        theBombs.clear()
        theBoxes.clear()
        theEmptyBoxes.clear()
        boxCreation()
        theBombs.forEach { it.warningBomb(theEmptyBoxes) }
        invalidate()
    }

    fun gameWon() {
        drawing = false
        discoveredBoxes = theDiscoveredBoxes.size
        showGameOverDialog(R.string.win)
    }

    fun gameLost() {
        theBombs.forEach { it.hide = false }
        discoveredBoxes = theDiscoveredBoxes.size - 1 //nombre de boxes qui ont été découvertes pdt les partie
        showGameOverDialog(R.string.lose)
        drawing = false
        invalidate()
    }

    fun pause() {
        drawing = false
    }

    fun resume() {
        drawing = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {}

}






