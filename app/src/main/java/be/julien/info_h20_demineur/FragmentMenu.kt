package be.julien.info_h20_demineur


import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*


class FragmentMenu : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_menu, container, false)

        var fragment_container = view.findViewById<ConstraintLayout>(R.id.fragment_container)
        var layout_main = view.findViewById<FrameLayout>(R.id.layout_main)
        var btnChangeFragment = view.findViewById<Button>(R.id.btnChangeFragment)
        var timer = view.findViewById<TextView>(R.id.timer)
        var text1 = view.findViewById<TextView>(R.id.text1)
        var text2 = view.findViewById<TextView>(R.id.text2)
        var modeNuit = view.findViewById<Switch>(R.id.modeNuit)
        var reglages = view.findViewById<Button>(R.id.reglages)

        modeNuit.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean){
                val backgroundMenuNightOff = resources.getColor(R.color.backgroundMenuNightOff)
                val backgroundMenuNightOn = resources.getColor(R.color.backgroundMenuNightOn)
                val backgroundButtonNightOn = resources.getColor(R.color.backgroundButtonNightOn)
                val backgroundButtonNightOff = resources.getColor(R.color.backgroundButtonNightOff)
                if(isChecked){
                    fragment_container.background = backgroundButtonNightOn
                }

            }
        }
        )
    return view
    }
}







