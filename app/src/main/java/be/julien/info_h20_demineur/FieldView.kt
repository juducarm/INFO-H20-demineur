package be.julien.info_h20_demineur

import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import android.content.Context
import android.graphics.*
import java.util.*
import kotlin.collections.ArrayList


class FieldView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
    SurfaceView(context, attributes,defStyleAttr) {

    var random = Random()
    var plantFlag = false
    var firstClick = true

    //listes d'objets
    val theBoxes = ArrayList<Box>()
    val theBombs = ArrayList<Bomb>()
    val theEmptyBoxes = ArrayList<EmptyBox>()

    //réglages du jeu
    var nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth)
    var nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight)
    var nbrBombs = resources.getInteger(R.integer.nbrBombs)
    val resolution = PointF(1080f, 1920f) //nombre de pixels sur le fragment
    val pixelsTopBar = resources.getDimension(R.dimen.heightTopBar) //hauteur en pixel de la TopBar (Float)
    val boxSize = minOf(resolution.x / nbrBoxesWidth, resolution.y / nbrBoxesHeight)
    val gridSize = resources.getInteger(R.integer.gridSize)
    val gameDifficulty = nbrBombs.toFloat()


    //réglages graphiques
    val imageBomb = resources.getDrawable(R.drawable.ic_bomb)
    val imageFlag = resources.getDrawable(R.drawable.ic_flag)
    val hiddenBoxColor1 = resources.getColor(R.color.hiddenBox_color1)
    val hiddenBoxColor2= resources.getColor(R.color.hiddenBox_color2)
    val safeBoxColor1 = resources.getColor(R.color.safeBox_color1)
    val safeBoxColor2 = resources.getColor(R.color.safeBox_color2)
    val closeBoxColor = resources.getColor(R.color.closeBox_color)
    val numberColor = resources.getColor(R.color.number_color)

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
                }
                else {
                    box = EmptyBox(Point(x - 1, y - 1), this)
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
                    if (boxUnderEvent.plantFlag) {boxUnderEvent.plantFlag = false}
                    else {boxUnderEvent.plantFlag = true}
                }
                else {
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
        return true
    }




}



