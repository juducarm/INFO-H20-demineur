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

    //listes d'objets
    val theBoxes = ArrayList<Box>()
    val theBombs = ArrayList<Bomb>()
    val theEmptyBoxes = ArrayList<EmptyBox>()
    val discoverdBoxes = ArrayList<Box>()

    //réglages du jeu
    val nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth)
    val nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight)
    val nbrBombs = resources.getInteger(R.integer.nbrBombs)
    val resolution = PointF(1080f, 1920f) //nombre de pixels sur le fragment
    val pixelsTopBar = resources.getDimension(R.dimen.heightTopBar) //hauteur en pixel de la TopBar (Float)
    val boxSize = minOf(resolution.x / nbrBoxesWidth, resolution.y / nbrBoxesHeight)
    val gridSize = resources.getInteger(R.integer.gridSize)
    val gameDifficulty = nbrBombs.toFloat()
    val hiddenBoxColor1 = resources.getColor(R.color.hiddenBox_color1)
    val hiddenBoxColor2= resources.getColor(R.color.hiddenBox_color2)

    //réglages graphiques
    val imageBomb = resources.getDrawable(R.drawable.ic_bomb)
    val imageFlag = resources.getDrawable(R.drawable.ic_flag)


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
            println("Case du clic : $eventOnField")
            print("Nombres de bombes : ")
            println(theBombs.size)
            //repere la case sous le clic
            val boxUnderEvent = theBoxes.single { it.fieldPosition == eventOnField }
            if (plantFlag) {
                if (boxUnderEvent.plantFlag) {boxUnderEvent.plantFlag = false}
                else {boxUnderEvent.plantFlag = true}
            }
            else {
                discoverdBoxes.add(boxUnderEvent)
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



