package com.vinsonb.password.manager.kotlin.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.adapter.AccountAdapter
import com.vinsonb.password.manager.kotlin.databinding.FragmentViewAccountsBinding
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import com.vinsonb.password.manager.kotlin.viewmodels.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ViewAccountsFragment"

@AndroidEntryPoint
class ViewAccountsFragment : Fragment(R.layout.fragment_view_accounts) {
    private var _binding: FragmentViewAccountsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user is authenticated
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)
        if (sharedPreferences != null) {
            validateAuthentication(view, sharedPreferences)
        }

        // Recycler view
        binding.recyclerViewAccounts.adapter = AccountAdapter(parentFragmentManager, viewModel)

        // Observer on account changes
        viewModel.accounts.observe(viewLifecycleOwner) {
            (binding.recyclerViewAccounts.adapter as AccountAdapter).updateAccountList(it)
        }

        // Search functionality for accounts or platform
        binding.inputSearch.addTextChangedListener { text ->
            viewModel.accounts.value?.filter {
                it.username.contains(text.toString(), ignoreCase = true) ||
                        it.platform.contains(text.toString(), ignoreCase = true)
            }.also {
                if (it != null) {
                    (binding.recyclerViewAccounts.adapter as AccountAdapter).updateAccountList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Navigate to login fragment if user is not authenticated.
     */
    private fun validateAuthentication(view: View, sharedPreferences: SharedPreferences) {
        val isAuthenticated = sharedPreferences.getBoolean(AUTHENTICATED_KEY, false)
        if (!isAuthenticated) {
            view.findNavController().navigate(R.id.action_viewAccountsFragment_to_loginFragment2)
        }
    }
}