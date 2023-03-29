package com.vinsonb.password.manager.kotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vinsonb.password.manager.kotlin.ui.saveaccount.SaveAccountScreen
import com.vinsonb.password.manager.kotlin.ui.saveaccount.SaveAccountViewModel
import com.vinsonb.password.manager.kotlin.utilities.withComposeView
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SaveAccountFragment"

@AndroidEntryPoint
class SaveAccountFragment : Fragment() {
    private val viewModel: SaveAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = withComposeView(requireContext()) {
        SaveAccountScreen(viewModel = viewModel)
    }
}
