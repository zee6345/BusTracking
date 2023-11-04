package com.app.bustracking.presentation.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.databinding.ItemRouteBinding
import java.util.Random

class RoutesAdapter(
    private val itemList: List<Route>,
    val onItemClick: (routes: Route, position: Int) -> Unit
) :
    RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    class ViewHolder(
        binding: ItemRouteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemRouteBinding

        init {
            _binding = binding
        }


        fun bind(routes: Route, onItemClick: (Route) -> Unit) {

            _binding.tvTitle.text = routes.route_title
            _binding.tvText.text = routes.description
            _binding.ivMsgIcon.visibility =
                if (routes.description.isEmpty()) View.GONE else View.VISIBLE
            _binding.lvMsg.visibility =
                if (routes.description.isEmpty()) View.GONE else View.VISIBLE

            val title = routes.route_title
            if (title.isNotEmpty()) {
                val firstChar = title[0]
                val lastChar = title[title.length - 1]
                _binding.ivIcon.text = "$firstChar$lastChar"
            }
            try {
                val drawable = generateRandomColor()
                _binding.ivIcon.background = drawable
                _binding.ivMsgIcon.background = drawable
            } catch (e: Exception) {
                e.printStackTrace()
            }


            _binding.root.setOnClickListener {
                onItemClick(routes)
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
        holder.bind(itemList[position]) {
            onItemClick(it, position)
        }
    }
}