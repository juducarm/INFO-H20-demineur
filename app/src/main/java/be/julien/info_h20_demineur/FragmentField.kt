package be.julien.info_h20_demineur


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.fragment_field.view.*


class FragmentField : Fragment() {

    lateinit var fieldView: FieldView
    private lateinit var textViewFlag: TextView
    private lateinit var textViewTimer: TextView



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {

        val layoutView = inflater.inflate(R.layout.fragment_field, container, false)
        textViewFlag = layoutView.findViewById(R.id.textFlag)
        textViewTimer = layoutView.findViewById(R.id.textTimer)
        fieldView = layoutView.findViewById(R.id.fieldView)
        fieldView.setWillNotDraw(false)
        fieldView.textViewFlag = textViewFlag as MaterialTextView //seule façon trouvée pour référencer les textView dans le fieldView
        fieldView.textViewTimer = textViewTimer as MaterialTextView
        fieldView.boxCreation()
        fieldView.theBombs.forEach { it.warningBomb(fieldView.theEmptyBoxes) }

        layoutView.btnFlag.setOnClickListener {
            fieldView.flagMode()
        }

        return layoutView
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

}
