package it.gruppoinfor.home2work.common

import android.support.v7.widget.RecyclerView

abstract class BaseAdapter<VH : RecyclerView.ViewHolder, I> : RecyclerView.Adapter<VH>() {

    val items: MutableList<I> = mutableListOf()

    fun setItems(items: List<I>){
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItems(items: List<I>){
        this.items.addAll(items)
        notifyItemRangeInserted(this.items.size - items.size, items.size)
    }

    fun hideItem(position: Int) {
        this.items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, 1)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}