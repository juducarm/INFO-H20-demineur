package be.julien.info_h20_demineur

import android.graphics.PointF
import android.graphics.RectF

interface AreaCalculator {
    fun Area(fieldPostion: PointF, boxSize: Float): RectF {
        return RectF(fieldPostion.x * boxSize,//surface couverte par la case
            fieldPostion.y * boxSize,
            (fieldPostion.x + 1) * boxSize,
            (fieldPostion.y + 1) * boxSize)
    }
}