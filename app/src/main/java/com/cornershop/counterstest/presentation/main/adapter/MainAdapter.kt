package com.cornershop.counterstest.presentation.main.adapter

import android.graphics.Color
import android.view.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.databinding.ItemCounterBinding
import com.cornershop.counterstest.domain.entity.Counter

class MainAdapter(
    private val onAction: ((action: ItemAction) -> Unit),
) : ListAdapter<Counter, MainAdapter.ViewHolder>(DiffCallback()) {

    lateinit var selectionTracker: SelectionTracker<String>

    class DetailsLookup(val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as ViewHolder).getItemDetails()
            }
            return null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val binding = ItemCounterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding, onAction)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val counter = currentList[position]
        holder.bind(counter, selectionTracker)
    }

    class ViewHolder(
        private val binding: ItemCounterBinding,
        private val onAction: (ItemAction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            counter: Counter,
            selectionTracker: SelectionTracker<String>
        ) = with(itemView) {
            bindSelectedState(this, selectionTracker.isSelected(counter.id))

            binding.textTitle.text = counter.title
            binding.textCounter.text = counter.count.toString()
            binding.root.setOnClickListener { onAction(ItemAction.Click(counter)) }
            //binding.root.setOnLongClickListener { onAction(ItemAction.LongClick(counter)) }
            binding.buttonIncrement.setOnClickListener {
                onAction(
                    ItemAction.Increment(
                        counter,
                        counter.count + 1
                    )
                )
            }
            binding.buttonDecrement.setOnClickListener {
                if (counter.count > 0)
                    onAction(ItemAction.Decrement(counter, counter.count - 1))
            }
            if (counter.count == 0) {
                binding.textCounter.setTextColor(Color.GRAY)
                binding.buttonDecrement.setColorFilter(Color.GRAY)
            } else {
                binding.textCounter.setTextColor(Color.BLACK)
                binding.buttonDecrement.setColorFilter(
                    ContextCompat.getColor(
                        binding.buttonDecrement.context,
                        R.color.orange
                    )
                )
            }

            selectionTracker?.let {
                if (it.isSelected(counter.id)) {
                    itemView.setBackgroundDrawable(
                        ContextCompat.getDrawable(binding.textCounter.context, R.drawable.rounded_selected_bg))
                } else {
                    itemView.background = null
                }
            }

        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = bindingAdapterPosition
                override fun getSelectionKey(): String? =
                    (bindingAdapter as MainAdapter).currentList[bindingAdapterPosition].id
            }

        private fun bindSelectedState(view: View, selected: Boolean) {
            view.isActivated = selected
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Counter>() {

        override fun areItemsTheSame(oldItem: Counter, newItem: Counter) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Counter, newItem: Counter) =
            oldItem == newItem
    }

    sealed class ItemAction() {
        data class Click(val counter: Counter) : ItemAction()
        data class Increment(val counter: Counter, val amount: Int) : ItemAction()
        data class Decrement(val counter: Counter, val amount: Int) : ItemAction()
    }

}
