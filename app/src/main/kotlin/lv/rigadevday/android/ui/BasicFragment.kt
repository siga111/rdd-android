package lv.rigadevday.android.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_basic.view.*
import lv.rigadevday.android.R
import lv.rigadevday.android.utils.toExtraKey

class BasicFragment : Fragment() {

    companion object {
        val EXTRA_TEXT = "basic_text".toExtraKey()

        fun newInstance(text: String) = BasicFragment().apply {
            arguments = Bundle().apply { putString(EXTRA_TEXT, text) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_basic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.basic_text.text = arguments.getString(EXTRA_TEXT, "placeholder")
    }
}

