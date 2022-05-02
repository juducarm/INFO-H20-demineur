package be.julien.info_h20_demineur

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.view.WindowManager.LayoutParams.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import be.julien.info_h20_demineur.R.*
import java.util.concurrent.TimeUnit


public class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var timer: TextView
    lateinit var Changecouleur: AppCompatButton
    val fragmentField = FragmentField()  //creation du fragment champs de case
    val fragmentMenu = FragmentMenu()  //creation du fragment menu
    val manager = supportFragmentManager //appel au gestionnaire de fragment
    var timeLeft: Long = 100000


    override fun onClick(v: View) {

        when(v.id) { //when au lieu de setOnClickListener pour pouvoir mettre plusieurs boutons si besoin
            R.id.btnChangeFragment -> {
                if (btnChangeFragment.text == getString(string.afficher_jeu)) {
                    btnChangeFragment.text = getString(string.afficher_menu)

                    //transaction vers le nouveau fragment
                    val transaction = manager.beginTransaction()
                    transaction.replace(id.fragment_container, fragmentField)
                    transaction.addToBackStack(null) //conserve le fragment en mémoire
                    transaction.commit()
                }
                else {
                    btnChangeFragment.text = getString(string.afficher_jeu)

                    //transaction vers le nouveau fragment
                    val transaction = manager.beginTransaction()
                    transaction.replace(id.fragment_container, fragmentMenu)
                    transaction.addToBackStack(null) //conserve le fragment en mémoire
                    transaction.commit()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN); //cachage de la barre de status
        setContentView(layout.activity_main)
        btnChangeFragment.setOnClickListener(this)

        timer = findViewById(R.id.timer)
        object : CountDownTimer(timeLeft, 1000) {
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


    }

    fun changeMode() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


        Changecouleur.setOnClickListener(View.OnClickListener {
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefEdit.putBoolean("NightMode", false)
                sharedPrefEdit.apply()


            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefEdit.putBoolean("NightMode", true)
                sharedPrefEdit.apply()
            }
        })
    }

}