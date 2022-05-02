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
import android.view.SurfaceHolder
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.collections.ArrayList
import android.os.CountDownTimer as CountDownTimer


class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr) , SurfaceHolder.Callback, Runnable {

    //variables et valeurs pour le temps
    var totalElapsedTime = 0.0
    var timeLeft = 100.0 //Pour indiquer le temps dans la fenetre de dialogue finale
    val HIT_REWARD_HARD = resources.getInteger(R.integer.hit_reward_hard).toLong() //Car getDouble ne fonctionne pas
    val HIT_REWARD_EASY = resources.getInteger(R.integer.hit_reward_easy).toLong()  //bonus du mode facile
    var HIT_REWARD: Long = 0

    //réglages du jeu
    val nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth)
    val nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight)
    val nbrBombs = resources.getInteger(R.integer.nbrBombs)
    val resolution = PointF(1080f, 1920f) //nombre de pixels sur le fragment
    val pixelsTopBar =
        resources.getDimension(R.dimen.heightTopBar) + resources.getDimension(R.dimen.heightStatusBar)//hauteur en pixel de la TopBar (Float)
    val boxSize = minOf(resolution.x / nbrBoxesWidth, resolution.y / nbrBoxesHeight)
    val gameDifficulty = nbrBombs.toFloat()
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
    private val w = width
    val backgroundPaint = Paint()

    //variables et valeurs pour le jeu
    var gameOver = false
    var discoveredBoxes = 0
    var random = Random()
    var flagMode = false
    var firstClick = true
    val activity = context as FragmentActivity
    var drawing = true
    lateinit var thread: Thread
    lateinit var canvas: Canvas

    //listes d'objets
    val theBoxes = ArrayList<Box>()
    val theBombs = ArrayList<Bomb>()
    val theEmptyBoxes = ArrayList<EmptyBox>()
    val theDiscoveredBoxes = ArrayList<Box>()


    init {
        textPaint.textSize = w / 20f
        textPaint.color = Color.BLACK
    }

    //création des boxes
    fun boxCreation() {
        (1..nbrBoxesWidth).forEach { x ->
            (1..nbrBoxesHeight).forEach { y ->

                lateinit var box: Box
                val aléatoire = random.nextInt(nbrBoxesHeight * nbrBoxesWidth)

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

    //gestion du clic du joueur
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (drawing) {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    //clickPosition : position du clic sur le field
                    val clickPosition =
                        Point(
                            (e.rawX / boxSize).toInt(),
                            ((e.rawY - pixelsTopBar) / boxSize).toInt()
                        )
                    //repere la case sous le clic
                    var boxUnderClick = theBoxes.single { it.fieldPosition == clickPosition }
                    if (firstClick) { //fait en sorte que le premier clic soit toujours sur une case safe
                        firstClick = false
                        while (!boxUnderClick.isSafe) {
                            theBombs.clear()
                            theEmptyBoxes.clear()
                            theBoxes.clear()
                            boxCreation()
                            theBombs.forEach { it.warningBomb(theEmptyBoxes) }
                            boxUnderClick = theBoxes.single { it.fieldPosition == clickPosition }
                        }
                    }
                    if (flagMode) {
                        boxUnderClick.plantFlag()
                    }
                    else {
                        boxUnderClick.discover()
                        if (boxUnderClick.isSafe) {
                            val emptyBox: EmptyBox =
                                boxUnderClick.invoke()   //change la classe de l'objet de Box à EmptyBox pour utiliser cleanField()
                            emptyBox.cleanField() //devoile toute la partie safe autours de la case
                            emptyBox.showAround()
                        }
                        if (!theDiscoveredBoxes.contains(boxUnderClick)) {
                            theDiscoveredBoxes.add(boxUnderClick)
                        }
                    }
                    invalidate() //appel à la méthode onDraw
                }
            }

        }
        return true
    }

    //gestion du dessin
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        theBoxes.forEach { it.draw(canvas) }
    }

    fun draw() { //dessin du timing
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas.drawRect(0f, 0f, canvas.width.toFloat(),
                canvas.height.toFloat(), backgroundPaint)
            val formatted = String.format("%.2f", timeLeft)
            canvas.drawText("Il reste $formatted secondes. ",
                30f, 1400f, textPaint)
            println("dessin du temps")
            holder.unlockCanvasAndPost(canvas)
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

    /*fun increaseTimeLeft() {
        if (gameDifficulty >= 30) { //définit le reward en cas de touche de case safe en fct de la difficulté
            HIT_REWARD = HIT_REWARD_EASY
        }
        else HIT_REWARD = HIT_REWARD_HARD
    }
    */





    override fun run() {
        var previousFrameTime = System.currentTimeMillis()
        if (drawing) {
            val currentTime = System.currentTimeMillis()
            var elapsedTimeMS:Double=(currentTime-previousFrameTime).toDouble()
            totalElapsedTime += elapsedTimeMS / 1000.0
            updatePositions(elapsedTimeMS)
            draw()
            previousFrameTime = currentTime
        }
    }

    fun updatePositions(elapsedTimeMS: Double) {
        val interval = elapsedTimeMS / 1000.0
        timeLeft -= interval
        println(timeLeft)
        if (timeLeft <= 0.0) {
            timeLeft = 0.0
            gameLost()
        }
    }



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

    fun reset(){

    }

    fun winCondition(){
        if (theDiscoveredBoxes.size == (nbrBoxesHeight*nbrBoxesHeight - nbrBombs)) {
            gameWon()
        }
    }

    fun newGame() {

        discoveredBoxes = 0
        totalElapsedTime = 0.0
        timeLeft = 100.0
        flagMode = false
        firstClick = true
        drawing = true
        gameOver = false
        thread = Thread(this)
        thread.start()
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
        pause()
    }

    fun gameLost() {
        drawing = false
        discoveredBoxes = theDiscoveredBoxes.size - 1 //nombre de boxes qui ont été découvertes pdt les partie
        showGameOverDialog(R.string.lose)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}



}



