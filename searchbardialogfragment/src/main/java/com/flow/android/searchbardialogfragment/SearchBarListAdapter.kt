package com.flow.android.searchbardialogfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flow.android.searchbardialogfragment.databinding.ItemSearchBarBinding

abstract class SearchBarListAdapter<T>: ListAdapter<ItemWrapper<T>, SearchBarListAdapter.ViewHolder>(DiffCallback<T>()) {

    @get:MenuRes
    protected abstract val menuRes: Int?
    protected abstract val itemIdToOnSelected: HashMap<Int, (item: T) -> Unit>?

    private val blank = ""
    private val itemWrapperList = arrayListOf<ItemWrapper<T>>()
    protected var recyclerView: RecyclerView? = null
    protected var searchWord = blank

    protected var onItemClickListener: OnItemClickListener<T>? = null

    @JvmName("_setOnItemClickListener")
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener<T> {
        fun onClick(item: T)
    }

    protected abstract fun copy(item: T): T

    fun submitItemList(itemList: List<T>) {
        this.itemWrapperList.clear()

        for ((index, item) in itemList.withIndex())
            this.itemWrapperList.add(ItemWrapper(index.toLong(), item))

        submitList(ArrayList(this.itemWrapperList))
    }

    class ViewHolder(val viewBinding: ItemSearchBarBinding): RecyclerView.ViewHolder(viewBinding.root)

    protected abstract fun bind(viewHolder: ViewHolder, item: T)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = ItemSearchBarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bind(holder, getItem(position).item)
    }

    protected abstract fun filter(itemWrapper: ItemWrapper<T>, searchWord: String): ItemWrapper<T>?

    fun filter(): Filter {
        var itemWrapperListFiltered: ArrayList<ItemWrapper<T>>

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                searchWord = charSequence.toString()
                val itemWrapperListBeingFiltered = arrayListOf<ItemWrapper<T>>()

                itemWrapperListFiltered =
                    if (searchWord.isBlank())
                        itemWrapperList
                    else {
                        for (item in itemWrapperList)
                            filter(item, searchWord)?.also {
                                val adapterItem = ItemWrapper(
                                    it.id,
                                    copy(it.item)
                                )
                                itemWrapperListBeingFiltered.add(adapterItem)
                            }

                        itemWrapperListBeingFiltered
                    }

                return FilterResults().apply {
                    values = itemWrapperListFiltered
                }
            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                results?.values?.also {
                    submitList(results.values as List<ItemWrapper<T>>)

                    if (searchWord.isBlank())
                        notifyDataSetChanged()
                }
            }
        }
    }

    protected fun showPopupMenu(view: View, item: T) {
        val popupMenu = PopupMenu(view.context, view)
        menuRes?.let {
            popupMenu.inflate(it)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                itemIdToOnSelected?.let { itemIdToOnSelected->
                    itemIdToOnSelected[menuItem.itemId]?.invoke(item)
                    return@setOnMenuItemClickListener true
                } ?: false
            }

            popupMenu.show()
        }
    }
}

class DiffCallback<T>: DiffUtil.ItemCallback<ItemWrapper<T>>() {
    override fun areItemsTheSame(oldItemWrapper: ItemWrapper<T>, newItemWrapper: ItemWrapper<T>): Boolean {
        return oldItemWrapper.id == newItemWrapper.id
    }

    override fun areContentsTheSame(oldItemWrapper: ItemWrapper<T>, newItemWrapper: ItemWrapper<T>): Boolean {
        return oldItemWrapper == newItemWrapper
    }
}

data class ItemWrapper<T>(
    val id: Long,
    val item: T
)