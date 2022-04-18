package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point

//la classe Bomb hérite de la classe Box
class Bomb(fieldPosition: Point, view: FieldView):
    Box(fieldPosition, view) {

    val paint = Paint()

    init {
        paint.color = Color.BLACK
    }

    override fun Draw(canvas: Canvas?) {
        super.Draw(canvas)
        if (!hide) {
            canvas?.drawRect(areaWithGrid, paint) // dessin de la case
        }
    }

    //envoie sa présence à chaque EmptyBox autours d'elle
   fun warningBomb(theEmptyBoxes: ArrayList<EmptyBox>) {

        //liste permettant d'accéder aux cases autours
        val aroundList = listOf(
            Point(-1, -1), Point(-1, 0), Point(-1, 1),
            Point(1, -1), Point(1, 0), Point(1, 1),
            Point(0, 1), Point(0, -1)
        )

        //envoie du message
        aroundList.forEach { point ->
            val fieldAround = Point(point.x + fieldPosition.x, point.y + fieldPosition.y)
            if (theEmptyBoxes.any { it.fieldPosition == fieldAround }) { //vérifie si il ya une EmptyBox sur la case
                val boxAround = theEmptyBoxes.single { it.fieldPosition == fieldAround }
                boxAround.bombsAround++
                boxAround.paint.color = Color.BLUE
                }
        }


    }

}
