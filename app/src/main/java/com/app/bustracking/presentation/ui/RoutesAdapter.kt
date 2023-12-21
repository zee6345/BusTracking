package com.app.bustracking.presentation.ui


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.data.local.RoutesDao
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.databinding.ItemRouteBinding


class RoutesAdapter(
    private val routesDao: RoutesDao,
    val onItemClick: (route: Route, position: Int) -> Unit
) : RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    private var itemList: List<Route> = emptyList()

    fun updateList(newList: List<Route>) {
        itemList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(
        binding: ItemRouteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemRouteBinding

        init {
            _binding = binding
        }


        fun bind(route: Route, routesDao: RoutesDao, onItemClick: (Route) -> Unit) {

            _binding.tvTitle.text = "${route.route_title}"
            _binding.ivCheck.setImageResource(if (route.isFavourite) com.app.bustracking.R.drawable.ic_check_filled else com.app.bustracking.R.drawable.ic_check_unfilled)
            route.color?.let {
                val headerColor = Color.parseColor(it)
                _binding.ivHeader.background = ColorDrawable(headerColor)
            }

            route.route_title?.let {
                _binding.tvHeader.text = getStartingCharacters(it)
            }


            _binding.ivCheck.setOnClickListener {
                _binding.ivCheck.setImageResource(if (route.isFavourite) com.app.bustracking.R.drawable.ic_check_unfilled else com.app.bustracking.R.drawable.ic_check_filled)
                route.isFavourite = !route.isFavourite
                routesDao.updateFav(route)
            }

            _binding.root.setOnClickListener {
                onItemClick(route)
            }

            //hide connected status
            _binding.lvMsg.visibility = if (route.isVehicleConnected) View.VISIBLE else View.GONE
        }

        private fun getStartingCharacters(inputString: String): String {
            // Remove leading and trailing spaces
            val trimmedString = inputString.trim()

            // Extract the starting 2 characters
            return trimmedString.substring(0, minOf(2, trimmedString.length))
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
        holder.bind(itemList[position], routesDao) {
            onItemClick(it, position)
        }
    }
}