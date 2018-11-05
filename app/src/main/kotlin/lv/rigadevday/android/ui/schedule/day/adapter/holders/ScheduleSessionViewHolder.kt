package lv.rigadevday.android.ui.schedule.day.adapter.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_schedule_session.view.*
import lv.rigadevday.android.repository.model.schedule.Session
import lv.rigadevday.android.ui.schedule.day.DayScheduleContract
import lv.rigadevday.android.utils.loadCircleAvatar

class ScheduleSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Session, contract: DayScheduleContract) = with(itemView) {
        schedule_item_title.text = item.title
        schedule_item_room.text = item.location

        item.mainSpeaker?.let { (id, _, name, _, _, photoUrl) ->
            schedule_item_speaker.text = name
            schedule_item_speaker.setOnClickListener { contract.openSpeaker(id) }

            schedule_item_speaker_photo.loadCircleAvatar(photoUrl)
            schedule_item_speaker_photo.setOnClickListener { contract.openSpeaker(id) }
        } ?: run {
            schedule_item_speaker.text = ""
            schedule_item_speaker.setOnClickListener(null)

            schedule_item_speaker_photo.loadCircleAvatar("")
            schedule_item_speaker_photo.setOnClickListener(null)
        }

        itemView.setOnClickListener { contract.openSession(item.id) }
    }

}
