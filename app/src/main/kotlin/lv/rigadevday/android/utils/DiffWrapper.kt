package lv.rigadevday.android.utils

import android.support.v7.util.DiffUtil

class DiffWrapper<T>(
    private val isSame: (T, T) -> Boolean,
    private val areContentsSame: (T, T) -> Boolean
) : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(left: T, right: T) = isSame(left, right)

    override fun areContentsTheSame(left: T, right: T) = areContentsSame(left, right)

}