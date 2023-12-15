package com.app.bustracking.presentation.ui


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

    private var itemList:List<Route> = emptyList()

    fun updateList(newList: List<Route>) {
        itemList = newList
        notifyDataSetChanged() // Notify the adapter that the data set has changed
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

//            route.color?.let {
//                _binding.ivIcon.background = ColorDrawable(Integer.parseInt(route.color.replace("#","")))
//            }


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


//        private fun getRandomColor(context: Context): Drawable? {
//            // Retrieve the color array from resources
//            val colors = context.resources.obtainTypedArray(com.app.bustracking.R.array.colorArray)
//            // Get a random index
//            val randomIndex = (Math.random() * colors.length()).toInt()
//            // Get the color resource ID at the random index
//            val colorResourceId = colors.getResourceId(randomIndex, 0)
//            // Recycle the TypedArray to avoid memory leaks
//            colors.recycle()
//            // Get the actual color value using the resource ID
//            return ColorDrawable(ContextCompat.getColor(context, colorResourceId))
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