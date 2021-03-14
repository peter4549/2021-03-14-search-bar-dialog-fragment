package com.flow.android.searchbardialogfragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.flow.android.searchbardialogfragment.databinding.FragmentSearchBarDialogBinding


abstract class SearchBarDialogFragment<T>: DialogFragment() {

    private object Key {
        const val MenuDrawableRes = "menu_drawable_res"
        const val Title = "title"
    }

    protected abstract val listAdapter: SearchBarListAdapter<T>?
    @get:DrawableRes
    protected abstract val menuDrawableRes: Int?
    protected abstract val title: String

    private val blank = ""
    private var viewBinding: FragmentSearchBarDialogBinding? = null

    private var onItemClickListener: SearchBarListAdapter.OnItemClickListener<T>? = null
    private var onMenuClickListener: OnMenuClickListener? = null

    interface OnMenuClickListener {
        fun onClick(dialogFragment: DialogFragment)
    }

    interface OnClickListener {
        fun <T> onItemClickListener(): SearchBarListAdapter.OnItemClickListener<T>?
        fun onMenuClickListener(): OnMenuClickListener?
    }

    override fun onAttach(context: Context) {
        if (context is OnClickListener) {
            onItemClickListener = context.onItemClickListener()
            onItemClickListener?.also { listAdapter?.setOnItemClickListener(it) }
            onMenuClickListener = context.onMenuClickListener()
        }

        super.onAttach(context)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Key.Title, title)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentSearchBarDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        var menuDrawableRes = this.menuDrawableRes
        var title = this.title

        savedInstanceState?.also {
            menuDrawableRes = it.getInt(Key.MenuDrawableRes)
            title = it.getString(Key.Title) ?: blank
        }

        viewBinding?.let { viewBinding ->
            viewBinding.textViewTitle.text = title
            menuDrawableRes?.also {
                viewBinding.imageViewMenu.setImageResource(it)
                viewBinding.imageViewMenu
            }
            viewBinding.imageViewMenu.setOnClickListener {
                onMenuClickListener?.onClick(this)
            }

            viewBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    listAdapter?.filter()?.filter(newText)
                    return true
                }
            })

            val searchAutoComplete: SearchAutoComplete = viewBinding.searchView.findViewById(R.id.search_src_text)

            try {
                val field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                field.isAccessible = true
                field.set(searchAutoComplete, R.drawable.cursor)
            } catch (e: Exception) {

            }

            viewBinding.searchView.setOnQueryTextFocusChangeListener { _, boolean ->
                if (boolean) {
                    if (viewBinding.textViewTitle.isVisible)
                        viewBinding.textViewTitle.gone()
                } else {
                    viewBinding.textViewTitle.visible()
                    viewBinding.searchView.isIconified = true
                }
            }

            viewBinding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this.context)
                listAdapter?.let { adapter = listAdapter }
            }
        }

        return viewBinding?.root
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun View.gone() {
        this.visibility = View.GONE
    }

    private fun View.visible() {
        this.visibility = View.VISIBLE
    }
}