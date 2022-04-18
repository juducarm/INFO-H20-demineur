package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

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
    }

    fun warningBomb() {

    }
}
