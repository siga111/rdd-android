package lv.rigadevday.android.repository.model.schedule

import com.google.firebase.database.IgnoreExtraProperties
import lv.rigadevday.android.repository.model.speakers.Speaker

@IgnoreExtraProperties
data class Session(
    val id: Int = -1,

    val auditorium: String = "",

    val title: String = "",
    val description: String = "",
    val speakers: List<Int> = emptyList(),

    val image: String = ""
) {
    var speakerObjects: List<Speaker> = listOf()
    var room: String = ""

    var time: String = ""
    var date: String = ""

    var rating: Rating = Rating()

    val location: String get() = auditorium.takeIf(String::isNotEmpty) ?: room

    val mainSpeaker get() = speakerObjects.firstOrNull()

    val isSession get() = speakerObjects.isNotEmpty()

}

