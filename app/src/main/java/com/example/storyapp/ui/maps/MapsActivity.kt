package com.example.storyapp.ui.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryInfoWindowAdapter
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.model.StoryEntity
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.ui.detail.DetailActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel by viewModels<MapsViewModel> {
        MapsViewModelFactory.getInstance(this)
    }
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.setInfoWindowAdapter(StoryInfoWindowAdapter(this))
        mMap.setOnInfoWindowClickListener { marker ->
            val story = marker.tag as StoryEntity
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.STORY_EXTRA, story)
            startActivity(intent)
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-5.0, 105.0), 5f))
        setMapStyle(googleMap)

        loadStoriesIntoMap(googleMap)
    }

    private fun setupMarkers(googleMap: GoogleMap, stories: List<StoryEntity>) {
        stories.forEach { story ->
            val marker = MarkerOptions()
            marker.position(LatLng(story.lat, story.lon))
                .title(story.name)
                .snippet(story.description)

            val markerTag = googleMap.addMarker(marker)
            markerTag?.tag = story
        }
    }

    private fun loadStoriesIntoMap(googleMap: GoogleMap) {
        viewModel.getToken().observe(this) { token ->
            if (token != "null") {
                val myToken = "Bearer $token"
                viewModel.getStory(myToken).observe(this) {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Error -> {}
                        is Result.Success -> {
                            setupMarkers(googleMap, it.data)
                        }
                    }
                }
            }
        }
    }

    private fun setMapStyle(mMap: GoogleMap) {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this,
                        R.raw.map_style
                    )
                )
            if (!success) {
                Toast.makeText(
                    this,
                    getString(R.string.failed_load_map),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.failed_load_map),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}