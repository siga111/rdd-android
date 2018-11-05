package lv.rigadevday.android.ui.schedule.day.adapter.holders

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_schedule_header.view.*

class ScheduleHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @SuppressLint("SetTextI18n")
    fun bind(item: DayScheduleItem.HeaderItem) = with(itemView) {
        schedule_header_time.text = item.title
    }
}
