package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point

//la classe EmptyBox hérite de la classe Box
class EmptyBox(fieldPosition: android.graphics.Point, view: FieldView):
    Box(fieldPosition, view) {

    val boxPaint = Paint()
    val numberPaint = Paint()
    var bombsAround = 0
    var cleaned = false


    init {
        if ((fieldPosition.x + fieldPosition.y ) % 2 == 0) {
            boxPaint.color = view.safeBoxColor1
        }
        else {boxPaint.color = view.safeBoxColor2}
        numberPaint.color = view.numberColor
        numberPaint.textSize = view.boxSize
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (!hide) {
            canvas?.drawRect(areaWithGrid, boxPaint) // dessin de la case
            if (bombsAround != 0) {
                canvas?.drawText(
                    "$bombsAround", // dessin du nombre
                    area.left + (view.boxSize / 4),
                    area.bottom - (view.boxSize / 10),
                    numberPaint
                )
            }

        }
    }

    fun showAround() { //dévoile les 8 cases entourant la case
        aroundList.forEach { point -> //aroundList est définie dans la classe Box
            val fieldAround = Point(point.x + fieldPosition.x, point.y + fieldPosition.y)
            if (view.theEmptyBoxes.any { it.fieldPosition == fieldAround}) {//verifie si la case n'est pas hors du field
                val boxAround = view.theEmptyBoxes.single { it.fieldPosition == fieldAround } // récupère l'objet case
                boxAround.hide = false
                //if (!view.discoverdBoxes.contains(boxAround)) view.discoverdBoxes.add(boxAround)
            }
        }
    }

    fun cleanField() { //dévoile toutes les cases vides au contact indirect d'une case safe
        cleaned = true
        aroundList.forEach { point ->
            val fieldAround = Point(point.x + fieldPosition.x, point.y + fieldPosition.y)
            if (view.theEmptyBoxes.any { it.fieldPosition == fieldAround}) {//verifie si la case n'est pas hors du field
                val boxAround = view.theEmptyBoxes.single { it.fieldPosition == fieldAround } //recupère l'objet case
                if (boxAround.isSafe && !boxAround.cleaned) {
                    boxAround.showAround()
                    boxAround.cleanField()
                }
            }
        }

    }




}

