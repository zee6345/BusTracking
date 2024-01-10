package com.app.bustracking.presentation.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.R
import com.app.bustracking.data.local.StopsDao
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.databinding.ItemRouteMapBinding

class RoutesMapAdapter(
    private val itemList: List<Stop>,
    private val stopsDao: StopsDao,
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

        fun bind(stop: Stop, stopsDao: StopsDao, onItemClick: (Stop) -> Unit) {

            _binding.tvTitle.text = stop.stop_title
            _binding.ivCheck.setImageResource(if (stop.isFavourite) R.drawable.ic_check_filled else R.drawable.ic_check_unfilled)


            _binding.ivCheck.setOnClickListener {
                _binding.ivCheck.setImageResource(if (stop.isFavourite) R.drawable.ic_check_unfilled else R.drawable.ic_check_filled)
                stop.isFavourite = !stop.isFavourite
                stopsDao.updateStop(stop)
            }

            _binding.llRoute.setOnClickListener {
                onItemClick(stop)
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
        val stop = itemList[position]
        stop?.let { stop ->
            holder.bind(stop, stopsDao) {
                onItemClick(it, position)
            }
        }
    }
}