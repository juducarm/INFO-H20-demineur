package be.julien.info_h20_demineur

import android.graphics.*


class Flag(val fieldPosition: Point, val view: FieldView) {


    val area = RectF(fieldPosition.x * view.boxSize,//surface couverte par la case
        fieldPosition.y * view.boxSize,
        (fieldPosition.x + 1) * view.boxSize,
        (fieldPosition.y + 1) * view.boxSize)


    fun draw(canvas: Canvas?) {
        view.imageFlag.setBounds(area.left.toInt(), area.top.toInt(),
            area.right.toInt(), area.bottom.toInt())
        if (canvas != null) {
            view.imageFlag.draw(canvas)
        }
        view.countFlagsLeft()
    }

}