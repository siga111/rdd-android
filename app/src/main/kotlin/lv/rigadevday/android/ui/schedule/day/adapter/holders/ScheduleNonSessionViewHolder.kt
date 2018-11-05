package lv.rigadevday.android.ui.schedule.day.adapter.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_schedule_non_session.view.*
import lv.rigadevday.android.repository.model.schedule.Session

class ScheduleNonSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Session) = with(itemView) {
        schedule_item_non_session_title.text = item.title
        schedule_item_non_session_subtitle.text = item.location
    }

}
