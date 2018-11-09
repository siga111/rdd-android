package lv.rigadevday.android.ui.lottery.partner

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_lottery.*
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.lottery.ParticipantEmail
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.auth.AuthStorage
import lv.rigadevday.android.utils.plus
import lv.rigadevday.android.utils.showMessage
import javax.inject.Inject


class LotteryPartnerFragment : BaseFragment() {

    @Inject
    lateinit var authStorage: AuthStorage

    override val layoutId = R.layout.fragment_lottery

    private val emailAdapter = ParticipantEmailsAdapter { deleteEmail(it) }

    private val resultRegex = Regex(".*\\n.*@.*\\..*")

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun viewReady(view: View) {
        with(lottery_recycler) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lottery_partner, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_camera -> {
            startQrScanner()
            true
        }
        R.id.action_export -> {
            exportEmails()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun startQrScanner() {
        IntentIntegrator(requireActivity())
            .apply {
                setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                setPrompt(getString(R.string.lottery_scan_qr))
                setBeepEnabled(false)
                setOrientationLocked(false)
                setBarcodeImageEnabled(true)
            }
            .createScanIntent()
            .let { startActivityForResult(it, IntentIntegrator.REQUEST_CODE) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.let { res ->
            res.contents
                ?.takeIf { it.matches(resultRegex) }
                ?.split('\n')
                ?.let { ParticipantEmail(it.first(), it.last()) }
                ?.let { repo.saveParticipantEmail(it) }
                ?: requireContext().showMessage(R.string.lottery_wrong_qr)
        } ?: super.onActivityResult(requestCode, resultCode, data)
    }

    private fun exportEmails() = Intent(Intent.ACTION_SEND)
        .apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, emailAdapter.emails().joinToString("\n"))
        }
        .let { startActivity(Intent.createChooser(it, "Share link using")) }

}

