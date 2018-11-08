package lv.rigadevday.android.ui.lottery

import android.view.View
import kotlinx.android.synthetic.main.fragment_lottery.*
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.lottery.LotteryState
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.utils.*
import lv.rigadevday.android.utils.auth.AuthStorage
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
                loadData()
            } else {
                showEmptyState()
            }
        }
    }

    private fun loadData() {
        lottery_recycler.show()
        lottery_empty_state.hide()
        dataFetchSubscription = repo.getLotteryState()
            .subscribe(
                {state ->
                    when (state) {
                        is LotteryState.NotLoggedIn -> showEmptyState()
                        is LotteryState.Partner -> {
                            setupPartner(state)
                        }
                        is LotteryState.Participant -> {
                            setupParticipant(state)
                        }
                    }

                },
                {
                    it.printStackTrace()
                    requireContext().showMessage(R.string.error_message)
                }
            )
    }

    private fun showEmptyState() {
        lottery_recycler.hide()
        lottery_empty_state.show()
    }

    private fun setupPartner(state: LotteryState.Partner) {
        lottery_recycler.show()
        lottery_empty_state.hide()
        state.logE()
    }

    private fun setupParticipant(state: LotteryState.Participant) {
        lottery_recycler.show()
        lottery_empty_state.hide()
        state.logE()
    }


}

