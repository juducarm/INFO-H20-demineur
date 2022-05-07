package be.julien.info_h20_demineur


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.fragment_field.*
import kotlinx.android.synthetic.main.fragment_field.view.*


class FragmentField : Fragment() {

    lateinit var fieldView: FieldView
    lateinit var textViewFlag: TextView
    lateinit var textViewTimer: TextView

    var nbrBoxesWidth = 9 //resources.getInteger(R.integer.nbrBoxesWidth_EZ)
    var nbrBoxesHeight =13 //resources.getInteger(R.integer.nbrBoxesHeight_EZ)
    var nbrBombs = 20 //resources.getInteger(R.integer.nbrBombs_EZ)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {

        val layoutView = inflater.inflate(R.layout.fragment_field, container, false)

        textViewFlag = layoutView.findViewById(R.id.textFlag)
        textViewTimer = layoutView.findViewById(R.id.textTimer)
        fieldView = layoutView.findViewById(R.id.fieldView)
        fieldView.setWillNotDraw(false)
        fieldView.textViewFlag = textViewFlag as MaterialTextView //seule façon trouvée pour référencer les textView dans le fieldView
        fieldView.textViewTimer = textViewTimer as MaterialTextView
        fieldView.nbrBoxesHeight = nbrBoxesHeight
        fieldView.nbrBoxesWidth = nbrBoxesWidth
        fieldView.nbrBombs = nbrBombs
        fieldView.boxSize = minOf(fieldView.resolution.x / nbrBoxesWidth, fieldView.resolution.y / nbrBoxesHeight)
        fieldView.boxCreation()
        fieldView.theBombs.forEach { it.warningBomb(fieldView.theEmptyBoxes) }

        layoutView.btnFlag.setOnClickListener {
            fieldView.flagMode()
        }

        return layoutView
    }




    override fun onPause() {
        super.onPause()
        fieldView.pause()
    }

    override fun onResume() {
        super.onResume()
        fieldView.resume()
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
