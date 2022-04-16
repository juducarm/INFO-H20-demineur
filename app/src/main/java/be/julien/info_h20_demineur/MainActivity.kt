package be.julien.info_h20_demineur

import android.graphics.PointF
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    lateinit var  fieldView: FieldView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fieldView = findViewById<FieldView>(R.id.main_layout)
        textTest.text = "bonjour"


    }


}