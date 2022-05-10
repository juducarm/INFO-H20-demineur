package be.julien.info_h20_demineur

import android.graphics.*
import androidx.core.graphics.toRect


class Flag(fieldPosition: Point,view: FieldView): SquareObject(fieldPosition, view) {

    fun draw(canvas: Canvas?) {
        view.imageFlag.setBounds(area.toRect())
        if (canvas != null) {
            view.imageFlag.draw(canvas)
        }
        view.countFlagsLeft()
    }


}