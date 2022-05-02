package be.julien.info_h20_demineur


import android.graphics.*


//classe abstraite car elle ne se manifestera qu'à travers ses sous-classes
abstract class Box(val fieldPosition: Point, var view: FieldView) {

    val area = RectF(fieldPosition.x * view.boxSize,//surface couverte par la case
        fieldPosition.y * view.boxSize,
        (fieldPosition.x + 1) * view.boxSize,
        (fieldPosition.y + 1) * view.boxSize)
    val hiddenBoxPaint = Paint()
    val aroundList = listOf( //liste permettant d'accéder aux cases autours
        Point(-1, -1), Point(-1, 0), Point(-1, 1),
        Point(1, -1), Point(1, 0), Point(1, 1),
        Point(0, 1), Point(0, -1)
    )

    open var hide = true
    var isSafe = true // est true si la case n'est pas une bomb et n'est pas entourée de bomb
    var flagMode = false // dessine un drapeau si true


    init {
        if ((fieldPosition.x + fieldPosition.y ) % 2 == 0) { //permet d'avoir un quadrillage
            hiddenBoxPaint.color = view.hiddenBoxColor1
        }
        else {hiddenBoxPaint.color = view.hiddenBoxColor2}
    }

    open fun draw(canvas: Canvas?) {
        if (hide) {
            canvas?.drawRect(area, hiddenBoxPaint) // dessin de la case cachée
            if (flagMode) {
                view.imageFlag.setBounds(area.left.toInt(), area.top.toInt(),
                    area.right.toInt(), area.bottom.toInt())
                if (canvas != null) {
                    view.imageFlag.draw(canvas)
                }
            }
        }
    }

    fun plantFlag() { // met un drapeau sur la case
        if (flagMode) { flagMode = false }
        else { flagMode = true }
    }

    fun discover() { //découvre la case
        hide = false
    }


    operator fun invoke(): EmptyBox { //permet de change la classe de l'objet de Box à EmptyBo
    return EmptyBox(fieldPosition, view)
    }

}