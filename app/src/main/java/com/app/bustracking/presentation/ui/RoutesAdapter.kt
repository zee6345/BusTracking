package com.app.bustracking.presentation.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.R
import com.app.bustracking.data.local.TravelDao
import com.app.bustracking.data.responseModel.Travel
import com.app.bustracking.databinding.ItemRouteBinding
import java.util.Random

class RoutesAdapter(
    private val itemList: List<Travel>,
    private val travelDao: TravelDao,
    val onItemClick: (travel: Travel, position: Int) -> Unit
) :
    RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    class ViewHolder(
        binding: ItemRouteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemRouteBinding
        private var isFavourite = false

        init {
            _binding = binding
        }


        fun bind(travel: Travel, travelDao: TravelDao, onItemClick: (Travel) -> Unit) {

            _binding.tvTitle.text = "${travel.travel_name}"
            _binding.ivCheck.setImageResource(if (travel.isFavourite) R.drawable.ic_check_filled else R.drawable.ic_check_unfilled)

            _binding.ivCheck.setOnClickListener {

                _binding.ivCheck.setImageResource(if (isFavourite) R.drawable.ic_check_filled else R.drawable.ic_check_unfilled)

                if (isFavourite) {
                    travelDao.addFavourite(travel.travelId, 1)
                } else {
                    travelDao.removeFavourite(travel.travelId, 0)
                }

                isFavourite = !isFavourite
            }

            _binding.root.setOnClickListener {
                onItemClick(travel)
            }

        }


//        private fun generateRandomColor(): ColorDrawable {
//            val random = Random()
//            val red = random.nextInt(256) // Random value between 0 and 255 for red
//            val green = random.nextInt(256) // Random value between 0 and 255 for green
//            val blue = random.nextInt(256) // Random value between 0 and 255 for blue
//
//            val backgroundColor = Color.rgb(red, green, blue)
//
//            // Create and return the random color
//            return ColorDrawable(backgroundColor)
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemRouteBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], travelDao) {
            onItemClick(it, position)
        }
    }
}