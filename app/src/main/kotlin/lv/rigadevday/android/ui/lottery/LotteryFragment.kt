package lv.rigadevday.android.ui.lottery

import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_lottery.*
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.lottery.ParticipantEmail
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.ui.lottery.partner.ParticipantEmailsAdapter
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.auth.AuthStorage
import lv.rigadevday.android.utils.plus
import lv.rigadevday.android.utils.showMessage
import javax.inject.Inject

class LotteryFragment : BaseFragment() {

    @Inject
    lateinit var authStorage: AuthStorage

    override val layoutId = R.layout.fragment_lottery

    private  val emailAdapter = ParticipantEmailsAdapter { deleteEmail(it) }

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun viewReady(view: View) {
        with (lottery_recycler) {
            adapter = emailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        dataFetchSubscription += repo.lotteryParticipantEmails()
            .subscribe(
                { emailAdapter.submitList(it) },
                {
                    requireContext().showMessage(R.string.error_message)
                    requireActivity().finish()
                }
            )
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

}

