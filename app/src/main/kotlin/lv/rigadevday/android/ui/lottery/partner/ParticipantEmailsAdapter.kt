package lv.rigadevday.android.ui.lottery.partner

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.lottery_email.view.*
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.lottery.ParticipantEmail
import lv.rigadevday.android.utils.DiffWrapper
import lv.rigadevday.android.utils.inflate

class ParticipantEmailsAdapter(
    private val onDelete: (ParticipantEmail) -> Unit
) : ListAdapter<ParticipantEmail, EmailViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        return EmailViewHolder(parent.inflate(R.layout.lottery_email))
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(getItem(position), onDelete)
    }

    fun emails() = (0 until itemCount).map { getItem(it).email }

    companion object {

        private val DIFF = DiffWrapper<ParticipantEmail>(
            { p0, p1 -> p0.id == p1.id },
            { p0, p1 -> p0 == p1 }
        )
    }
}

class EmailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(item: ParticipantEmail, onDelete: (ParticipantEmail) -> Unit) = with(itemView) {
        lottery_item_email.text = item.email
        lottery_item_email_delete.setOnClickListener { onDelete(item) }
    }
}