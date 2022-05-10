package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint


import android.graphics.Point
import android.view.SurfaceHolder
import androidx.core.graphics.toRect


//la classe Bomb hérite de la classe Box
class Bomb(fieldPosition: Point, view: FieldView): Box(fieldPosition, view) {

    val animPaint = Paint()
    var animColor = false


    init {
        paint.color = Color.TRANSPARENT //sinon l'animation ne marche pas car onDraw() dessine au dessus de draw()
        devPaint.color = view.devBombColor
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (!hide ) {
            canvas?.drawRect(area, paint)
            view.imageBomb.setBounds(area.toRect())
            view.imageBomb.draw(canvas!!)
            if (!view.ending) {view.gameLost()}
        }
        else {
            if (view.devMode) {
                canvas?.drawRect(area, devPaint) // dessin de la case
            }
        }
    }

    fun setAnimColor() {
        animColor = true
        animPaint.color = view.bombColor1
        paint.color = view.bombColor2
    }

    fun anim(canvas: Canvas?) {
        canvas?.drawRect(area, animPaint)
        view.imageBomb.setBounds(area.toRect())
        view.imageBomb.draw(canvas!!)
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


