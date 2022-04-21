package be.julien.info_h20_demineur


import android.content.SharedPreferences
import android.graphics.PointF
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.activity_main.*
import android.os.PersistableBundle
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import androidx.appcompat.app.AppCompatDelegate
import be.julien.info_h20_demineur.R.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_field.*
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    lateinit var timer: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN); //cachage de la barre de status
        setContentView(layout.activity_main)


        //bouton de changement de fragment
        btnChangeFragment.setOnClickListener {
            if (btnChangeFragment.text == getString(string.afficher_jeu)) {
                btnChangeFragment.text = getString(string.afficher_menu)

                val fragmentField = FragmentField()  //creation du fragment clavier

                val manager = supportFragmentManager //appel au gestionnaire de fragment


                //transaction vers le nouveau fragment
                val transaction = manager.beginTransaction()
                transaction.replace(id.fragment_container, fragmentField)
                transaction.addToBackStack(null) //conserve le fragment en mémoire
                transaction.commit()
            } else {
                btnChangeFragment.text = getString(string.afficher_jeu)
                val fragmentMenu = FragmentMenu()  //creation du fragment clavier

                val manager = supportFragmentManager //appel au gestionnaire de fragment

                //transaction vers le nouveau fragment
                val transaction = manager.beginTransaction()
                transaction.replace(id.fragment_container, fragmentMenu)
                transaction.addToBackStack(null) //conserve le fragment en mémoire
                transaction.commit()
            }
        }


        timer = findViewById(R.id.timer)


        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var hms = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                )

                timer.setText(hms)
            }
            override fun onFinish() {}
        }.start()


            val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
            val sharedPrefEdit: SharedPreferences.Editor = appSettingPrefs.edit()
            val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Changecolor.text = "Désactiver mode Nuit"
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Changecolor.text = "Activer mode nuit"
            }


            Changecolor.setOnClickListener(View.OnClickListener {
                if (isNightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPrefEdit.putBoolean("NightMode", false)
                    sharedPrefEdit.apply()

                    Changecolor.text = "Activer mode nuit"
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedPrefEdit.putBoolean("NightMode", true)
                    sharedPrefEdit.apply()

                    Changecolor.text = "Désactiver mode nuit"
                }


            })

    }


}