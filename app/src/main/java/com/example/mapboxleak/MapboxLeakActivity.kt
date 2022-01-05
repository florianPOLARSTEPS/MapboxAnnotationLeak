package com.example.mapboxleak

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.example.mapboxleak.databinding.ActivityMapboxLeakBinding
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.plugin.Plugin
import com.mapbox.maps.viewannotation.viewAnnotationOptions

class MapboxLeakActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapboxLeakBinding

    private val mapFragment
        get() = supportFragmentManager.findFragmentByTag("map")!! as LeakMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapboxLeakBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // remove and add these fragments a couple of times (> 5) and you will see LeakCanary report the a memory leak
        binding.btRemoveFragment.setOnClickListener {
            removeFragment()
        }
        binding.btAddFragment.setOnClickListener {
            putFragment()
        }
        putFragment()

    }

    private fun removeFragment() {
        binding.btAddFragment.visibility = View.VISIBLE
        binding.btRemoveFragment.visibility = View.GONE
        supportFragmentManager.commitNow {
            this.remove(mapFragment)
        }
    }

    private fun putFragment() {
        binding.btAddFragment.visibility = View.GONE
        binding.btRemoveFragment.visibility = View.VISIBLE
        supportFragmentManager.commitNow {
            val leakMapFragment = LeakMapFragment()
            this.add(binding.mapContainer.id, leakMapFragment, "map")
        }
    }
}

class LeakMapFragment : Fragment() {

    private val mapView
        get() = requireView() as MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = MapView(
        requireContext(),
        MapInitOptions(
            requireContext(),
            textureView = true,
            plugins = listOf(
                Plugin.Mapbox(Plugin.MAPBOX_CAMERA_PLUGIN_ID),
                Plugin.Mapbox(Plugin.MAPBOX_GESTURES_PLUGIN_ID),
                Plugin.Mapbox(Plugin.MAPBOX_COMPASS_PLUGIN_ID),
                Plugin.Mapbox(Plugin.MAPBOX_LOGO_PLUGIN_ID),
                Plugin.Mapbox(Plugin.MAPBOX_ATTRIBUTION_PLUGIN_ID),
                Plugin.Mapbox(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID),
                Plugin.Mapbox(Plugin.MAPBOX_LIFECYCLE_PLUGIN_ID),
                Plugin.Mapbox(Plugin.MAPBOX_MAP_OVERLAY_PLUGIN_ID)
            ),
            resourceOptions = MapInitOptions.getDefaultResourceOptions(requireContext())
                .toBuilder()
                .accessToken(resources.getString(R.string.mapbox_access_token))
                .build(),
            mapOptions = MapInitOptions.getDefaultMapOptions(requireContext()).toBuilder().apply {
                this.optimizeForTerrain(false)
            }.build()
        )
    ).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMarkerView()
    }

    @SuppressLint("SetTextI18n")
    @OptIn(MapboxExperimental::class)
    private fun addMarkerView() {
        val annotation = TextView(requireContext())
        annotation.text = "This is a Marker"
        annotation.background = ColorDrawable(Color.WHITE)
        annotation.setTextColor(Color.BLACK)
        annotation.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        annotation.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        annotation.layout(0, 0, annotation.measuredWidth, annotation.measuredHeight)

        mapView.viewAnnotationManager.addViewAnnotation(
            annotation,
            viewAnnotationOptions {
                this.visible(true)
                this.width(annotation.measuredWidth)
                this.height(annotation.measuredHeight)
                this.anchor(ViewAnnotationAnchor.BOTTOM)
                this.geometry(fromLngLat(0.0, 0.0))
                this.allowOverlap(true)
            })
    }

}