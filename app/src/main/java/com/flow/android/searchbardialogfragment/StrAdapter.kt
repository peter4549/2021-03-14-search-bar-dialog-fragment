package com.flow.android.searchbardialogfragment

class StrAdapter: SearchBarListAdapter<String>() {
    override val menuRes: Int?
        get() = null
    override val itemIdToOnSelected: HashMap<Int, (item: String) -> Unit>?
        get() = null

    override fun copy(item: String): String {
        return item
    }

    override fun bind(viewHolder: ViewHolder, item: String) {
        viewHolder.viewBinding.textViewContent.text = item

        viewHolder.viewBinding.root.setOnClickListener {
            onItemClickListener?.onClick(item)
        }
    }

    override fun filter(
        itemWrapper: ItemWrapper<String>,
        searchWord: String
    ): ItemWrapper<String>? {
        return itemWrapper
    }
}