package com.vob.unlocktracker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vob.unlocktracker.R
import com.vob.unlocktracker.databinding.FragmentSetupBinding
import com.vob.unlocktracker.util.Constants
import com.vob.unlocktracker.util.Constants.KEY_FIRST_TIME_TOGGLE
import com.vob.unlocktracker.util.Constants.KEY_NAME
import com.vob.unlocktracker.util.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import java.util.prefs.AbstractPreferences
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    private lateinit var binding: FragmentSetupBinding
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @set:Inject
    var isFirstAppOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupBinding.bind(view)

        if (!isFirstAppOpen)
        {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()

            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }

        binding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()

            if (success)
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            else
                Snackbar.make(
                    requireView(),
                    "Name or weight cannot be left blank",
                    Snackbar.LENGTH_SHORT
                ).show()
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()

        if (name.isEmpty() || weight.isEmpty())
            return false

        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        val toolbarText = "Let's go, $name"
        (activity as AppCompatActivity).supportActionBar?.title = toolbarText

        return true
    }
}