package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point

//la classe Bomb hérite de la classe Box
class Bomb(fieldPosition: android.graphics.Point, size : Float, view: FieldView):
    Box(fieldPosition,size, view) {

    val paint = Paint()

    init {
        paint.color = Color.BLACK
    }


    override fun DrawDiscover(canvas: Canvas?) {
        super.DrawDiscover(canvas)
        canvas?.drawRect(areaWithGrid, paint) // dessin de la case
        println("DrawDiscover / bombes : BOOM")
    }

    //envoie sa présence à chaque EmptyBox autours d'elle
   fun warningBomb(theEmptyBoxes: ArrayList<EmptyBox>) {

        //liste permettant d'accéder aux cases autours
        val AroundList = listOf(
            Point(-1, -1), Point(-1, 0), Point(-1, 1),
            Point(1, -1), Point(1, 0), Point(1, 1),
            Point(0, 1), Point(0, -1)
        )

        //envoie du message
        AroundList.forEach { point ->
            val fieldAround = Point(point.x + fieldPosition.x, point.y + fieldPosition.y)
            if (theEmptyBoxes.any { it.fieldPosition.equals(fieldAround) }) { //vérifie si il ya une EmptyBox sur la case
                val BoxAround = theEmptyBoxes.filter { it.fieldPosition.equals(fieldAround) }.single()
                BoxAround.bombsAround++
                BoxAround.paint.color = Color.BLUE
                }
        }


    }

}
