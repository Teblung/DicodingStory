package com.teblung.dicodingstory.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.teblung.dicodingstory.data.source.remote.response.StoryResponse

class StoryCallback(
    private val oldList: ArrayList<StoryResponse>,
    private val newList: ArrayList<StoryResponse>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (_, value, nameOld) = oldList[oldItemPosition]
        val (_, value1, nameNew) = newList[newItemPosition]
        return nameOld == nameNew && value == value1
    }
}