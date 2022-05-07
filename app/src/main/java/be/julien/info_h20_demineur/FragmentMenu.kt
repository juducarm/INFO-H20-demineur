package be.julien.info_h20_demineur


import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable

import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_menu.*


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

        val btnLangue = view?.findViewById<AppCompatButton>(R.id.btnLanguage)
        val btnHardMode = view?.findViewById<AppCompatButton>(R.id.btnHardMode)
        val btnNightMode = view?.findViewById<AppCompatButton>(R.id.btnNightMode)

        btnNightMode?.setOnClickListener {
            (activity as MainActivity?)!!.changeNightMode()
        }

        btnLangue?.setOnClickListener {
            (activity as MainActivity?)!!.changeLanguage()
        }

        btnHardMode?.setOnClickListener {
           (activity as MainActivity?)!!.changeMode()
        }
        return view
    }
}







