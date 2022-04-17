package be.julien.info_h20_demineur

import android.graphics.PointF
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.activity_main.*
import android.os.PersistableBundle
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_field.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN); //cachage de la barre de status
        setContentView(R.layout.activity_main)


        //bouton de changement de fragment
        btnChangeFragment.setOnClickListener {
            if (btnChangeFragment.text == getString(R.string.afficher_jeu)) {
                btnChangeFragment.text = getString(R.string.afficher_menu)

                val fragmentField = FragmentField()  //creation du fragment clavier

                val manager = supportFragmentManager //appel au gestionnaire de fragment


                //transaction vers le nouveau fragment
                val transaction = manager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragmentField)
                transaction.addToBackStack(null) //conserve le fragment en mémoire
                transaction.commit()
            } else {
                btnChangeFragment.text = getString(R.string.afficher_jeu)
                val fragmentMenu = FragmentMenu()  //creation du fragment clavier

                val manager = supportFragmentManager //appel au gestionnaire de fragment

                //transaction vers le nouveau fragment
                val transaction = manager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragmentMenu)
                transaction.addToBackStack(null) //conserve le fragment en mémoire
                transaction.commit()
            }
        }


    }
}