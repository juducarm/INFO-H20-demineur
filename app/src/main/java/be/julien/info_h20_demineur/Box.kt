package be.julien.info_h20_demineur


import android.graphics.*


//classe abstraite car elle ne se manifestera qu'à travers ses sous-classes
open class Box(val fieldPosition: android.graphics.Point, var view: FieldView) {

    var hide = true
    val size = view.boxSize
    val area = RectF(fieldPosition.x * size , fieldPosition.y * size, (fieldPosition.x + 1) * size,
        (fieldPosition.y + 1) * size)
    val hiddenBoxPaint = Paint()
    val gridSize = view.gridSize
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
            //canvas?.drawRect(area, gridPaint) //dessin de la grille autours de la case
        }
    open fun Draw(canvas: Canvas?) {
        canvas?.drawRect(area, gridPaint) //dessin de la grille autours de la case
        if (hide) {
            canvas?.drawRect(areaWithGrid, hiddenBoxPaint) // dessin de la case cachée
        }
        else {}
    }
}