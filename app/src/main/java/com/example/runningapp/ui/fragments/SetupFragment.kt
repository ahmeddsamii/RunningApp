package com.example.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentSetupBinding
import com.example.runningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.other.Constants.KEY_NAME
import com.example.runningapp.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {
    private lateinit var binding: FragmentSetupBinding

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @set:Inject
    var isFirstTimeToOpenApp = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!isFirstTimeToOpenApp){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment,  savedInstanceState, navOptions)
        }

        binding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPreferences()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }else{
                Snackbar.make(view, "All Fields must be filled", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun writePersonalDataToSharedPreferences(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isNotEmpty() || weight.isNotEmpty()) {
            sharedPrefs.edit().putString(KEY_NAME, name).putFloat(KEY_WEIGHT, weight.toFloat())
                .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
                .apply()
            setToolbarTitle("Let's go $name!")
        }
        return true
    }


    private fun Fragment.setToolbarTitle(title: String) {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = title
    }
}