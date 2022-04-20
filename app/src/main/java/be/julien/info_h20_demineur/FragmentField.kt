package be.julien.info_h20_demineur


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class FragmentField : Fragment() {

    lateinit var fieldView: FieldView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_field, container, false)
        fieldView = layoutView.findViewById(R.id.fieldView)
        fieldView.setWillNotDraw(false)
        fieldView.invalidate()
        fieldView.boxCreation()
        fieldView.theBombs.forEach { it.warningBomb(fieldView.theEmptyBoxes) }
        return layoutView
    }

}