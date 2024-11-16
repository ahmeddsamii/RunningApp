package com.example.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentFramgentSettingsBinding
import com.example.runningapp.other.Constants.KEY_NAME
import com.example.runningapp.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentSettings : Fragment() {

    private lateinit var binding: FragmentFramgentSettingsBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFramgentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPrefs()
        binding.btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPrefs()
            if (success) {
                Snackbar.make(view, "Data changes successfully", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(
                    view,
                    "There was an error while updating the data",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun loadFieldsFromSharedPrefs() {
        val name = sharedPreferences.getString(KEY_NAME, "") ?: ""
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 0.0f)

        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())
    }


    private fun applyChangesToSharedPrefs(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()

        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }

        sharedPreferences.edit().putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat()).apply()

        return true

    }
}