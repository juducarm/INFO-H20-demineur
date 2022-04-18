package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

//la classe EmptyBox hérite de la classe Box
class EmptyBox(fieldPosition: android.graphics.Point, size : Float, view: FieldView):
    Box(fieldPosition,size, view) {

    val paint = Paint()
    var bombsAround = 0

    init {
        paint.color = Color.GREEN
    }


    override fun DrawDiscover(canvas: Canvas?) {
        super.DrawDiscover(canvas)
        canvas?.drawRect(areaWithGrid, paint) // dessin de la case
    }

    fun findBombsAround(theBombs: ArrayList<Bomb>) {

        //liste permettant de controler si les cases sont côte à côte
        val AroundList = listOf(Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1), Pair(0, 1), Pair(0, -1))
        theBombs.forEach { bomb ->
            val temoin = Pair(bomb.fieldPosition.x - fieldPosition.x,
                bomb.fieldPosition.y - fieldPosition.y)
            if (AroundList.contains(temoin)) {
                bombsAround++
                println("nombre de bomes : $bombsAround")
                print("Position1 : $fieldPosition et Position2 : ")
                println(bomb.fieldPosition)
                println(" temoin : $temoin")
                paint.color = Color.BLUE
            }
        }
    }


}

