package lv.rigadevday.android.ui.lottery.participant

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_lottery_partners_logo.view.*
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.lottery.PartnerStatus
import lv.rigadevday.android.utils.*

class LotteryPartnerAdapter : ListAdapter<PartnerStatus, PartnerStatusViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerStatusViewHolder {
        return PartnerStatusViewHolder(parent.inflate(R.layout.item_lottery_partners_logo))
    }

    override fun onBindViewHolder(holder: PartnerStatusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {

        private val DIFF = DiffWrapper<PartnerStatus>(
            { p0, p1 -> p0.partner.id == p1.partner.id },
            { p0, p1 -> p0 == p1 }
        )
    }
}

class PartnerStatusViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(item: PartnerStatus) = with(itemView) {
        lottery_partners_logo_item_image.setImageDrawable(null)
        lottery_partners_logo_item_image.loadLogo(item.partner.logoUrl) { lottery_partners_logo_item_name.show() }
        lottery_partners_logo_item_name.hide()
        lottery_partners_logo_item_name.text = item.partner.name

        if (item.emailGiven) {
            lottery_partners_logo_overlay.hide()
            lottery_partners_logo_check.show()
        } else {
            lottery_partners_logo_overlay.show()
            lottery_partners_logo_check.hide()
        }
    }
}