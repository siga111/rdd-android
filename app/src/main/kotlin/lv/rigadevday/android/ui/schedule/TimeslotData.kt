package lv.rigadevday.android.ui.schedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeslotData(
    val readableDate: String,
    val dateCode: String,
    val time: String = "",
    val ids: List<Int> = emptyList()
) : Parcelable
