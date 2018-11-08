package lv.rigadevday.android.ui.lottery

import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_lottery.*
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.lottery.LotteryState
import lv.rigadevday.android.repository.model.lottery.ParticipantEmail
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.ui.lottery.partner.ParticipantEmailsAdapter
import lv.rigadevday.android.utils.*
import lv.rigadevday.android.utils.auth.AuthStorage
import javax.inject.Inject

class LotteryFragment : BaseFragment() {

    @Inject
    lateinit var authStorage: AuthStorage

    override val layoutId = R.layout.fragment_lottery

    private var emailAdapter: ParticipantEmailsAdapter? = null

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
                {
                    when (it) {
                        is LotteryState.NotLoggedIn -> showEmptyState()
                        is LotteryState.Partner -> setupPartner(it)
                        is LotteryState.Participant -> setupParticipant(it)
                    }
                },
                { requireContext().showMessage(R.string.error_message) }
            )
    }

    private fun showEmptyState() {
        lottery_recycler.hide()
        lottery_empty_state.show()
    }

    private fun setupPartner(state: LotteryState.Partner) {
        lottery_empty_state.hide()
        if (emailAdapter == null) {
            emailAdapter = ParticipantEmailsAdapter { deleteEmail(it) }
        }
        emailAdapter?.submitList(state.emails)
        lottery_recycler.apply {
            adapter   = emailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun deleteEmail(item: ParticipantEmail) {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.lottery_email_delete_message)
            .setPositiveButton(R.string.lottery_email_delete_action) { di, _ ->
                repo.deleteParticipantEmail(item)
                di.dismiss()
            }
            .show()
    }

    private fun setupParticipant(state: LotteryState.Participant) {
        lottery_recycler.show()
        lottery_empty_state.hide()
        state.logE()
    }


}

