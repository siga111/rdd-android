package lv.rigadevday.android.ui.schedule.day.adapter.holders

import com.brandongogetap.stickyheaders.exposed.StickyHeader
import lv.rigadevday.android.repository.model.schedule.Session

sealed class DayScheduleItem {

    data class HeaderItem(val title: String): DayScheduleItem(), StickyHeader
    data class SessionItem(val session: Session): DayScheduleItem()
    data class NonSessionItem(val session: Session): DayScheduleItem()
}