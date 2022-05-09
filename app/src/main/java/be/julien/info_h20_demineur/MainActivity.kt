package be.julien.info_h20_demineur

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import be.julien.info_h20_demineur.R.*
import kotlinx.android.synthetic.main.fragment_field.*
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    val fragmentField = FragmentField()
    val fragmentMenu = FragmentMenu()
    val manager = supportFragmentManager
    var hardModeOn = false
    var onMenu = true
    var devModeOn = false
    var changeMade = false

    lateinit var timeBarView: TimeBarView
    lateinit var appSettingPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN); //cachage de la barre de status
        setContentView(layout.activity_main)
        btnChangeFragment.setOnClickListener(this)
        createFragments()
        timeBarView = findViewById<TimeBarView>(R.id.timeBarView)
        fragmentMenu.mainActivity = this
        appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0)
    }

    override fun onClick(v: View) {

        when(v.id) { //when au lieu de setOnClickListener pour pouvoir mettre plusieurs boutons facilement si besoin
            R.id.btnChangeFragment -> {
                if (onMenu) {
                    btnChangeFragment.text = getString(string.afficher_menu)

                    showFragmentField()

                }
                else {
                    btnChangeFragment.text = getString(string.afficher_jeu)

                    showFragmentMenu()


                }
            }
        }
    }

    
   fun createFragments() {
       val transaction = manager.beginTransaction()
       transaction.add(id.fragment_container, fragmentMenu)
       transaction.add(id.fragment_container, fragmentField)
       transaction.hide(fragmentField)
       transaction.commit()
   }

   fun showFragmentField() {
       onMenu = false
       manager.beginTransaction()
           .show(fragmentField)
           .hide(fragmentMenu)
           .commit()
       timeBarView.background = Color.TRANSPARENT.toDrawable()
       timeBarView.startDrawing()
       timeBarView.start()

       if (changeMade) { //redémarre la partie si besoin
           fragmentField.fieldView.newGame()
           fragmentField.fieldView.timer.start()
       }
       else {
           fragmentField.fieldView.setNewTimer()
       }
       changeMade = false
   }

   fun showFragmentMenu() {
       fragmentField.fieldView.timer.cancel()
       onMenu = true
       manager.beginTransaction()
           .show(fragmentMenu)
           .hide(fragmentField)
           .commit()
       timeBarView.background = resources.getColor(R.color.Background).toDrawable()
       timeBarView.stopDrawing()
       timeBarView.stop()
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

    fun changeDifficulty() {
        if (hardModeOn) {
            fragmentField.fieldView.goToEasyMode()
            hardModeOn = false
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.PopupHardModeOFF),
                Toast.LENGTH_LONG
            ).show()
        } else {
            fragmentField.fieldView.goToHardMode()
            hardModeOn = true
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.PopupHardModeON),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun changeDevMode() {
        devModeOn = !devModeOn
        fragmentField.fieldView.changeDevMode(devModeOn)
        if (devModeOn) {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.PopupdevModeON),
                Toast.LENGTH_LONG
            ).show()
        }
        else {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.PopupdevModeOFF),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    fun changesHaveBeenMade() { //permet de redémarrer la partie si des réglages ont été changés
        changeMade = true
    }


}

