package lv.rigadevday.android.ui.schedule.day.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.brandongogetap.stickyheaders.exposed.StickyHeaderHandler
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.schedule.Timeslot
import lv.rigadevday.android.ui.schedule.day.DayScheduleContract
import lv.rigadevday.android.ui.schedule.day.adapter.holders.DayScheduleItem
import lv.rigadevday.android.ui.schedule.day.adapter.holders.ScheduleHeaderViewHolder
import lv.rigadevday.android.ui.schedule.day.adapter.holders.ScheduleNonSessionViewHolder
import lv.rigadevday.android.ui.schedule.day.adapter.holders.ScheduleSessionViewHolder
import lv.rigadevday.android.utils.inflate

class ScheduleAdapter(private val contract: DayScheduleContract) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderHandler {

    private var data: List<DayScheduleItem> = emptyList()

    fun setTimeslots(timeslots: List<Timeslot>) {
        val result = mutableListOf<DayScheduleItem>()
        timeslots.forEach { timeslot ->
            result += DayScheduleItem.HeaderItem(timeslot.formattedStartTime)
            timeslot.sessionObjects.forEach {
                result +=
                    if (it.isSession) DayScheduleItem.SessionItem(it)
                    else DayScheduleItem.NonSessionItem(it)
            }
        }

        data = result
        notifyDataSetChanged()
    }

    override fun getAdapterData(): List<DayScheduleItem> {
        return data
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is DayScheduleItem.HeaderItem -> R.layout.item_schedule_header
            is DayScheduleItem.SessionItem -> R.layout.item_schedule_session
            is DayScheduleItem.NonSessionItem -> R.layout.item_schedule_non_session
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(viewType)
        return when (viewType) {
            R.layout.item_schedule_header -> ScheduleHeaderViewHolder(view)
            R.layout.item_schedule_session -> ScheduleSessionViewHolder(view)
            else -> ScheduleNonSessionViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when (item) {
            is DayScheduleItem.HeaderItem -> (holder as ScheduleHeaderViewHolder).bind(item)
            is DayScheduleItem.SessionItem -> (holder as ScheduleSessionViewHolder).bind(item.session, contract)
            is DayScheduleItem.NonSessionItem -> (holder as ScheduleNonSessionViewHolder).bind(item.session)
        }
   }
}
