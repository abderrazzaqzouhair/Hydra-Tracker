package com.app.hydratracker.ui.profile

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import com.app.hydratracker.HomeActivity
import com.app.hydratracker.R
import com.app.hydratracker.ui.auth.AuthActivity
import com.app.hydratracker.ui.auth.AuthManager
import com.app.hydratracker.ui.auth.UserViewModel
import java.util.Locale

class ProfileFragment : Fragment() {
     lateinit var authManager:AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        resources.updateConfiguration(config, resources.displayMetrics)

        authManager = AuthManager(this)
        authManager.initialize()
    }

    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode", "CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)
        val sharedPrefs = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupLanguage)
        val btnConfirmLan = view.findViewById<Button>(R.id.btnConfirm)
        val btnConfirmPas = view.findViewById<Button>(R.id.btnConfirme)
        val btnConfirmInfo = view.findViewById<Button>(R.id.btnConfirmInfo)
        val selectLanguage:FrameLayout = view.findViewById(R.id.selectLanguage)
        val selectLang:LinearLayout = view.findViewById(R.id.selectLang)
        val changePassword:LinearLayout = view.findViewById(R.id.changePassword)
        val cardLanguageSelection:CardView = view.findViewById(R.id.cardLanguageSelection)
        val cardPasswordChange:CardView = view.findViewById(R.id.cardPasswordChange)
        val notificationAccess: Switch = view.findViewById(R.id.notificationAccess)
        val darkMode: Switch = view.findViewById(R.id.darkMode)
        val aboutUs:LinearLayout= view.findViewById(R.id.aboutUs)
        val logOut : CardView = view.findViewById(R.id.logOut)
        val userName:EditText = view.findViewById(R.id.userName)
        val userEmail:EditText = view.findViewById(R.id.userEmail)
        val name:TextView = view.findViewById(R.id.name)
        val email:TextView = view.findViewById(R.id.email)

        val userViewModel = UserViewModel(requireContext())
        userViewModel.getUserInfo()
        userViewModel.isDataLoaded.observe(viewLifecycleOwner, Observer { isLoaded ->
            if (isLoaded) {
                name.text = userViewModel.username.value.toString()
                email.text = userViewModel.email.value.toString()

            } else {
                Toast.makeText(requireContext(), userViewModel.failed.value.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        btnConfirmInfo.setOnClickListener {

        }
        logOut.setOnClickListener {
            authManager.logout{
                Intent(requireActivity(), AuthActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

        aboutUs.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.nav_host_fragment, AboutFragment())
                ?.commit()
        }



        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        darkMode.setOnCheckedChangeListener(null)
        darkMode.isChecked = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        darkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("isDarkMode", true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("isDarkMode", false)

            }
            editor.apply()
            val intent = Intent(requireActivity(), HomeActivity::class.java)
            intent.putExtra("EXTRA_SEND_PROFILE", true)
            startActivity(intent)
            requireActivity().finish()
        }

        val isNotificationEnabled = sharedPrefs.getBoolean("isNotificationEnabled", false)
        notificationAccess.isChecked = isNotificationEnabled
        notificationAccess.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableNotifications()
                editor.putBoolean("isNotificationEnabled", true)

            } else {
                disableNotifications()
                editor.putBoolean("isNotificationEnabled", false)
            }
            editor.apply()
        }



        changePassword.setOnClickListener {
            selectLanguage.visibility = View.VISIBLE
            cardPasswordChange.visibility = View.VISIBLE
        }
        btnConfirmPas.setOnClickListener {
            selectLanguage.visibility = View.GONE
            cardPasswordChange.visibility = View.GONE
        }


        selectLang.setOnClickListener{
            selectLanguage.visibility = View.VISIBLE
            cardLanguageSelection.visibility = View.VISIBLE
        }
        btnConfirmLan.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val selectedLanguage = when (selectedId) {
                R.id.rbArabic -> "ar"
                R.id.rbFrench -> "fr"
                R.id.rbEnglish -> "en"
                else -> return@setOnClickListener
            }
            editor.putString("language", selectedLanguage)
            editor.apply()
            setAppLocale(selectedLanguage)
            selectLanguage.visibility = View.GONE
            cardLanguageSelection.visibility = View.GONE
            val intent = Intent(requireActivity(), HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }


        return view
    }
    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.nav_host_fragment, ProfileFragment())
            ?.commit()
    }

    private fun enableNotifications() {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel"
            val channelName = "Default Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(requireContext(), "default_channel")
            .setSmallIcon(R.drawable.baseline_water_drop_24)
            .setContentTitle("Notifications Enabled")
            .setContentText("You have enabled notifications.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(requireContext()).notify(1, builder.build())
    }

    private fun disableNotifications() {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

}