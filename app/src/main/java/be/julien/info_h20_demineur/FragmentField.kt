package be.julien.info_h20_demineur

import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.fragment_field.*


class FragmentField : Fragment() {

    lateinit var fieldView: FieldView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val layoutView = inflater!!.inflate(R.layout.fragment_field,container,false)
        fieldView = layoutView.findViewById(R.id.fieldView)
        fieldView.setWillNotDraw(false)
        fieldView.invalidate()
        fieldView.boxCreation()
        return layoutView
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

}