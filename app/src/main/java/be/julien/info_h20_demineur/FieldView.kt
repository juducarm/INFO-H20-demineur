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
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.util.SparseIntArray
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList



class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr) , SurfaceHolder.Callback{


    //réglages du jeu
    var nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth_HARD)
    var nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight_HARD)
    var nbrBombs = resources.getInteger(R.integer.nbrBombs_HARD)
    

    //réglages graphiques ( tout est dans le fieldView pour avoir accès au getResources() )
    val xRes = resources.getInteger(R.integer.xResolution).toFloat()
    val yRes = resources.getInteger(R.integer.yResolution).toFloat()
    val pixelsTopBar =
        resources.getDimension(R.dimen.heightTopBar) + resources.getDimension(R.dimen.heightStatusBar)//hauteur en pixel de la TopBar
    var boxSize = minOf(xRes / nbrBoxesWidth, yRes / nbrBoxesHeight)
    val textPaint = Paint()
    val imageBomb = resources.getDrawable(R.drawable.ic_bomb)
    val imageFlag = resources.getDrawable(R.drawable.ic_flag)
    val bombColor = resources.getColor(R.color.bomb_color)
    val hiddenBoxColor1 = resources.getColor(R.color.hiddenBox_color1)
    val hiddenBoxColor2 = resources.getColor(R.color.hiddenBox_color2)
    val safeBoxColor1 = resources.getColor(R.color.safeBox_color1)
    val safeBoxColor2 = resources.getColor(R.color.safeBox_color2)
    val closeBoxColor = resources.getColor(R.color.closeBox_color)
    val numberColor = resources.getColor(R.color.number_color)


    //variables et valeurs pour le jeu
    val activity = context as FragmentActivity
    
    var flagWitness = "Off " //affiche si le mode drapeau est activé ou non
    var gameOver = false
    var random = Random()
    var flagModeOn = false
    var firstClick = true
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

    //variables et valeurs pour le timer
    val initialTime = resources.getInteger(R.integer.initial_time).toLong()
    val timerInterval = resources.getInteger(R.integer.timer_interval).toLong()
    var timer = Timer(initialTime, timerInterval, this)
    val textTimer = resources.getString(R.string.timeRemaining)
    
    var timeReward = resources.getInteger(R.integer.hit_reward_easy).toLong()
    var timeLeftOnGame = initialTime
    var totalElapsedTime = 0

    //sons
    val soundPool: SoundPool
    val soundMap: SparseIntArray


    init {
        textPaint.textSize = width / 20f
        textPaint.color = Color.BLACK

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundMap = SparseIntArray(10)
        soundMap.put(0, soundPool.load(context, R.raw.empty_box, 1))
        soundMap.put(1, soundPool.load(context, R.raw.win, 1))
        soundMap.put(2, soundPool.load(context, R.raw.lose, 1))
        soundMap.put(3, soundPool.load(context, R.raw.new_game, 1))
        soundMap.put(4, soundPool.load(context, R.raw.flag, 1))
        soundMap.put(6, soundPool.load(context, R.raw.show_around, 1))
        soundMap.put(7, soundPool.load(context, R.raw.tiktok, 1))
        soundMap.put(8, soundPool.load(context, R.raw.button, 1))

    }

    //création des boxes
    fun boxCreation() {
        (1..nbrBoxesWidth).forEach { x ->
            (1..nbrBoxesHeight).forEach { y ->

                lateinit var box: Box
                val randomiser = random.nextInt(nbrBoxesHeight * nbrBoxesWidth)

                if (randomiser <= nbrBombs.toFloat()) {
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

                //clickPosition : position du clic sur le champ de case
                val clickPosition = Point((e.rawX / boxSize).toInt(), ((e.rawY - pixelsTopBar) / boxSize).toInt())

                //repere la case sous le clic
                if (theBoxes.any { it.fieldPosition == clickPosition }) {//vérifie que le clic est sur le champ de case
                    var boxUnderClick = theBoxes.single { it.fieldPosition == clickPosition }

                    //fait en sorte que le premier clic soit toujours sur une case safe
                    if (firstClick) {
                        playEmptyBoxSound()
                        firstClick = false
                        while (!boxUnderClick.isSafe) {
                            boxUnderClick = cleanFirstClic(clickPosition)
                        }
                        boxUnderClick.invoke().cleanField()
                        boxUnderClick.invoke().showAround()
                        playShowAroundSound()
                    }
                    else {

                        if (flagModeOn) {
                            if (!theDiscoveredBoxes.any { it.fieldPosition == clickPosition }) {//vérifie que la case n'est pas déjà découverte
                                if (theFlags.any { it.fieldPosition == clickPosition }) {
                                    theFlags.removeIf { it.fieldPosition == clickPosition }
                                    playFlagInSound()
                                } else {
                                    theFlags.add(Flag(clickPosition, this))
                                    playFlagInSound()
                                }
                            }
                        }
                        else {
                            boxUnderClick.discover()
                            if (boxUnderClick.isSafe) {
                                boxUnderClick.invoke().showAround() //dévoile les cases voisines
                                boxUnderClick.invoke().cleanField() //devoile toute la partie safe autours de la case
                                playShowAroundSound()
                            }
                            if (!theDiscoveredBoxes.contains(boxUnderClick) && !theBombs.contains(boxUnderClick)) {
                                theDiscoveredBoxes.add(boxUnderClick)
                                timeBonus(timeReward, timeLeftOnGame)
                                playEmptyBoxSound()
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
        playButtonSound()
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

    //gestion du dessin (ressine tout le plan du jeu à chaque modif)
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
                builder.setMessage(resources.getString(R.string.results_format, theDiscoveredBoxes.size, (timeLeftOnGame/1000).toInt(), totalElapsedTime))
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
        if (theDiscoveredBoxes.size == theEmptyBoxes.size) {
            gameWon()
        }
    }

    fun newGame() {
        playNewGameSound()
        totalElapsedTime = 0
        timeLeftOnGame = initialTime
        flagModeOn = false
        firstClick = true
        drawing = true
        gameOver = false
        theLists.forEach { it.clear() }
        boxCreation()
        theBombs.forEach { it.warningBomb(theEmptyBoxes) }
        invalidate()
        timer.start()
    }

    fun gameWon() {
        playWinSound()
        drawing = false
        showGameOverDialog(R.string.win)
        timer.cancel()
    }

    fun gameLost() {
        playLoseSound()
        theBombs.forEach { it.hide = false } //nombre de boxes qui ont été découvertes pdt les partie
        showGameOverDialog(R.string.lose)
        drawing = false
        textViewTimer.text =  " "
        invalidate()
        timer.cancel()
    }

    fun pause() {
        drawing = false
    }

    fun resume() {
        timer.start()
        activity.timeBarView.timeMax = initialTime
        drawing = true

    }

    //gestion du timer
    fun displayTimer(timeLeft: Long) {
        if (drawing) {
            timeLeftOnGame = timeLeft
            textViewTimer.text = textTimer + " " + (timeLeft/1000).toString()
        }
        else timer.cancel()
    }

    fun timeBonus(timeReward: Long, timeLeft: Long) {
        timer.cancel()
        println(timeReward)
        timer = Timer( timeLeft + timeReward, timerInterval, this)
        timer.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {}

    fun countElapsedTime() {
        totalElapsedTime ++
    }

    fun playEmptyBoxSound() {
        soundPool.play(soundMap.get(0), 1f, 1f, 1, 0, 1f)
    }
    fun playLoseSound() {
        soundPool.play(soundMap.get(2), 1f, 1f, 1, 0, 1f)
    }
    fun playWinSound() {
        soundPool.play(soundMap.get(1), 1f, 1f, 1, 0, 1f)
    }
    fun playFlagInSound() {
        soundPool.play(soundMap.get(4), 1f, 1f, 1, 0, 1f)
    }
    fun playNewGameSound(){
        soundPool.play(soundMap.get(3), 1f, 1f, 1, 0, 1f)
    }
    fun playShowAroundSound(){
        soundPool.play(soundMap.get(6), 1f, 1f, 1, 0, 1f)
    }

    fun playButtonSound(){
        soundPool.play(soundMap.get(8), 1f, 1f, 0, 0, 3f)
    }


}





