package com.app.bustracking.presentation.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.R
import com.app.bustracking.data.local.StopsDao
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.databinding.ItemRouteBinding
import java.util.Random

class StopsAdapter(
    private val itemList: List<Stop>,
    private val stopsDao: StopsDao,
    val onItemClick: (travel: Stop, position: Int) -> Unit
) :
    RecyclerView.Adapter<StopsAdapter.ViewHolder>() {

    class ViewHolder(
        binding: ItemRouteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemRouteBinding
//        private var isFavourite = false


        init {
            _binding = binding
        }


        fun bind(travel: Stop, stopsDao: StopsDao, onItemClick: (Stop) -> Unit) {

            _binding.tvTitle.text = "${travel.stop_title}"
            _binding.ivCheck.setImageResource(if (travel.isFavourite) R.drawable.ic_check_filled else R.drawable.ic_check_unfilled)

            _binding.ivCheck.setOnClickListener {
                _binding.ivCheck.setImageResource(if (travel.isFavourite) R.drawable.ic_check_filled else R.drawable.ic_check_unfilled)
                travel.isFavourite = !travel.isFavourite
                stopsDao.updateFav(travel)
            }

//            _binding.tvText.text = travel.travel_description
//            _binding.ivMsgIcon.visibility = if (travel.travel_description.isEmpty()) View.GONE else View.VISIBLE
//            _binding.lvMsg.visibility = if (travel.travel_description.isEmpty()) View.GONE else View.VISIBLE

//            val title = travel.travel_name
//            if (title.isNotEmpty()) {
//                val firstChar = title[0]
//                val lastChar = title[title.length - 1]
//                _binding.ivIcon.text = "$firstChar$lastChar"
//            }
//            try {
//                val drawable = generateRandomColor()
//                _binding.ivIcon.background = drawable
//                _binding.ivMsgIcon.background = drawable
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

            _binding.root.setOnClickListener {
                onItemClick(travel)
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
        return ViewHolder(ItemRouteBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], stopsDao) {
            onItemClick(it, position)
        }
    }
}