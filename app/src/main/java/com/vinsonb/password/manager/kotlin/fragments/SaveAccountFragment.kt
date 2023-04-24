package com.vinsonb.password.manager.kotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.extensions.showToast
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountScreen
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountViewModel
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent
import com.vinsonb.password.manager.kotlin.utilities.withComposeView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "SaveAccountFragment"

@AndroidEntryPoint
class SaveAccountFragment : Fragment() {
    private val viewModel: SaveAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        lifecycleScope.launch {
            viewModel.eventFlow.collect {
                when (it) {
                    SimpleToastEvent.None -> {}
                    SimpleToastEvent.ShowFailed ->
                        requireContext().showToast(R.string.error_save_unsuccessful)
                    SimpleToastEvent.ShowSucceeded ->
                        requireContext().showToast(R.string.success_save_account)
                }
            }
        }

        return withComposeView(requireContext()) {
            SaveAccountScreen(viewModel = viewModel)
        }
    }
}
