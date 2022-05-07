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
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.SurfaceHolder
import android.graphics.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.collections.ArrayList



class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr) , SurfaceHolder.Callback{


    //réglages du jeu
    var nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth_HARD)
    var nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight_HARD)
    var nbrBombs = resources.getInteger(R.integer.nbrBombs_HARD)
    val resolution = PointF(1080f, 1300f) //nombre de pixels sur le fragment
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

    //variables et valeurs pour le jeu
    var flagWitness = "Off "
    var gameOver = false
    var discoveredBoxes = 0
    var random = Random()
    var flagModeOn = false
    var firstClick = true
    val activity = context as FragmentActivity
    var drawing = true
    lateinit var textViewFlag: com.google.android.material.textview.MaterialTextView
    lateinit var textViewTimer: com.google.android.material.textview.MaterialTextView

    //listes d'objets
    val theBoxes = ArrayList<Box>()
    val theBombs = ArrayList<Bomb>()
    val theEmptyBoxes = ArrayList<EmptyBox>()
    val theDiscoveredBoxes = ArrayList<Box>()
    val theFlags = ArrayList<Flag>()
    val theLists = listOf(theBombs, theBoxes, theDiscoveredBoxes, theEmptyBoxes, theFlags)

    //variables pour le timer
    val initialTime = resources.getInteger(R.integer.initial_time).toLong()
    val timerInterval = resources.getInteger(R.integer.timer_interval).toLong()
    var timer = Timer(initialTime, timerInterval, this)
    val textTimer = resources.getString(R.string.timeRemaining)
    var timeReward = resources.getInteger(R.integer.hit_reward_easy).toLong()
    var timeLeftOnGame = initialTime


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
            }
        }
    }

    //gestion du clic du joueur
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                //clickPosition : position du clic sur le field
                val clickPosition = Point((e.rawX / boxSize).toInt(), ((e.rawY - pixelsTopBar) / boxSize).toInt())

                //repere la case sous le clic
                if (theBoxes.any { it.fieldPosition == clickPosition }) {//vérifie que le clic est sur le champ de case
                    var boxUnderClick = theBoxes.single { it.fieldPosition == clickPosition }

                    //fait en sorte que le premier clic soit toujours sur une case safe
                    if (firstClick) {
                        firstClick = false
                        while (!boxUnderClick.isSafe) {
                            boxUnderClick = cleanFirstClic(clickPosition)
                        }
                        boxUnderClick.invoke().cleanField()
                    }
                    else {

                        if (flagModeOn) {
                            if (!theDiscoveredBoxes.any { it.fieldPosition == clickPosition }) {//vérifie que la case n'est pas déjà découverte
                                if (theFlags.any { it.fieldPosition == clickPosition }) {
                                    theFlags.removeIf { it.fieldPosition == clickPosition }
                                } else {
                                    theFlags.add(Flag(clickPosition, this))
                                }
                            }
                        }
                        else {
                            timeBonus(timeReward, timeLeftOnGame)
                            boxUnderClick.discover()
                            if (boxUnderClick.isSafe) {
                                boxUnderClick.invoke().cleanField() //devoile toute la partie safe autours de la case
                            }
                            if (!theDiscoveredBoxes.contains(boxUnderClick) && !theBombs.contains(boxUnderClick)) {
                                theDiscoveredBoxes.add(boxUnderClick)
                                winCondition()
                            }

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
        textViewFlag.text = flagWitness + (theBombs.size - theFlags.size).toString()
    }

    fun countFlagsLeft() {
        textViewFlag.text = flagWitness + (theBombs.size - theFlags.size).toString()
    }

    //gestion du dessin (ressine tout le plan du jeu à chaque modif
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        theBoxes.forEach { it.draw(canvas) }
        theFlags.forEach { it.draw(canvas) }
    }


    //fait en sorte que le premier clique soit toujours sur une case safe
    fun cleanFirstClic(position: Point): Box{
        theLists.forEach { it.clear() }
        boxCreation()
        theBombs.forEach { it.warningBomb(theEmptyBoxes) }
        textViewFlag.text = flagWitness + theBombs.size.toString()
        return theBoxes.single { it.fieldPosition == position }
    }

    //gestion de fin du jeu
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

    fun winCondition() {
        if (theDiscoveredBoxes.size == (nbrBoxesHeight * nbrBoxesWidth - theBombs.size)) {
            gameWon()
        }
    }

    fun newGame() {
        discoveredBoxes = 0
        timeLeftOnGame = initialTime
        flagModeOn = false
        firstClick = true
        drawing = true
        gameOver = false
        theLists.forEach { it.clear() }
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
        //thread.join()

    }

    fun resume() {
        timer.start()
        drawing = true
        //thread.start()

    }

    //gestion du timer
    fun displayTimer(timeLeft: Long) {
        println(timeLeft)
        timeLeftOnGame = timeLeft
        textViewTimer.text = textTimer + " " + timeLeft.toString()
    }

    fun timeBonus(timeReward: Long, timeLeft: Long) {
        println("time reawrd : $timeReward")
        println("time left : ${timeLeft + timeReward}")
        timer.cancel()
        timer = Timer( timeLeft*1000 + timeReward, timerInterval, this)
        timer.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {}



}





