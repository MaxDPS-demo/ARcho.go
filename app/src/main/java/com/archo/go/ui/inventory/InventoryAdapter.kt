package com.archo.go.ui.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.archo.go.databinding.ItemInventoryBinding
import com.archo.go.domain.model.Artefact

class InventoryAdapter : RecyclerView.Adapter<InventoryAdapter.InventoryVH>() {

    private var items: List<Artefact> = emptyList()

    fun submitList(newItems: List<Artefact>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryVH {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryVH(binding)
    }

    override fun onBindViewHolder(holder: InventoryVH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    class InventoryVH(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artefact) {
            binding.icon.setImageResource(item.iconRes)
            binding.name.text = item.name
        }
    }
}
