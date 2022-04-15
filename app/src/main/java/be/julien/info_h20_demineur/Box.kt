package be.julien.info_h20_demineur

import android.graphics.Point
import android.graphics.PointF

//classe abstraite car elle ne se manifestera qu'a travers ses sous-classes
abstract class Box {
    var position = PointF() //couple de float. position.x pour la coord en x, idem en y
}
