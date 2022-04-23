package uz.smartmuslim.fleethefield.ui.screen.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.smartmuslim.fleethefield.databinding.ItemRectangleBinding
import uz.smartmuslim.fleethefield.model.Item

class ItemAdapter() : RecyclerView.Adapter<ItemAdapter.VH>() {

    private var list = mutableListOf<Item>()

    inner class VH(private val binding: ItemRectangleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: Item) {

            binding.image.setImageResource(item.image)
        }
    }

    fun setData(list: List<Item>) {
        this.list = list.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRectangleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}