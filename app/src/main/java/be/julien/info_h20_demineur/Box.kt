package be.julien.info_h20_demineur


import android.graphics.*


//classe abstraite car elle ne se manifestera qu'à travers ses sous-classes
abstract class Box(val fieldPosition: android.graphics.Point, var view: FieldView) {

    val area = RectF(fieldPosition.x * view.boxSize,//surface couverte par la case
        fieldPosition.y * view.boxSize,
        (fieldPosition.x + 1) * view.boxSize,
        (fieldPosition.y + 1) * view.boxSize)
    val hiddenBoxPaint = Paint()
    val gridPaint = Paint()
    val areaWithGrid = RectF(area.left + view.gridSize, //surface couverte par la case sans la grill
        area.top + view.gridSize,
        area.right - view.gridSize,
        area.bottom - view.gridSize)
    val aroundList = listOf( //liste permettant d'accéder aux cases autours
        Point(-1, -1), Point(-1, 0), Point(-1, 1),
        Point(1, -1), Point(1, 0), Point(1, 1),
        Point(0, 1), Point(0, -1)
    )

    var hide = true
    var isSafe = true // est true si la case n'est pas une bomb et n'est pas entourée de bomb
    var plantFlag = false // dessine un drapeau si true


    init {
        if ((fieldPosition.x + fieldPosition.y ) % 2 == 0) {
            hiddenBoxPaint.color = view.hiddenBoxColor1
        }
        else {hiddenBoxPaint.color = view.hiddenBoxColor2}

        gridPaint.color = Color.WHITE
    }

    open fun draw(canvas: Canvas?) {
        if (hide) {
            canvas?.drawRect(area, gridPaint) //dessin de la grille autours de la case
            canvas?.drawRect(areaWithGrid, hiddenBoxPaint) // dessin de la case cachée
            if (plantFlag) {
                view.imageFlag.setBounds(areaWithGrid.left.toInt(), areaWithGrid.top.toInt(),
                    areaWithGrid.right.toInt(), areaWithGrid.bottom.toInt())
                if (canvas != null) {
                    view.imageFlag.draw(canvas)
                }
            }
        }
    }

    operator fun invoke(): EmptyBox { //permet de change la classe de l'objet de Box à EmptyBo
    return EmptyBox(fieldPosition, view)
    }
}