package com.app.bustracking.presentation.ui

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bustracking.databinding.ItemRouteBinding

import com.app.bustracking.presentation.model.Routes

class RoutesAdapter(
    private val itemList: List<Routes>,
    val onItemClick: (routes: Routes, position: Int) -> Unit
) :
    RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    class ViewHolder(
        binding: ItemRouteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val _binding: ItemRouteBinding

        init {
            _binding = binding
        }


        fun bind(routes: Routes, onItemClick: (Routes) -> Unit) {


            // Generate a random color
            val randomColor = Color.rgb(
                (0..255).random(),
                (0..255).random(),
                (0..255).random()
            )

            // Create a ColorDrawable with the random color
            val ovalShape = OvalShape()
            val shapeDrawable = ShapeDrawable(ovalShape)
            shapeDrawable.paint.color = randomColor


            val parts = routes.header.split("-")
            val extractedValue = parts.firstOrNull() ?: ""


            _binding.ivIcon.background = shapeDrawable
            _binding.ivIcon.text = extractedValue
            _binding.tvTitle.text = routes.header
            _binding.tvText.text = routes.msg
            _binding.ivMsgIcon.visibility = if (routes.msgIcon == 0) View.GONE else View.VISIBLE
            _binding.ivMsgIcon.background = shapeDrawable
            _binding.lvMsg.visibility = if (routes.msg.isEmpty()) View.GONE else View.VISIBLE

            _binding.root.setOnClickListener {
                onItemClick(routes)
            }

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