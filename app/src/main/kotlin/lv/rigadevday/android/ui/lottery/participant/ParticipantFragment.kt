package lv.rigadevday.android.ui.lottery.participant

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.frament_participant.*
import lv.rigadevday.android.R
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.utils.*
import lv.rigadevday.android.utils.auth.AuthStorage
import net.glxn.qrgen.android.QRCode
import javax.inject.Inject

class ParticipantFragment : BaseFragment() {

    @Inject
    lateinit var authStore: AuthStorage

    override val layoutId = R.layout.frament_participant

    private val partnersAdapter = LotteryPartnerAdapter()

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun viewReady(view: View) {
        with(lottery_qr_code) {
            "${authStore.uId}\n${authStore.email}"
                .let { QRCode.from(it).bitmap() }
                .let { setImageBitmap(it) }
        }

        with(lottery_partners_list) {
            adapter = partnersAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        }

        dataFetchSubscription += repo.lotteryPartnerStatuses()
            .subscribe(
                {
                    partnersAdapter.submitList(it)
                },
                {
                    requireContext().showMessage(R.string.error_message)
                    requireActivity().finish()
                }
            )
    }
}

