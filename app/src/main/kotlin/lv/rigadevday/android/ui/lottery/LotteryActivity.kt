package lv.rigadevday.android.ui.lottery

import kotlinx.android.synthetic.main.activity_lottery.*
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.lottery.LotteryState.Participant
import lv.rigadevday.android.repository.model.lottery.LotteryState.Partner
import lv.rigadevday.android.ui.base.BaseActivity
import lv.rigadevday.android.utils.*
import lv.rigadevday.android.utils.auth.AuthStorage
import javax.inject.Inject

class LotteryActivity : BaseActivity() {

    @Inject
    lateinit var authStorage: AuthStorage

    override val layoutId = R.layout.activity_lottery

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun viewReady() {
        setupActionBar(R.string.lottery_title)
        homeAsUp()
        refreshView()
    }

    override fun refreshLoginState() {
        refreshView()
    }

    private fun refreshView() {
        lottery_empty_state.hide()
        lottery_frame.hide()
        dataFetchSubscription += repo.getLotteryState()
            .subscribe(
                { state ->
                    when (state) {
                        Partner -> showPartnerScreen()
                        Participant -> showParticipantScreen()
                        else -> showEmptyState()
                    }
                },
                { showMessage(R.string.error_message) }
            )
    }

    private fun showEmptyState() {
        lottery_frame.hide()
        lottery_empty_state.show()

        lottery_empty_state.setOnClickListener {
            loginWrapper.logIn(this)
        }
    }

    private fun showPartnerScreen() {
        lottery_empty_state.hide()
        lottery_frame.show()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.lottery_frame, LotteryFragment())
            .commit()
    }

    private fun showParticipantScreen() {
        lottery_empty_state.hide()
    }
}