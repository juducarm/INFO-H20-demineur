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
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.util.SparseIntArray
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList



class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr), SurfaceHolder.Callback, DrawAnimation {


    //mode developpeur (affiche le type des cases lorsqu'elles sont cachées pour pouvoir faire des tests)
    var devMode = false
    val devSafeColor = resources.getColor(R.color.dev_safe1)
    val devBombColor = resources.getColor(R.color.dev_bomb)
    val devCloseColor = resources.getColor(R.color.dev_close1)


    //réglages du jeu
    private var nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth_EZ )
    private var nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight_EZ)
    private var nbrBombs = resources.getInteger(R.integer.nbrBombs_EZ)

    //réglages graphiques ( tout est dans le fieldView pour avoir accès au getResources() )
    private val xRes = resources.getInteger(R.integer.xResolution).toFloat()
    private val yRes = resources.getInteger(R.integer.yResolution).toFloat()
    private val pixelsTopBar =
        resources.getDimension(R.dimen.heightTopBar) + resources.getDimension(R.dimen.heightStatusBar)//hauteur en pixel de la TopBar
    var boxSize = minOf(xRes / nbrBoxesWidth, yRes / nbrBoxesHeight)
    private val textPaint = Paint()
    private val backgroundPaint = Paint()
    val imageBomb = resources.getDrawable(R.drawable.ic_bomb)
    val imageFlag = resources.getDrawable(R.drawable.ic_flag)
    val bombColor1 = resources.getColor(R.color.bomb_color1)
    val bombColor2 = resources.getColor(R.color.bomb_color2)
    val hiddenBoxColor1 = resources.getColor(R.color.hiddenBox_color1)
    val hiddenBoxColor2 = resources.getColor(R.color.hiddenBox_color2)
    val safeBoxColor1 = resources.getColor(R.color.safeBox_color1)
    val safeBoxColor2 = resources.getColor(R.color.safeBox_color2)
    val closeBoxColor = resources.getColor(R.color.closeBox_color)
    val numberColor = resources.getColor(R.color.number_color)


    //variables et valeurs pour le jeu
    val activity = context as FragmentActivity
    private var flagWitness = "Off " //affiche si le mode drapeau est activé ou non
    private var random = Random()
    private var flagModeOn = false
    private var firstClick = true
    var playing = true
    var ending = false
    lateinit var textViewFlag: com.google.android.material.textview.MaterialTextView
    lateinit var textViewTimer: com.google.android.material.textview.MaterialTextView

    //listes d'objets
    private val theBoxes = ArrayList<Box>()
    val theBombs = ArrayList<Bomb>()
    val theEmptyBoxes = ArrayList<EmptyBox>()
    val theDiscoveredBoxes = ArrayList<Box>()
    val theFlags = ArrayList<Flag>()
    private val theLists = listOf(theBombs, theBoxes, theDiscoveredBoxes, theEmptyBoxes, theFlags)

    //variables et valeurs pour le timer
    private val initialTime = resources.getInteger(R.integer.initial_time).toLong()
    private val timerInterval = resources.getInteger(R.integer.timer_interval).toLong()
    private var timeReward = resources.getInteger(R.integer.hit_reward_easy).toLong()
    private var textTimer = resources.getString(R.string.timeRemaining)
    var timerInGame = TimerInGame(initialTime, timerInterval, this)
    var timeLeftOnGame = initialTime
    var totalElapsedTime = 0

    //sons
    private val soundPool: SoundPool
    private val soundMap: SparseIntArray


    init {
        textPaint.textSize = width / 20f
        textPaint.color = Color.BLACK
        backgroundPaint.color = hiddenBoxColor2

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

    fun changeDevMode(devModeOn: Boolean) {
        devMode = devModeOn
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

                            if (!theDiscoveredBoxes.contains(boxUnderClick) && !theBombs.contains(boxUnderClick)) {
                                theDiscoveredBoxes.add(boxUnderClick)
                                timeBonus(timeReward, timeLeftOnGame)
                                playEmptyBoxSound()
                                winCondition()
                            }
                            if (boxUnderClick.isSafe) {
                                boxUnderClick.invoke().showAround() //dévoile les cases voisines
                                boxUnderClick.invoke().cleanField() //devoile toute la partie safe autours de la case
                                playShowAroundSound()
                            }
                        }
                    }
                    if (playing) invalidate() //appel à la méthode onDraw
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
        if (playing) {
            println("onDraw de fils deup $playing")
            canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
            theBoxes.forEach { it.draw(canvas) }
            theFlags.forEach { it.draw(canvas) }

            if (ending) {
                prepareAnimation()
                playing = false
            }
        }
    }


    //fait en sorte que le premier clique soit toujours sur une case safe
    private fun cleanFirstClic(position: Point): Box{
        theLists.forEach { it.clear() }
        boxCreation()
        theBombs.forEach { it.warningBomb(theEmptyBoxes) }
        textViewFlag.text = flagWitness + theBombs.size.toString()
        return theBoxes.single { it.fieldPosition == position }
    }


    //gestion de fin du jeu
    private fun showGameOverDialog(messageId: Int) {

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
        onDrawOff = false
        ending = false
        timeLeftOnGame = initialTime
        flagModeOn = false
        firstClick = true
        playing = true
        backgroundPaint.color = hiddenBoxColor2
        timerAnimation.cancel()
        totalElapsedTime = 0
        activity.timeBarView.timeMax = timeLeftOnGame
        theLists.forEach { it.clear() }
        boxCreation()
        theBombs.forEach { it.warningBomb(theEmptyBoxes) }
        invalidate()
        setNewTimer()
    }

    private fun gameWon() {
        timerInGame.cancel()
        playWinSound()
        playing = false
        showGameOverDialog(R.string.win)
    }

    fun gameLost() {
        backgroundPaint.color = Color.TRANSPARENT
        timerInGame.cancel()
        playLoseSound()
        theBombs.forEach { it.hide = false }
        showGameOverDialog(R.string.lose)
        textViewTimer.text =  " "
        ending = true
        invalidate()

    }


    //gestion de la difficulté
    fun goToEasyMode() {
        nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth_EZ)
        nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight_EZ)
        boxSize = minOf(xRes / nbrBoxesWidth, yRes / nbrBoxesHeight)
        nbrBombs = resources.getInteger(R.integer.nbrBombs_EZ)
        timeReward = resources.getInteger(R.integer.hit_reward_easy).toLong()
    }

    fun goToHardMode() {
        nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth_HARD)
        nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight_HARD)
        boxSize = minOf(xRes / nbrBoxesWidth, yRes / nbrBoxesHeight)
        nbrBombs = resources.getInteger(R.integer.nbrBombs_HARD)
        timeReward = resources.getInteger(R.integer.hit_reward_hard).toLong()
    }


    //gestion du timer
    fun displayTimer(timeLeft: Long) {
        timeLeftOnGame = timeLeft
        textViewTimer.text = textTimer + " " + (timeLeft/1000).toString()
    }

    private fun timeBonus(timeReward: Long, timeLeft: Long) {
        setNewTimer(timeLeft + timeReward)
    }

    fun setNewTimer(timeLeft: Long = timeLeftOnGame) {
        timerInGame.cancel()
        timerInGame = TimerInGame(timeLeft, timerInterval, this)
        timerInGame.start()
    }

    fun countElapsedTime() {
        totalElapsedTime ++
    }



    //gestion des sons
    private fun playEmptyBoxSound() {
        soundPool.play(soundMap.get(0), 1f, 1f, 1, 0, 1f)
    }
    private fun playLoseSound() {
        soundPool.play(soundMap.get(2), 1f, 1f, 1, 0, 1f)
    }
    private fun playWinSound() {
        soundPool.play(soundMap.get(1), 1f, 1f, 1, 0, 1f)
    }
    private fun playFlagInSound() {
        soundPool.play(soundMap.get(4), 1f, 1f, 1, 0, 1f)
    }
    private fun playNewGameSound(){
        soundPool.play(soundMap.get(3), 1f, 1f, 1, 0, 1f)
    }
    private fun playShowAroundSound(){
        soundPool.play(soundMap.get(6), 1f, 1f, 1, 0, 1f)
    }
    private fun playButtonSound(){
        soundPool.play(soundMap.get(8), 1f, 1f, 0, 0, 3f)
    }

    var bombsOn = true
    val animInterval = resources.getInteger(R.integer.interval_anim_bombs).toLong()
    lateinit var canvas: Canvas
    lateinit var timerAnimation: TimerAnimation
    var onDrawOff = false

    fun prepareAnimation() {
        theBombs.forEach { it.setAnimColor() }
        timerAnimation = TimerAnimation(0L, animInterval, this)
        timerAnimation.start()
        onDrawOff = true
    }

    override fun drawAnim() {
        super.drawAnim()
        bombsOn = !bombsOn
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            theBoxes.forEach { it.draw(canvas) }
            if (bombsOn) theBombs.forEach { it.draw(canvas) }
            else theBombs.forEach { it.anim(canvas) }
            holder.unlockCanvasAndPost(canvas)
        }
        timerAnimation.cancel()
        timerAnimation = TimerAnimation(animInterval, animInterval, this)
        timerAnimation.start()
    }
/*
    fun draw() {
        bombsOn = !bombsOn
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            theBoxes.forEach { it.draw(canvas) }
            if (bombsOn) theBombs.forEach { it.draw(canvas) }
            else theBombs.forEach { it.anim(canvas) }
            holder.unlockCanvasAndPost(canvas)
        }
        timerAnimation.cancel()
        timerAnimation = TimerAnimation(animInterval, animInterval, this)
        timerAnimation.start()
    }
 */


    override fun surfaceCreated(p0: SurfaceHolder) {}

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {}


}





