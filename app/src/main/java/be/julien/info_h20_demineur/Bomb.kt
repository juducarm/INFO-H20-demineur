package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.CountDownTimer


//la classe Bomb hérite de la classe Box
class Bomb(fieldPosition: Point, view: FieldView):
    Box(fieldPosition, view) {

    val paint = Paint()

    init {
        paint.color = Color.BLACK
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (!hide) {
            showAllBombs(view.theBombs)
           // canvas?.drawRect(areaWithGrid, paint) // dessin de la case
            view.imageBomb.setBounds(areaWithGrid.left.toInt(), areaWithGrid.top.toInt(),
                areaWithGrid.right.toInt(), areaWithGrid.bottom.toInt())
            if (canvas != null) {
                view.imageBomb.draw(canvas)
            }

        }
    }

    fun showAllBombs(theBombs: ArrayList<Bomb>) {
        theBombs.forEach {
            it.hide = false
        }

    }

    //envoie sa présence à chaque EmptyBox autours d'elle
   fun warningBomb(theEmptyBoxes: ArrayList<EmptyBox>) {

        aroundList.forEach { point ->
            val fieldAround = Point(point.x + fieldPosition.x, point.y + fieldPosition.y)
            if (theEmptyBoxes.any { it.fieldPosition == fieldAround }) { //vérifie si il ya une EmptyBox sur la case
                val boxAround = theEmptyBoxes.single { it.fieldPosition == fieldAround }
                boxAround.isSafe = false //la case n'est pas safe
                boxAround.bombsAround++
                boxAround.boxPaint.color = Color.BLUE
            }
        }


   }


}
