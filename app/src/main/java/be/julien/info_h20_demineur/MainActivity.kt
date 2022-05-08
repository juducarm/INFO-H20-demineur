package be.julien.info_h20_demineur

import android.content.SharedPreferences
import android.graphics.Color
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
import androidx.core.graphics.drawable.toDrawable
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
    var onMenu = true

    lateinit var timeBarView: TimeBarView
    lateinit var appSettingPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN); //cachage de la barre de status
        setContentView(layout.activity_main)
        btnChangeFragment.setOnClickListener(this)
        timeBarView = findViewById<TimeBarView>(R.id.timeBarView)
        setFragmentMenu()
        fragmentMenu.mainActivity = this
        appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        //sharedPrefEdit.putBoolean("NightMode", false)

    }

    override fun onClick(v: View) {

        when(v.id) { //when au lieu de setOnClickListener pour pouvoir mettre plusieurs boutons facilement si besoin
            R.id.btnChangeFragment -> {

                if (onMenu) {
                    btnChangeFragment.text = getString(string.afficher_menu)

                   setFragmentField()

                    timeBarView.background = Color.TRANSPARENT.toDrawable()
                    timeBarView.startDrawing()
                    timeBarView.start()
                }
                else {
                    btnChangeFragment.text = getString(string.afficher_jeu)

                    setFragmentMenu()

                    timeBarView.background = resources.getColor(R.color.Background).toDrawable()
                    timeBarView.stopDrawing()
                    timeBarView.stop()
                    val sharedPrefEdit: SharedPreferences.Editor = appSettingPrefs.edit()
                    sharedPrefEdit.putBoolean("NightMode", false)

                }
                onMenu = !onMenu
            }
        }
    }

    fun setFragmentField() {
        val transaction = manager.beginTransaction()
        transaction.replace(id.fragment_container, fragmentField)
        transaction.addToBackStack(null) //conserve le fragment en mémoire
        transaction.commit()
    }

    fun setFragmentMenu() {
        val transaction = manager.beginTransaction()
        transaction.replace(id.fragment_container, fragmentMenu)
        transaction.addToBackStack(null) //conserve le fragment en mémoire
        transaction.commit()
     }

    fun getIsNightModeOn(): Boolean {
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        return isNightModeOn
    }

    fun changeNightMode() {

        val sharedPrefEdit: SharedPreferences.Editor = appSettingPrefs.edit()


        if (getIsNightModeOn()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPrefEdit.putBoolean("NightMode", false)
            //Toast.makeText(applicationContext,resources.getString(R.string.PopupNightModeOFF),Toast.LENGTH_LONG).show()

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPrefEdit.putBoolean("NightMode", true)
            //Toast.makeText(applicationContext,resources.getString(R.string.PopupNightModeON),Toast.LENGTH_LONG).show()
        }
        sharedPrefEdit.apply()

    }

    fun changeLanguage() {
        if (Locale.getDefault().language == "en") {
            setToFrench()
        } else {
            setToEnglish()
        }
        recreate()
    }

    fun setToEnglish() {
        resources.configuration.setLocale(Locale("en"))
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        Locale.setDefault(Locale("en"))
    }

    fun setToFrench() {
        resources.configuration.setLocale(Locale("fr"))
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        Locale.setDefault(Locale("fr"))
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

