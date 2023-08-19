package com.app.bustracking.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.databinding.ItemSelectRouteBinding
import com.app.bustracking.presentation.model.SelectNetwork

class SelectRouteAdapter(private val itemList: List<SelectNetwork>, val onItemClick: (selectNetwork: SelectNetwork, position:Int) -> Unit) :
    RecyclerView.Adapter<SelectRouteAdapter.ViewHolder>() {

    class ViewHolder(
        binding: ItemSelectRouteBinding,
        ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemSelectRouteBinding

        init {
            _binding = binding
        }


        fun bind(selectNetwork: SelectNetwork, onItemClick: (selectNetwork: SelectNetwork) -> Unit) {

//            _binding.tvTitle.text = selectNetwork.title
            _binding.root.setOnClickListener {
                onItemClick(selectNetwork)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemSelectRouteBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position]){
            onItemClick(it, position)
        }
    }
}