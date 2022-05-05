package be.julien.info_h20_demineur

import android.graphics.*


class Flag(val fieldPosition: Point, val view: FieldView) {

    val paint = Paint()

    val area = RectF(fieldPosition.x * view.boxSize,//surface couverte par la case
        fieldPosition.y * view.boxSize,
        (fieldPosition.x + 1) * view.boxSize,
        (fieldPosition.y + 1) * view.boxSize)

    init {
        paint.color = Color.WHITE
    }

    fun draw(canvas: Canvas?) {
        canvas?.drawRect(area, paint) // dessin de la case
        view.imageFlag.setBounds(area.left.toInt(), area.top.toInt(),
            area.right.toInt(), area.bottom.toInt())
        if (canvas != null) {
            view.imageFlag.draw(canvas)
        }
    }
}