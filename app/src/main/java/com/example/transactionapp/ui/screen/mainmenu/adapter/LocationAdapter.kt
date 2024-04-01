import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.transactionapp.ui.viewmodel.location.LocationModel
import com.example.transactionapp.ui.viewmodel.location.LocationViewModel
import com.google.android.gms.location.*

class LocationAdapter(
    private val contextProvider: () -> Context,
    private val locationViewModel: LocationViewModel
) {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.S)
    fun startLocationUpdates() {
        if (hasLocationPermission()) {
            requestLocationUpdates()
        } else {
            requestLocationPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun requestLocationUpdates() {
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(contextProvider())
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        } catch (e: SecurityException) {
            Toast.makeText(contextProvider(), "Location permission denied", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location? = locationResult.lastLocation
            if (lastLocation != null) {
                locationViewModel.setLocation(
                    LocationModel(
                        locationName = getCityName(lastLocation.latitude, lastLocation.longitude),
                        latitude = lastLocation.latitude,
                        longitude = lastLocation.longitude
                    )
                )
            }
        }
    }

    private fun getCityName(lat: Double, long: Double): String {
        var cityName = ""
        val geoCoder = Geocoder(contextProvider())
        val address = geoCoder.getFromLocation(lat, long, 3)
        cityName = address?.get(0)?.subAdminArea ?: ""
        return cityName
    }

    fun hasLocationPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            contextProvider(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    contextProvider(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        if (contextProvider() is Activity) {
            ActivityCompat.requestPermissions(
                contextProvider() as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1010
    }
}