package com.app.hydratracker


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.app.hydratracker.data.AppDatabase
import com.app.hydratracker.data.DailyTarget
import com.app.hydratracker.data.WaterIntake
import com.app.hydratracker.data.WaterIntakeDao
import com.app.hydratracker.ui.auth.UserViewModel
import com.app.hydratracker.ui.notification.NotificationActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var fabAddEntry: FloatingActionButton
    private lateinit var waveView: WaveProgressView
    var consommationList = mutableListOf<DailyTarget>()
    var theTarget = 0
    private lateinit var waterIntake: WaterIntakeDao

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val database = AppDatabase.getDatabase(requireContext())
        waterIntake = database.waterIntakeDao()


        val currentDay = database.dailyTargetDao()

        val sharedPreferences = requireContext().getSharedPreferences("WaterIntakePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val userViewModel = UserViewModel(requireContext())
        userViewModel.getUserInfo()

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewDaily)
        val userName: TextView = view.findViewById(R.id.tv_username)


        userViewModel.isDataLoaded.observe(viewLifecycleOwner, Observer { isLoaded ->
            if (isLoaded) {
                userName.text = userViewModel.username.value.toString()
            } else {
                Toast.makeText(
                    requireContext(),
                    userViewModel.failed.value.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        barChart = view.findViewById(R.id.chart_water_consumption)
        fabAddEntry = view.findViewById(R.id.fab)
        waveView = view.findViewById(R.id.waveView)
        val notificationLogo = view.findViewById<ImageView>(R.id.notification_logo)
        val notificationRed = view.findViewById<ImageView>(R.id.notification_red)
        val target: CardView = view.findViewById(R.id.target)
        val targetText: TextView = view.findViewById(R.id.targetText)



        CoroutineScope(Dispatchers.Main).launch {
            consommationList = currentDay.getAllTargets().toMutableList()
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            var progress = 0
            consommationList.forEach { target->
                if (target.isdrinked){
                    progress += target.targetAmount
                }
            }
            waveView.setProgress(progress.toFloat())
            val adaptr = ConsommationAdapter(consommationList)
            recyclerView.adapter = adaptr
            adaptr.notifyItemInserted(consommationList.size - 1)
        }
        theTarget = sharedPreferences.getInt("theTarget", 0)

        notificationLogo.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                currentDay.deleteAllTargets()
                currentDay.insertTarget(DailyTarget(0, 900, "05:00", true))
                currentDay.insertTarget(DailyTarget(2, 250, "18:35", true))
                currentDay.insertTarget(DailyTarget(1, 400, "19:40", false))
                currentDay.insertTarget(DailyTarget(4, 200, "22:10", false))
            }
            notificationRed.visibility = View.GONE
            Intent(requireContext(), NotificationActivity::class.java).also { startActivity(it) }
        }

        if (theTarget > 0) {
            targetText.text = "${theTarget} ml"
        }


        target.setOnClickListener {
            showAddConsumptionDialog("Entrez votre Target", "daiyli target") { consumption ->
                if (consumption != null) {
                    if (consumption > 4000) {
                        Toast.makeText(
                            requireContext(),
                            "Consumption must be under 4L",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (consumption < 1000) {
                        Toast.makeText(
                            requireContext(),
                            "Consumption must be at least 1L",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        theTarget = consumption
                        waveView.setMaxProgress(theTarget.toFloat())
                        targetText.text = "${consumption} ml"
                        editor.putInt("theTarget", consumption)
                        editor.apply()
                    }
                } else {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                }
            }

        }
        waveView.setMaxProgress(theTarget.toFloat())

        fabAddEntry.setOnClickListener {
            if (theTarget == 0) {
                showAddConsumptionDialog("Entrez votre Target", "daiyli target") { consumption ->
                    if (consumption != null) {
                        if (consumption > 4000) {
                            Toast.makeText(
                                requireContext(),
                                "Consumption must be under 4L",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (consumption < 1000) {
                            Toast.makeText(
                                requireContext(),
                                "Consumption must be at least 1L",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            theTarget = consumption
                            targetText.text = "${consumption} ml"
                            editor.putInt("theTarget", consumption)
                            editor.apply()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                showAddConsumptionDialog(
                    "Entrez votre consommation en ml",
                    "Ajouter votre consommation"
                ) { consumption ->
                    if (consumption != null && consumption < theTarget) {
                        showDateTimePicker { hour, minute ->
                            val formattedTime = String.format("%02d:%02d", hour, minute)
                            waveView.setMaxProgress(theTarget.toFloat())
                            scheduleNotification(
                                requireContext(),
                                hour,
                                minute,
                                data = Data.Builder().putString("time", formattedTime).build()
                            )

                            CoroutineScope(Dispatchers.IO).launch {
                                currentDay.insertTarget(
                                    DailyTarget(
                                        consommationList.size,
                                        consumption,
                                        formattedTime,
                                        false
                                    )
                                )
                                consommationList = currentDay.getAllTargets().toMutableList()
                                requireActivity().runOnUiThread {
                                    recyclerView.layoutManager =
                                        LinearLayoutManager(requireContext())
                                    val adaptr = ConsommationAdapter(consommationList)
                                    recyclerView.adapter = adaptr
                                    adaptr.notifyItemInserted(consommationList.size - 1)
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Consumption must be under 4000 ml",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        }

        val entries = ArrayList<BarEntry>()
        //entries.add(BarEntry((index+1).toFloat(), day.amount.toFloat()))

        CoroutineScope(Dispatchers.Main).launch {
            waterIntake.deleteAllTargets()
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-01", amount = 2300, target = 3000))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-02", amount = 3000, target = 3000))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-03", amount = 1700, target = 2500))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-04", amount = 1500, target = 2500))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-05", amount = 1600, target = 2500))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-06", amount = 2500, target = 2500))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-07", amount = 1200, target = 2500))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-08", amount = 2400, target = 3000))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-09", amount = 3000, target = 3000))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-10", amount = 1300, target = 3000))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-11", amount = 2800, target = 3000))
            waterIntake.insertWaterIntake(WaterIntake(date = "2025-03-12", amount = 1200, target = 2000))
            val getData  = waterIntake.getAllWaterIntakes()
            val lastSevenDay  = getData.takeLast(6)
            lastSevenDay.forEachIndexed{ index, day->
                entries.add(BarEntry((index+1).toFloat(), day.amount.toFloat()))
            }
            drawChar(entries)
        }



        return view
    }

    private fun scheduleNotification(context: Context, hour: Int, minute: Int, data: Data) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        val delay = calendar.timeInMillis - System.currentTimeMillis()
        val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }


    private fun showDateTimePicker(onTimeSelected: (hour: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                onTimeSelected(hourOfDay, minute)
            },
            currentHour,
            currentMinute,
            true
        )

        timePickerDialog.show()
    }

    private fun showAddConsumptionDialog(
        hint: String,
        title: String,
        onConsumptionAdded: (Int?) -> Unit
    ) {
        val editText = EditText(requireActivity())
        editText.hint = hint
        editText.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("Ajouter") { dialog, _ ->
                val inputText = editText.text.toString().trim()
                if (inputText.isNotEmpty()) {
                    val consumption = inputText.toIntOrNull()
                    onConsumptionAdded(consumption)
                } else {
                    onConsumptionAdded(null)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Annuler") { dialog, _ ->
                onConsumptionAdded(null)
                dialog.dismiss()
            }
            .show()
    }


    fun getSortedLabels(): Array<String> {
        val labels = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val currentDayIndex = when (currentDayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> throw IllegalStateException("Invalid day of week")
        }

        return labels.sliceArray(currentDayIndex + 1 until labels.size) + labels.sliceArray(0..currentDayIndex)
    }

    fun drawChar(entries: ArrayList<BarEntry>) {
        val entries = entries

        val labels = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        Log.d("this", labels.size.toString())
        val barDataSet = BarDataSet(entries, "Daily Sales")
        val legend = barChart.legend
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.textColo)
        val xAxis = barChart.xAxis
        barDataSet.color = ContextCompat.getColor(requireContext(), R.color.blue)
        barDataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textColo)
        val barData = BarData(barDataSet)
        barChart.data = barData
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textColo)
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }
}
