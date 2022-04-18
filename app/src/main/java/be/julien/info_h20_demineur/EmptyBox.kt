package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point

//la classe EmptyBox hérite de la classe Box
class EmptyBox(fieldPosition: android.graphics.Point, view: FieldView):
    Box(fieldPosition, view) {

    val paint = Paint()
    var bombsAround = 0


    init {
        paint.color = Color.GREEN
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (!hide) {
            canvas?.drawRect(areaWithGrid, paint) // dessin de la case
            if (bombsAround == 0) {showAround()}
        }
    }

    fun showAround() {
        aroundList.forEach { point -> //aroundList est définie dans la classe Box
            val fieldAround = Point(point.x + fieldPosition.x, point.y + fieldPosition.y)
            if (view.theEmptyBoxes.any { it.fieldPosition == fieldAround}) {//verifie si la case n'est pas hors du field
                val boxAround = view.theEmptyBoxes.single { it.fieldPosition == fieldAround }
                boxAround.hide = false
            }
        }
    }


}

