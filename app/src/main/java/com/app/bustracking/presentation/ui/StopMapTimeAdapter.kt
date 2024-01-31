package com.app.bustracking.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.databinding.ItemStopTimeBinding
import com.mapbox.mapboxsdk.geometry.LatLng

class StopMapTimeAdapter(
    private val itemList: List<Stop>,
    val onItemClick: (routes: Stop, position: Int) -> Unit
) :
    RecyclerView.Adapter<StopMapTimeAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: ItemStopTimeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(stop: Stop, onItemClick: (Stop) -> Unit) {

            binding.tvStopName.text = stop.stop_title

            stop.stop_time?.let {
                binding.tvStopTime.text = if (it.isEmpty()) "-" else stop.stop_time
            }

//            stop.lat?.let {
//                binding.verticalSeekBar.setCoordinatesList(
//                    LatLng(
//                        stop.lat.toDouble(),
//                        stop.lng!!.toDouble()
//                    )
//                )
//            }




            binding.root.setOnClickListener {
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