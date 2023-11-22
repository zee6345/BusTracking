package com.app.bustracking.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.databinding.ItemStopTimeBinding

class StopMapTimeAdapter(
    private val itemList: List<Stop>,
    val onItemClick: (routes: Stop, position: Int) -> Unit
) :
    RecyclerView.Adapter<StopMapTimeAdapter.ViewHolder>() {

    class ViewHolder(
        binding: ItemStopTimeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemStopTimeBinding

        init {
            _binding = binding
        }

        fun bind(stop: Stop, onItemClick: (Stop) -> Unit) {


            _binding.tvStopName.text = stop.stop_title

            _binding.tvStopTime.text = stop.stop_time ?: "-"

            _binding.root.setOnClickListener {
                onItemClick(stop)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemStopTimeBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position]) {
            onItemClick(it, position)
        }
    }
}