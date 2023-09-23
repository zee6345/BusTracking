package com.app.bustracking.presentation.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.data.responseModel.Travel
import com.app.bustracking.databinding.ItemSelectRouteBinding
import java.util.Random


class SelectRouteAdapter(

    val onItemClick: (selectNetwork: Travel, position: Int) -> Unit
) :
    RecyclerView.Adapter<SelectRouteAdapter.ViewHolder>() {

    private var _itemList: List<Travel> = ArrayList()

    fun setList(itemList: List<Travel>){
        _itemList = itemList
        notifyDataSetChanged()
    }

    class ViewHolder(
        binding: ItemSelectRouteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemSelectRouteBinding

        init {
            _binding = binding
        }


        fun bind(selectNetwork: Travel, onItemClick: (selectNetwork: Travel) -> Unit) {


            _binding.tvTitle.text = selectNetwork.travel_name
            _binding.tvText.text = selectNetwork.travel_description


            val title = selectNetwork.travel_name
            if (title.isNotEmpty()) {
                val firstChar = title[0]
                val lastChar = title[title.length - 1]
                _binding.ivIcon.text = "$firstChar$lastChar"
            }

            try {
                _binding.ivIcon.background = generateRandomColor()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _binding.root.setOnClickListener {
                onItemClick(selectNetwork)
            }

        }

        private fun generateRandomColor(): ColorDrawable {
            val random = Random()
            val red = random.nextInt(256) // Random value between 0 and 255 for red
            val green = random.nextInt(256) // Random value between 0 and 255 for green
            val blue = random.nextInt(256) // Random value between 0 and 255 for blue

            val backgroundColor = Color.rgb(red, green, blue)

            // Create and return the random color
            return ColorDrawable(backgroundColor)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemSelectRouteBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return _itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_itemList[position]) {
            onItemClick(it, position)
        }
    }


}