package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

//la classe EmptyBox hérite de la classe Box
class EmptyBox(fieldPosition: android.graphics.Point, view: FieldView):
    Box(fieldPosition, view) {

    val paint = Paint()
    var bombsAround = 0


    init {
        paint.color = Color.GREEN
    }

    override fun Draw(canvas: Canvas?) {
        super.Draw(canvas)
        if (!hide) {
            canvas?.drawRect(areaWithGrid, paint) // dessin de la case
            println("DrawDiscover / bombes : $bombsAround")
        }
    }


}

