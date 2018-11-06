package lv.rigadevday.android.ui.lottery

import android.view.View
import kotlinx.android.synthetic.main.fragment_lottery.*
import lv.rigadevday.android.R
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.auth.AuthStorage
import lv.rigadevday.android.utils.hide
import lv.rigadevday.android.utils.show
import javax.inject.Inject

class LotteryFragment : BaseFragment() {

    @Inject
    lateinit var authStorage: AuthStorage

    override val layoutId = R.layout.fragment_lottery

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun viewReady(view: View) {
        setupActionBar(R.string.tab_lottery)
        refreshLoginState()
    }

    fun refreshLoginState() {
        if (isAdded) {
            if (authStorage.hasLogin) {
                lottery_empty_state.hide()
                loadData()
            } else {
                lottery_empty_state.show()
            }
        }
    }

    private fun loadData() {

    }
}