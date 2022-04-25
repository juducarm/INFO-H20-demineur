package be.julien.info_h20_demineur


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_field.view.*


class FragmentField : Fragment() {

    lateinit var fieldView: FieldView
    lateinit var textView: TextView



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {

        val layoutView = inflater.inflate(R.layout.fragment_field, container, false)
        textView = layoutView.findViewById(R.id.textFlag)

        fieldView = layoutView.findViewById(R.id.fieldView)
        fieldView.setWillNotDraw(false)
        fieldView.invalidate()
        fieldView.boxCreation()
        fieldView.theBombs.forEach { it.warningBomb(fieldView.theEmptyBoxes) }

        layoutView.btnFlag.setOnClickListener {
            if (fieldView.plantFlag) { fieldView.plantFlag = false }
            else { fieldView.plantFlag = true}
        }
        textView.text = fieldView.nbrFlags.toString()
        fieldView.textNbrFlags = textView
        return layoutView
    }




    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
    }

}