package lv.rigadevday.android.repository.model.schedule

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Timeslot(
    val startTime: String = "",
    val endTime: String = "",
    val sessions: List<SessionList> = emptyList()
) {
    val sessionIds: List<Int> get() = sessions.flatMap { it.items }

    val formattedStartTime: String get() = if (startTime.length < 5) "0$startTime" else startTime

    var sessionObjects = listOf<Session>()

}

@IgnoreExtraProperties
data class SessionList(
    val items: List<Int> = emptyList()
)
