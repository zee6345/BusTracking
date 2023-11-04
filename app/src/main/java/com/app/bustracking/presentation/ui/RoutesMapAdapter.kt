package com.app.bustracking.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.databinding.ItemRouteMapBinding
import com.app.bustracking.presentation.model.MapRoutes

class RoutesMapAdapter(
    private val itemList: List<Stop>,
    val onItemClick: (routes: Stop, position: Int) -> Unit
) :
    RecyclerView.Adapter<RoutesMapAdapter.ViewHolder>() {

    class ViewHolder(
        binding: ItemRouteMapBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemRouteMapBinding

        init {
            _binding = binding
        }

        fun bind(routes: Stop, onItemClick: (Stop) -> Unit) {

            _binding.tvMapRoute.text = routes.stop_title

            _binding.root.setOnClickListener {
                onItemClick(routes)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemRouteMapBinding.inflate(inflater, parent, false))
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