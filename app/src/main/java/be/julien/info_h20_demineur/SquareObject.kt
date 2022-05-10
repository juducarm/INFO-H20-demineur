package be.julien.info_h20_demineur

import android.graphics.Point
import android.graphics.RectF

abstract class SquareObject(val fieldPosition: Point, var view: FieldView) {
    val area = RectF(fieldPosition.x * view.boxSize,//surface couverte par la case
        fieldPosition.y * view.boxSize,
        (fieldPosition.x + 1) * view.boxSize,
        (fieldPosition.y + 1) * view.boxSize)
}