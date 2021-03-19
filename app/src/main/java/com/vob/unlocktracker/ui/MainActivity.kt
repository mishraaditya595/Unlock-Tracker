package com.vob.unlocktracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.vob.unlocktracker.R
import com.vob.unlocktracker.databinding.ActivityMainBinding
import com.vob.unlocktracker.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navHostFragment: NavHostFragment
    @set:Inject
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = "Let's go, $name"

        navHostFragment = supportFragmentManager.findFragmentByTag("nav_host_frag") as NavHostFragment

        navigateToTrackingFragment(intent)

        //setSupportActionBar(binding.toolbar)
        binding.bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        navHostFragment.findNavController()
                .addOnDestinationChangedListener { controller, destination, arguments ->
                    when(destination.id)
                    {
                        R.id.settingsFragment, R.id.runFragment, R.id.statisticsFragment ->
                            binding.bottomNavigationView.visibility = View.VISIBLE
                        else -> binding.bottomNavigationView.visibility = View.GONE
                    }
                }
    }

    private fun navigateToTrackingFragment(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT)
        {
            navHostFragment.findNavController().navigate(R.id.action_global_tracking_fragment)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //in case the main activity gets destroyed
        navigateToTrackingFragment(intent)
    }


}