package com.example.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.users.LocationRepository
import com.example.myapplication.users.User
import com.example.myapplication.users.UserRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.fragment_location_child.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class LocationChildFragment : Fragment() {

    private val userRepository = UserRepository()
    private val locationRepository = LocationRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().userAgentValue = "pwr-pam-2023"
        return inflater.inflate(R.layout.fragment_location_child, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map.setTileSource(TileSourceFactory.MAPNIK)
        init()

        getLocation.setOnClickListener {
            Log.d("MAPA", "Klik w przycisk mapy")
            val userT = listChild.selectedItem as User
            locationRepository.getLocation(userT, {
                Log.d("MAPA", "Znaleźliśmy XD")

                val mapController = map.controller
                mapController.setZoom(13)

                val startPoint = GeoPoint(it.latitude, it.longitude)
                val marker = Marker(map)

                mapController.setCenter(startPoint);

                marker.position = startPoint
                marker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
                marker.title = "Dziecko: ${userT.firstName} ${userT.lastName}"
                map.overlays.add(marker)

                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map);
                locationOverlay.enableMyLocation();
                map.overlays.add(locationOverlay)

                map.invalidate()

                latlng.text = "Współrzędne: ${it.latitude},  ${it.longitude}"

            }, {

                var comment = "Dziecko nie został zlokalizowany"
                if (it !== null && it.isNotBlank()) {
                    comment = it
                }

                Snackbar.make(requireView(), comment, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(R.color.md_deep_purple_700))
                    .setActionTextColor(resources.getColor(R.color.white))
                    .show()

            })
        }
    }

    private fun init() {

        userRepository.getCurrentUserMustExist {
            userRepository.getChildren(it.uid) { users ->
                view?.findViewById<Spinner>(R.id.listChild)
                    ?.adapter =
                    ArrayAdapter<User>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item, users
                    )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause()  //needed for compass, my location overlays, v6.0.0 and up
    }
}