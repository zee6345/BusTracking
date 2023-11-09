package com.app.bustracking.presentation.views.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.bustracking.R
import com.app.bustracking.data.local.Database
import com.app.bustracking.data.responseModel.Stop
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    abstract fun initNavigation(navController: NavController)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = NavHostFragment.findNavController(this)
        initNavigation(controller)

    }

    fun appDb(): Database {
        return Database.init(requireActivity())
    }

    fun showProgress(): AlertDialog {
        return AlertDialog.Builder(requireActivity(), R.style.TransparentAlertDialogTheme)
            .setView(R.layout.item_progress)
            .create()
    }

    fun showMessage(str:String){
        Toast.makeText(requireActivity(), "$str", Toast.LENGTH_SHORT).show()
    }

    inline fun <reified T : Activity> routeScreen(){
        val intent = Intent(requireActivity(),  T::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    fun writeToFile(fileName: String, content: String) {
        try {
            // Create a File object
            val file = File(fileName)

            // Create a FileWriter and BufferedWriter to write to the file
            val fileWriter = FileWriter(file)
            val bufferedWriter = BufferedWriter(fileWriter)

            // Write the content to the file
//            bufferedWriter.write(content)
            bufferedWriter.append(content)

            // Close the BufferedWriter and FileWriter
            bufferedWriter.close()
            fileWriter.close()

            println("Data has been written to the file.")
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }

    fun readFromFile(fileName: String): String {
        val file = File(fileName)
        val stringBuilder = StringBuilder()

        try {
            val bufferedReader = BufferedReader(FileReader(file))
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
            bufferedReader.close()
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }

        return stringBuilder.toString()
    }

    fun parseStopsFromString(input: String): List<Stop> {
        val stopList = mutableListOf<Stop>()
        val regex = Regex("Stop\\((.*?)\\)")

        val matchResults = regex.findAll(input)
        for (matchResult in matchResults) {
            val stopData = matchResult.groupValues[1].split(", ")
            if (stopData.size == 10) {
                val agencyId = stopData[0].substringAfter("agency_id=").toInt()
                val createdAt = stopData[1].substringAfter("created_at=")
                val direction = stopData[2].substringAfter("direction=")
                val id = stopData[3].substringAfter("id=").toInt()
                val lat = stopData[4].substringAfter("lat=").toDouble()
                val lng = stopData[5].substringAfter("lng=").toDouble()
                val routeId = stopData[6].substringAfter("route_id=").toInt()
                val stopTime = stopData[7].substringAfter("stop_time=")
                val stopTitle = stopData[8].substringAfter("stop_title=")
                val updatedAt = stopData[9].substringAfter("updated_at=")

//                val stop = Stop(agencyId, createdAt, direction, id, "$lat", "$lng", routeId, stopTime, stopTitle, updatedAt)
//                stopList.add(stop)
            }
        }

        return stopList
    }

}