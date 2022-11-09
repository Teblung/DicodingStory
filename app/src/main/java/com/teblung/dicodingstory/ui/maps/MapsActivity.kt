package com.teblung.dicodingstory.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.data.source.local.preference.DataStoreVM
import com.teblung.dicodingstory.databinding.ActivityMapsBinding
import com.teblung.dicodingstory.ui.home.upload.UploadActivity
import com.teblung.dicodingstory.ui.home.upload.UploadActivity.Companion.LATITUDE_POINT
import com.teblung.dicodingstory.ui.home.upload.UploadActivity.Companion.LONGITUDE_POINT
import com.teblung.dicodingstory.ui.home.upload.UploadActivity.Companion.MY_LOCATION_TO_SHARE
import com.teblung.dicodingstory.ui.home.upload.UploadActivity.Companion.UPLOAD_REQUEST
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val binding: ActivityMapsBinding by lazy {
        ActivityMapsBinding.inflate(layoutInflater)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {}
            }
        }

    private val mapsVM by viewModels<MapsVM>()
    private val dataStoreVM by viewModels<DataStoreVM>()

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    enum class TYPE { FeatureName, AddressLine }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        supportActionBar?.title = getString(R.string.maps)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLastLocation()
        setMapStyle()

        mMap.setOnMapLongClickListener { latLng ->
            Log.d(TAG, "getAddressNameByLongClick: ${latLng.latitude}, ${latLng.longitude}")
            val addressLine = getAddressName(latLng.latitude, latLng.longitude, TYPE.AddressLine)
            val featureName = getAddressName(latLng.latitude, latLng.longitude, TYPE.FeatureName)
            mMap.addMarker(MarkerOptions().position(latLng).title(featureName).snippet(addressLine))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
        }

        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    showStartMarker(location)
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        latitude = location.latitude
        longitude = location.longitude
        mMap.animateCamera(CameraUpdateFactory.newLatLng(startLocation))
    }

    private fun getAddressName(lat: Double, lon: Double, type: TYPE): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        if (type == TYPE.AddressLine) {
            try {
                val list = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.size != 0) {
                    addressName = list[0].getAddressLine(0)
                    Log.d(TAG, "getAddressName: $addressName")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            try {
                val list = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.size != 0) {
                    addressName = list[0].featureName
                    Log.d(TAG, "getAddressName: $addressName")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return addressName
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting, menu)
        if (menu != null) {
            menu.findItem(R.id.settings).isVisible = false
            menu.findItem(R.id.logout).isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                startActivity(Intent(this, UploadActivity::class.java).apply {
                    putExtra(UPLOAD_REQUEST, MY_LOCATION_TO_SHARE)
                    putExtra(LATITUDE_POINT, latitude)
                    putExtra(LONGITUDE_POINT, longitude)
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        dataStoreVM.apply {
            getLoginSession().observe(this@MapsActivity) {
                mapsVM.apply {
                    getStoryWithLocation(it.token)
                    listStoryData.observe(this@MapsActivity) {
                        for (i in it) {
                            val latLng = LatLng(i.lat, i.lon)
                            if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
                                mMap.addMarker(
                                    MarkerOptions().position(latLng).title(i.name)
                                        .snippet(i.description)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        val TAG: String = MapsActivity::class.java.simpleName
    }
}