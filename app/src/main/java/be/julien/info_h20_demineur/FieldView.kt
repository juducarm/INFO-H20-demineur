package be.julien.info_h20_demineur

import android.app.AlertDialog
import android.app.Dialog
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*
import kotlin.collections.ArrayList
import android.os.CountDownTimer as CountDownTimer


class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr) {

    var random = Random()
    var plantFlag = false
    var firstClick = true
    val activity = context as FragmentActivity

    //listes d'objets
    val theBoxes = ArrayList<Box>()
    val theBombs = ArrayList<Bomb>()
    val theEmptyBoxes = ArrayList<EmptyBox>()
    var discoveredBoxes = 0

    //réglages du jeu
    val nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth)
    val nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight)
    val nbrBombs = resources.getInteger(R.integer.nbrBombs)
    val resolution = PointF(1080f, 1920f) //nombre de pixels sur le fragment
    val pixelsTopBar =
        resources.getDimension(R.dimen.heightTopBar) //hauteur en pixel de la TopBar (Float)
    val boxSize = minOf(resolution.x / nbrBoxesWidth, resolution.y / nbrBoxesHeight)
    val gridSize = resources.getInteger(R.integer.gridSize)
    val gameDifficulty = nbrBombs.toFloat()
    var timeLeft = 0.0 //Pour indiquer le temps dans la fenetre de dialogue finale
    val HIT_REWARD_HARD =
        resources.getInteger(R.integer.hit_reward_hard).toLong() //Car getDouble ne fonctionne pas
    val HIT_REWARD_EASY =
        resources.getInteger(R.integer.hit_reward_easy).toLong()  //bonus du mode facile
    var HIT_REWARD: Long = 0
    val textPaint = Paint()
    var gameOver = false

    //réglages graphiques
    val imageBomb = resources.getDrawable(R.drawable.ic_bomb)
    val imageFlag = resources.getDrawable(R.drawable.ic_flag)
    val hiddenBoxColor1 = resources.getColor(R.color.hiddenBox_color1)
    val hiddenBoxColor2 = resources.getColor(R.color.hiddenBox_color2)
    val safeBoxColor1 = resources.getColor(R.color.safeBox_color1)
    val safeBoxColor2 = resources.getColor(R.color.safeBox_color2)
    val closeBoxColor = resources.getColor(R.color.closeBox_color)
    val numberColor = resources.getColor(R.color.number_color)
    private val w = width
    val backgroundPaint = Paint()


    init {
        textPaint.textSize = w / 20f
        textPaint.color = Color.BLACK
        timeLeft = 10.0
    }


    fun timeFLoading(elapsedTimeMS: Double) {
        val interval = elapsedTimeMS / 1000.0
        timeLeft -= interval

        if (timeLeft <= 0.0) {
            timeLeft = 0.0
            /*gameOver = true
            drawing = false
            showGameOverDialog(R.string.lose)*/
        }
    }

    /*
    fun increaseTimeLeft() {
        if (gameDifficulty >= 30) { //définit le reward en cas de touche de case safe en fct de la difficulté
            HIT_REWARD = HIT_REWARD_EASY
        }
        else HIT_REWARD = HIT_REWARD_HARD
        timeLeft += HIT_REWARD
    }*/


    fun showGameOverDialog(messageId: Int) {
        class GameResult : DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(resources.getString(messageId))
                builder.setMessage(
                    resources.getString(
                        R.string.results_format, discoveredBoxes //totalElapsedTime
                    )
                )
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        theBoxes.forEach { it.draw(canvas) }
    }

    //création des boxes
    fun boxCreation() {
        (1..nbrBoxesWidth).forEach { x ->
            (1..nbrBoxesHeight).forEach { y ->

                lateinit var box: Box
                var aléatoire = random.nextInt(nbrBoxesHeight * nbrBoxesWidth)

                if (aléatoire <= gameDifficulty) {
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

    fun newGame() {
        boxCreation()
        discoveredBoxes = 0
        if (gameOver) {
            gameOver = false
        }
    }

    fun gameWin() {
        showGameOverDialog(R.string.win)
        gameOver = true
    }

    fun gameLost() {
        showGameOverDialog(R.string.lose)
        gameOver = true
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (gameOver == false) {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    //eventOnField : position du clic sur le field
                    val eventOnField =
                        Point(
                            (e.rawX / boxSize).toInt(),
                            ((e.rawY - pixelsTopBar) / boxSize).toInt()
                        )
                    //repere la case sous le clic
                    var boxUnderEvent = theBoxes.single { it.fieldPosition == eventOnField }
                    if (firstClick) { //fait en sorte que le premier clic soit toujours sur une case safe
                        firstClick = false
                        while (!boxUnderEvent.isSafe) {
                            theBombs.clear()
                            theEmptyBoxes.clear()
                            theBoxes.clear()
                            boxCreation()
                            theBombs.forEach { it.warningBomb(theEmptyBoxes) }
                            boxUnderEvent = theBoxes.single { it.fieldPosition == eventOnField }
                        }
                    }

                    if (plantFlag) {
                        if (boxUnderEvent.plantFlag) {
                            boxUnderEvent.plantFlag = false
                        } else {
                            boxUnderEvent.plantFlag = true
                        }
                    } else {
                        boxUnderEvent.hide = false
                        if (boxUnderEvent.isSafe) {
                            val emptyBox: EmptyBox =
                                boxUnderEvent.invoke()   //change la classe de l'objet de Box à EmptyBox pour utiliser cleanField()
                            emptyBox.cleanField() //devoile toute la partie safe autours de la case
                            emptyBox.showAround()
                        }
                    }

                    invalidate() //appel à la méthode onDraw
                }
            }

        }
        return true
    }

}



