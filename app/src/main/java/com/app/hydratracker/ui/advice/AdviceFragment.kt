package com.app.hydratracker.ui.advice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hydratracker.R
import com.app.hydratracker.data.AdviceItem


class AdviceFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: AdviceAdapter
    private lateinit var cardList: ArrayList<AdviceItem>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_advice, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cardList = arrayListOf(
            AdviceItem(
                R.drawable.cup_water,
                getString(R.string.advice_start_day_with_water),
                getString(R.string.advice_start_day_with_water_description)
            ),
            AdviceItem(
                R.drawable.water_bottle,
                getString(R.string.advice_set_hydration_goal),
                getString(R.string.advice_set_hydration_goal_description)
            ),
            AdviceItem(
                R.drawable.thirsty,
                getString(R.string.advice_drink_before_thirsty),
                getString(R.string.advice_drink_before_thirsty_description)
            ),
            AdviceItem(
                R.drawable.fruit_water,
                getString(R.string.advice_infuse_water_with_flavor),
                getString(R.string.advice_infuse_water_with_flavor_description)
            ),
            AdviceItem(
                R.drawable.cup_water,
                getString(R.string.advice_carry_water_bottle),
                getString(R.string.advice_carry_water_bottle_description)
            ),
            AdviceItem(
                R.drawable.vegetables,
                getString(R.string.advice_eat_water_rich_foods),
                getString(R.string.advice_eat_water_rich_foods_description)
            ),
            AdviceItem(
                R.drawable.reminder,
                getString(R.string.advice_set_hydration_reminders),
                getString(R.string.advice_set_hydration_reminders_description)
            )
        )
        cardAdapter = AdviceAdapter(cardList)
        recyclerView.adapter = cardAdapter

        return view
    }


}