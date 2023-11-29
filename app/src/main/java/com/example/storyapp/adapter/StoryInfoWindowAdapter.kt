package com.example.storyapp.adapter

import android.app.Activity
import android.view.View
import com.example.storyapp.R
import com.example.storyapp.databinding.InfoWindowBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class StoryInfoWindowAdapter(private val context: Activity) :
    GoogleMap.InfoWindowAdapter {
    private val binding: InfoWindowBinding by lazy {
        InfoWindowBinding.inflate(context.layoutInflater)
    }

    override fun getInfoContents(marker: Marker): View {
        binding.tvItemName.text = context.getString(R.string.story_map_title).format(marker.title)
        binding.tvItemDesc.text = marker.snippet

        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}