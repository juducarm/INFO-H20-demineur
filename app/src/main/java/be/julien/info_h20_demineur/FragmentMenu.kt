package be.julien.info_h20_demineur


import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable

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

        val btnLangue = view?.findViewById<AppCompatButton>(R.id.btnLanguage)
        val btnHardMode = view?.findViewById<AppCompatButton>(R.id.btnHardMode)
        val btnNightMode = view?.findViewById<AppCompatButton>(R.id.btnNightMode)

        btnNightMode?.setOnClickListener {
            (activity as MainActivity?)!!.changeNightMode()
            println("nightMode")
        }

        btnLangue?.setOnClickListener {
            (activity as MainActivity?)!!.changeLanguage()
            println("changeLange")
        }

        btnHardMode?.setOnClickListener {
            println("hardLevel")
           (activity as MainActivity?)!!.changeHardMode()
        }

        return view
    }
}







