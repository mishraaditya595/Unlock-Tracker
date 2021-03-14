package com.vob.unlocktracker.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.vob.unlocktracker.R
import com.vob.unlocktracker.databinding.ActivityMainBinding
import com.vob.unlocktracker.db.RunDAO
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentByTag("nav_host_frag") as NavHostFragment

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
}