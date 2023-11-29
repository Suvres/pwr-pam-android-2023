package com.example.myapplication

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.fragments.ChildFragment
import com.example.myapplication.fragments.MenuParentFragment
import com.example.myapplication.fragments.ToDoCaregiverFragment
import com.example.myapplication.log.LoginActivity
import com.example.myapplication.users.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var userRepository: UserRepository = UserRepository()
    private val permissionId = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val locationRepository = LocationRepository()
    private val taskRepository = TasksRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.setGraph(R.navigation.nav_graph, this.intent.extras)


        auth = Firebase.auth

        configureBottomNavigation()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()


    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser === null) {
            this.startActivity(Intent(this, LoginActivity::class.java))
        }

        userRepository.getCurrentUser()?.addOnSuccessListener {
            val user = it.toObject(User::class.java);

            if (user === null) {
               return@addOnSuccessListener
            }


            if (user!!.caregiver.isEmpty()) {
                bottomNavigation.visibility = View.VISIBLE
                this.nav_host_fragment.findNavController()
                    .navigate(R.id.action_blankMenuFragment_to_menuCaregiverFragment)
            } else {
                taskRepository.getActiveTasksByUser(user) {
                    it.forEach {
                        scheduleNotification(it)
                    }
                }
                val executorService = Executors.newSingleThreadScheduledExecutor()
                executorService.scheduleAtFixedRate({
                    getLocation(uid = UserRepository.auth.currentUser!!.uid)
                    Log.d("ZAPIS LOKALIZACJI", "minęło 10min")
                }, 0, 10, TimeUnit.MINUTES)

                this.nav_host_fragment.findNavController()
                   .navigate(R.id.action_blankMenuFragment_to_menuchildFragment)
            }
       }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                UserRepository.auth.signOut()
                finish();
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item)
    }




    private fun configureBottomNavigation() {
        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNavigation)


        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_person))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_home))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_addtask))
        bottomNavigation.show(2, true)

        bottomNavigation.setOnClickMenuListener {
            var fragment: Fragment = MenuParentFragment()
            when(it.id) {
                1-> {
                    fragment = ChildFragment()
                }
                2 -> {
                    fragment = MenuParentFragment()
                }
                3 -> {
                    fragment = ToDoCaregiverFragment()
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit()
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment, fragment)
            commit()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation(uid: String) {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        val l = com.example.myapplication.users.LocationUser(latitude = list[0].latitude, longitude = list[0].longitude, user = uid)
                        locationRepository.save(l)
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun scheduleNotification(userTask: UserTask)
    {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = userTask.type.title
        val message = "Wykonaj zadanie"
        intent.putExtra(Notification.titleExtra, title)
        intent.putExtra(Notification.iconExtra, userTask.type.icon)
        intent.putExtra(Notification.messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            Notification.notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            userTask.startDate.seconds*1000,
            pendingIntent
        )
        showAlert(userTask)
    }

    private fun showAlert(userTask: UserTask)
    {
        AlertDialog.Builder(this)
            .setTitle(userTask.type.title)
            .setIcon(userTask.type.icon)
            .setMessage(
                "Należy wykonać zadanie"
            )
            .setPositiveButton("TAK"){_,_ ->}
            .show()
    }

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Notification.channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}