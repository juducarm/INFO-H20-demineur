package be.julien.info_h20_demineur

import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import kotlinx.android.synthetic.main.*

//classe abstraite car elle ne se manifestera qu'à travers ses sous-classes
class Box(var boxSize : Float, val view : FieldView){


    val boxPaint = Paint()
    // valeurs pour la taille du jeu
    val size = 0f // ?
    val nbr_box_lenght = 0 // ?
    val nbr_box_width = 0 // ?

    var position = PointF() //position du coin supérieur gauche de la case
}
