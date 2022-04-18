package be.julien.info_h20_demineur


import android.content.res.Resources
import android.graphics.*
import android.os.Parcelable
import kotlinx.android.synthetic.main.*

//classe abstraite car elle ne se manifestera qu'à travers ses sous-classes
open class Box(val fieldPosition: android.graphics.Point, val size : Float, var view: FieldView) {


    val area = RectF(fieldPosition.x * size , fieldPosition.y * size, (fieldPosition.x + 1) * size,
        (fieldPosition.y + 1) * size)
    val hiddenBoxPaint = Paint()
    val gridSize = 7
    val gridPaint = Paint()
    val areaWithGrid = RectF(area.left + gridSize, area.top + gridSize, area.right - gridSize,
        area.bottom - gridSize)

    init {
        hiddenBoxPaint.color = Color.RED
        gridPaint.color = Color.WHITE
    }

    fun DrawHidden(canvas: Canvas?) {
        canvas?.drawRect(area, gridPaint) //dessin de la grille autours de la case
        canvas?.drawRect(areaWithGrid, hiddenBoxPaint) // dessin de la case
    }

    open fun DrawDiscover(canvas: Canvas?) {
        canvas?.drawRect(area, gridPaint) //dessin de la grille autours de la case
    }




}
