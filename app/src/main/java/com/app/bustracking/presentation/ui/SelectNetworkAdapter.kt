package com.app.bustracking.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.app.BusTracking
import com.app.bustracking.data.responseModel.Agency
import com.app.bustracking.databinding.ItemSelectNetworkBinding
import com.bumptech.glide.Glide

class SelectNetworkAdapter(val onItemClick: (selectNetwork: Agency, position:Int) -> Unit) : RecyclerView.Adapter<SelectNetworkAdapter.ViewHolder>()
    {

    private var _itemList: List<Agency> = ArrayList()
    private var filteredList: List<Agency> = _itemList

    fun setList(itemList: List<Agency>){
        _itemList = itemList
        notifyDataSetChanged()
    }

    class ViewHolder(
        binding: ItemSelectNetworkBinding,
        ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemSelectNetworkBinding

        init {
            _binding = binding
        }

        fun bind(selectNetwork: Agency, onItemClick: (selectNetwork: Agency) -> Unit) {

            _binding.tvTitle.text = selectNetwork.name
            Glide.with(BusTracking.context)
                .load(selectNetwork.photo)
                .into(_binding.ivIcon)


            _binding.root.setOnClickListener {
                onItemClick(selectNetwork)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemSelectNetworkBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return _itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_itemList[position]){
            onItemClick(it, position)
        }
    }

}