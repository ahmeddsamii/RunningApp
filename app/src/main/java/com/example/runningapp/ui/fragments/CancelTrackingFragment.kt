package com.example.runningapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.runningapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingFragment: DialogFragment() {

    private var yesListener: (() -> Unit) ? = null

    fun setYesListener(listener: () -> Unit){
        yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure you want to cancel this run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("yes"){_, _->
                yesListener?.let { yes ->
                    yes()
                }
            }
            .setNegativeButton("No"){dialogInterface,_ ->
                dialogInterface.cancel()
            }
            .create()
    }
}