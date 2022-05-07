package be.julien.info_h20_demineur

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import be.julien.info_h20_demineur.R.*
import kotlinx.android.synthetic.main.fragment_field.*
import kotlinx.android.synthetic.main.fragment_menu.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), View.OnClickListener {

    val fragmentField = FragmentField()
    val fragmentMenu = FragmentMenu()
    val manager = supportFragmentManager
    var hardModeOn = false

    lateinit var timeBarView: TimeBarView

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
        timeBarView = findViewById<TimeBarView>(R.id.timeBarView)
        timeBarView.start()

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
            Toast.makeText(applicationContext,resources.getString(R.string.PopupNightModeOFF),Toast.LENGTH_LONG).show()
            sharedPrefEdit.apply()

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPrefEdit.putBoolean("NightMode", true)
            Toast.makeText(applicationContext,resources.getString(R.string.PopupNightModeON),Toast.LENGTH_LONG).show()
            sharedPrefEdit.apply()

        }
    }

    fun changeLanguage() {
        val defaultLocale = Locale.getDefault()
        if (defaultLocale.language == "en") {
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

    fun changeMode() {
         if (hardModeOn) {
             fragmentField.goToEasyMode()
             hardModeOn = false
             Toast.makeText(applicationContext,resources.getString(R.string.PopupHardModeOFF), Toast.LENGTH_LONG).show()
         }
        else {
             fragmentField.goToHardMode()
             hardModeOn = true
             Toast.makeText(applicationContext,resources.getString(R.string.PopupHardModeON),Toast.LENGTH_LONG).show()
         }
    }
}

