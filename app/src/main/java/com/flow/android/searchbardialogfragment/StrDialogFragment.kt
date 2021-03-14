package com.flow.android.searchbardialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class StrDialogFragment: SearchBarDialogFragment<String>() {

    private val strAdapter = StrAdapter()

    override val listAdapter: SearchBarListAdapter<String>?
        get() = strAdapter
    override val menuDrawableRes: Int?
        get() = R.drawable.ic_baseline_add_circle_outline_24
    override val title: String
        get() = "HAL!"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        strAdapter.submitItemList(listOf("AAAA", "BBB", "CC"))

        return view
    }
}