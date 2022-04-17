package be.julien.info_h20_demineur

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FragmentField : Fragment() {
    lateinit var fieldView: FieldView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.fragment_field)
        //fieldView = findViewById<FieldView>(R.id.vMain)
    }

    override fun onPause() {
        super.onPause()
        fieldView.pause()
    }

    override fun onResume() {
        super.onResume()
        fieldView.resume()
    }
}