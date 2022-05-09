package be.julien.info_h20_demineur


import android.graphics.*


abstract class Box(val fieldPosition: Point, var view: FieldView) {

    //mode dev
    val devPaint = Paint()

    val area = RectF(fieldPosition.x * view.boxSize,//surface couverte par la case
        fieldPosition.y * view.boxSize,
        (fieldPosition.x + 1) * view.boxSize,
        (fieldPosition.y + 1) * view.boxSize)
    val hiddenBoxPaint = Paint()
    val aroundList = listOf( //liste permettant d'accéder aux cases voisines
        Point(-1, -1), Point(-1, 0), Point(-1, 1),
        Point(1, -1), Point(1, 0), Point(1, 1),
        Point(0, 1), Point(0, -1)
    )
    var hide = true
    var isSafe = true // est true si la case n'est pas une bomb et ne compte aucune bombe parmis ses voisines
    val paint = Paint()


    init {
        if ((fieldPosition.x + fieldPosition.y ) % 2 == 0) { //permet d'avoir un quadrillage
            hiddenBoxPaint.color = view.hiddenBoxColor1
        }
        else {hiddenBoxPaint.color = view.hiddenBoxColor2}
    }

    open fun draw(canvas: Canvas?) {
        if (hide) {
            canvas?.drawRect(area, hiddenBoxPaint) // dessin de la case cachée
        }
    }


    fun discover() { //découvre la case
        hide = false
    }

    operator fun invoke(): EmptyBox { //permet de change la classe de l'objet de Box à EmptyBo
    return EmptyBox(fieldPosition, view)
    }

}


