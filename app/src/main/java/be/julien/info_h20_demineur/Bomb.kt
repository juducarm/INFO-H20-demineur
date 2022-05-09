package be.julien.info_h20_demineur

import android.graphics.Canvas


import android.graphics.Point


//la classe Bomb hérite de la classe Box
class Bomb(fieldPosition: Point, view: FieldView):
    Box(fieldPosition, view) {


    init {
        paint.color = view.bombColor
        devPaint.color = view.devBombColor
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (!hide ) {
            canvas?.drawRect(area, paint) // dessin de la case
            view.imageBomb.setBounds(area.left.toInt(), area.top.toInt(),
                area.right.toInt(), area.bottom.toInt())
            if (canvas != null) {
                view.imageBomb.draw(canvas)
            }
            if (view.drawing) {view.gameLost()}
        }
        else {
            if (view.devMode) {
                canvas?.drawRect(area, devPaint) // dessin de la case
            }
        }
    }


    //envoie sa présence à chaque EmptyBox autours d'elle
   fun warningBomb(theEmptyBoxes: ArrayList<EmptyBox>) {

        aroundList.forEach { point ->
            val fieldAround = Point(point.x + fieldPosition.x, point.y + fieldPosition.y)
            if (theEmptyBoxes.any { it.fieldPosition == fieldAround }) { //vérifie si il ya une EmptyBox sur la case
                val boxAround = theEmptyBoxes.single { it.fieldPosition == fieldAround }
                boxAround.carefullBomb()
            }
        }
   }


}


