package com.app.bustracking.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.app.bustracking.R
import com.app.bustracking.databinding.ItemProgressBinding

class Progress(val context: Context) {

    private lateinit var _dialog: AlertDialog

    fun showProgress(): AlertDialog {
        val customDialog: View = LayoutInflater.from(context).inflate(R.layout.item_progress, null)
        val binding: ItemProgressBinding = ItemProgressBinding.bind(customDialog)
        val alert = AlertDialog.Builder(context)
        alert.setView(binding.root)
        _dialog = alert.create()
        _dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return _dialog
    }

    fun showDialog() {
        _dialog.show()
    }

    fun dismissDialog() {
        _dialog.dismiss()
    }

}