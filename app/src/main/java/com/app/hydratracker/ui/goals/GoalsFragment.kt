package com.app.hydratracker.ui.goals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hydratracker.ConsommationAdapter
import com.app.hydratracker.R
import com.app.hydratracker.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GoalsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_goals, container, false)
        val database = AppDatabase.getDatabase(requireContext())
        val waterIntake = database.waterIntakeDao()
        CoroutineScope(Dispatchers.Main).launch {
            val waterIntakeList = waterIntake.getAllWaterIntakes().toMutableList()
            val recyclerView: RecyclerView = view.findViewById(R.id.goalsrecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = WaterIntakeAdapter(waterIntakeList)
        }


        return view
    }

}