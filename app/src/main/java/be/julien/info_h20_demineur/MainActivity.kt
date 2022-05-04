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
import kotlinx.android.synthetic.main.fragment_menu.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var timer: TextView
    val fragmentField = FragmentField()  //creation du fragment champs de case
    val fragmentMenu = FragmentMenu()  //creation du fragment menu
    val manager = supportFragmentManager //appel au gestionnaire de fragment
    var timeLeft: Long = 100000

    var hardModeOn = false


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
    fun getIsNightModeOn(): Boolean {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        return isNightModeOn
    }

    fun changeNightMode() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        println("nightMode : ${getIsNightModeOn()}")
        if (getIsNightModeOn()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPrefEdit.putBoolean("NightMode", false)
            sharedPrefEdit.apply()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPrefEdit.putBoolean("NightMode", true)
            sharedPrefEdit.apply()
        }
    }



    fun changeLanguage() {
        val defaultLocale = Locale.getDefault()
        if (defaultLocale.language != "fr") {
            resources.configuration.setLocale(Locale("fr"))
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
            Locale.setDefault(Locale("fr"))
            recreate()
        } else {
            resources.configuration.setLocale(Locale("en"))
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
            Locale.setDefault(Locale("en"))
            recreate()
        }
    }


    fun changeHardMode() {
         if (hardModeOn) {
             fragmentField.nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth_EZ)
             fragmentField.nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight_EZ)
             fragmentField.nbrBombs = resources.getInteger(R.integer.nbrBombs_EZ)
             hardModeOn = false
         }
        else {
             fragmentField.nbrBoxesWidth = resources.getInteger(R.integer.nbrBoxesWidth_HARD)
             fragmentField.nbrBoxesHeight = resources.getInteger(R.integer.nbrBoxesHeight_HARD)
             fragmentField.nbrBombs = resources.getInteger(R.integer.nbrBombs_HARD)
             hardModeOn = true
         }

    }




}

