package be.julien.info_h20_demineur


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment


class FragmentMenu : Fragment() {


    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val btnLangue = view?.findViewById<AppCompatButton>(R.id.btnLanguage)
        val btnHardMode = view?.findViewById<AppCompatButton>(R.id.btnHardMode)
        val btnNightMode = view?.findViewById<AppCompatButton>(R.id.btnNightMode)

        btnNightMode?.setOnClickListener {
            mainActivity.changeNightMode()
        }

        btnLangue?.setOnClickListener {
            mainActivity.changeLanguage()
        }

        btnHardMode?.setOnClickListener {
            mainActivity.changeMode()
        }

        return view
    }
}







