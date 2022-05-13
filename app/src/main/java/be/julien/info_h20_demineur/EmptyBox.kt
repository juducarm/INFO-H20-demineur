package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.os.Build
import androidx.annotation.RequiresApi

//la classe EmptyBox hérite de la classe Box
class EmptyBox(fieldPosition: Point, view: FieldView):
    Box(fieldPosition, view) {

    private val numberPaint = Paint()
    private var bombsAround = 0
    private var cleaned = false //permet d'éviter que cleanfield tourne en boucle infinie

    init {
        if ((fieldPosition.x + fieldPosition.y ) % 2 == 0) { //permet d'avoir un quadrillage
            paint.color = view.safeBoxColor1
        }
        else {
            paint.color = view.safeBoxColor2
        }
        numberPaint.color = view.numberColor
        numberPaint.textSize = view.boxSize
        devPaint.color = view.devSafeColor

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (!hide) {
            view.theFlags.removeIf { it.fieldPosition == fieldPosition }
            canvas?.drawRect(area, paint) // dessin de la case
            if (bombsAround != 0) {
                canvas?.drawText(
                    "$bombsAround", // dessin du nombre
                    area.left + (view.boxSize / 4),
                    area.bottom - (view.boxSize / 10),
                    numberPaint
                )
            }
        }
        else {
            if (view.devMode) {
                canvas?.drawRect(area, devPaint) // dessin de la case cachée
            }
        }
    }

    private fun showAround(theEmptyBoxes: ArrayList<EmptyBox>, theDiscoveredBoxes: ArrayList<Box>) { //dévoile les 8 cases entourant la case
        aroundList.forEach { point -> //aroundList est définie dans la classe Box
            val fieldAround = Point(point.x + fieldPosition.x, point.y + fieldPosition.y)
            if (theEmptyBoxes.any { it.fieldPosition == fieldAround}) {//verifie si la case n'est pas hors du field
                val boxAround = theEmptyBoxes.single { it.fieldPosition == fieldAround } // récupère l'objet case
                boxAround.discover()
                if (!theDiscoveredBoxes.contains(boxAround)) theDiscoveredBoxes.add(boxAround)
                view.winCondition()
            }
        }
    }

    fun cleanField(theEmptyBoxes: ArrayList<EmptyBox>, theDiscoveredBoxes: ArrayList<Box>) { //dévoile toutes les cases vides au contact indirect d'une case safe
        cleaned = true
        showAround(theEmptyBoxes, theDiscoveredBoxes)
        aroundList.forEach { point ->
            val fieldAround = Point(point.x + fieldPosition.x, point.y + fieldPosition.y)
            if (theEmptyBoxes.any { it.fieldPosition == fieldAround}) {//verifie si la case n'est pas hors du field
                val boxAround = theEmptyBoxes.single { it.fieldPosition == fieldAround } //recupère l'objet case
                if (boxAround.isSafe && !boxAround.cleaned) {
                    if (!theDiscoveredBoxes.contains(boxAround)) {
                        theDiscoveredBoxes.add(boxAround)
                    }
                    view.winCondition()
                    boxAround.showAround(theEmptyBoxes, theDiscoveredBoxes)
                    boxAround.cleanField(theEmptyBoxes, theDiscoveredBoxes)
                }
            }
        }

    }

    fun carefullBomb() {
        isSafe = false
        bombsAround++
        paint.color = view.closeBoxColor
        devPaint.color = view.devCloseColor
    }




}


