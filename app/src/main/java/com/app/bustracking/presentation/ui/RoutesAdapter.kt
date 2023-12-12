package com.app.bustracking.presentation.ui


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.R
import com.app.bustracking.data.local.RoutesDao
import com.app.bustracking.data.responseModel.Route
import com.app.bustracking.databinding.ItemRouteBinding


class RoutesAdapter(
    private var itemList: List<Route>,
    private val routesDao: RoutesDao,
    val onItemClick: (route: Route, position: Int) -> Unit
) :
    RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

//    private var _itemList: List<Route> = ArrayList()

//    fun setRouteList(itemList: List<Route>){
//        _itemList = itemList
//        notifyDataSetChanged()
//    }


    class ViewHolder(
        binding: ItemRouteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemRouteBinding

        init {
            _binding = binding
        }


        fun bind(route: Route, routesDao: RoutesDao, onItemClick: (Route) -> Unit) {

            _binding.tvTitle.text = "${route.route_title}"
            _binding.ivCheck.setImageResource(if (route.isFavourite) R.drawable.ic_check_filled else R.drawable.ic_check_unfilled)

            _binding.ivCheck.setOnClickListener {
                _binding.ivCheck.setImageResource(if (route.isFavourite) R.drawable.ic_check_unfilled else R.drawable.ic_check_filled)
                route.isFavourite = !route.isFavourite
                routesDao.updateFav(route)
            }

            _binding.root.setOnClickListener {
                onItemClick(route)
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
        holder.bind(itemList[position], routesDao) {
            onItemClick(it, position)
        }
    }
}