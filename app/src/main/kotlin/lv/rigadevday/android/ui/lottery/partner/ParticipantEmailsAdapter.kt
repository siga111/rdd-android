package lv.rigadevday.android.ui.lottery.partner

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.lottery_email.view.*
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.lottery.ParticipantEmail
import lv.rigadevday.android.utils.inflate

class ParticipantEmailsAdapter(
    private val onDelete: (ParticipantEmail) -> Unit
)  : ListAdapter<ParticipantEmail, EmailViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        return EmailViewHolder(parent.inflate(R.layout.lottery_email))
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
         holder.bind(getItem(position), onDelete)
    }

    companion object {

        private val DIFF = object: DiffUtil.ItemCallback<ParticipantEmail>(){
            override fun areItemsTheSame(p0: ParticipantEmail, p1: ParticipantEmail) = p0.id == p1.id
            override fun areContentsTheSame(p0: ParticipantEmail, p1: ParticipantEmail) = p0 == p1
        }
    }
}

class EmailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(item: ParticipantEmail, onDelete: (ParticipantEmail) -> Unit) = with (itemView){
        lottery_item_email.text = item.email
        lottery_item_email_delete.setOnClickListener { onDelete(item) }
    }
}