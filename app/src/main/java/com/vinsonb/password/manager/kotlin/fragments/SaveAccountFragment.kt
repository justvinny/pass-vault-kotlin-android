package com.vinsonb.password.manager.kotlin.fragments

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.activities.MainActivity
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.databinding.FragmentSaveAccountBinding
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.Companion.checkInputNotEmpty
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.Companion.checkInputTextMatches
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.Companion.isNoneTextInputLayoutErrorEnabled
import com.vinsonb.password.manager.kotlin.viewmodels.AccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "SaveAccountFragment"

class SaveAccountFragment : Fragment(R.layout.fragment_save_account) {
    private lateinit var viewModel: AccountViewModel

    private var _binding: FragmentSaveAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init
        viewModel = (activity as MainActivity).getAccountViewModel()
        enableAllErrorText()

        // Observer
        viewModel.accounts.observe(viewLifecycleOwner) {
            d(TAG, it.toString())
        }

        // Input Text Change
        binding.inputPlatform.addTextChangedListener {
            checkInputNotEmpty(
                binding.layoutPlatform,
                getString(R.string.error_text_empty, binding.layoutPlatform.hint)
            )
        }

        binding.inputUsername.addTextChangedListener {
            checkInputNotEmpty(
                binding.layoutUsername,
                getString(R.string.error_text_empty, binding.layoutUsername.hint)
            )
        }

        binding.inputPassword.addTextChangedListener {
            checkInputNotEmpty(
                binding.layoutPassword,
                getString(R.string.error_text_empty, binding.layoutPassword.hint)
            )
        }

        binding.inputRepeatPassword.addTextChangedListener {
            if (binding.inputRepeatPassword.text.toString().isEmpty()) {
                checkInputNotEmpty(
                    binding.layoutRepeatPassword,
                    getString(R.string.error_text_empty, binding.layoutRepeatPassword.hint)
                )
            } else {
                checkInputTextMatches(
                    binding.inputPassword,
                    binding.inputRepeatPassword,
                    binding.layoutRepeatPassword,
                    getString(R.string.error_password_must_match)
                )
            }
        }

        // Button
        binding.buttonSaveAccount.setOnClickListener {
            saveAccount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Sets all Text Input to error enabled along with their error message.
     */
    private fun enableAllErrorText() {
        binding.layoutPlatform.isErrorEnabled = true
        binding.layoutUsername.isErrorEnabled = true
        binding.layoutPassword.isErrorEnabled = true
        binding.layoutRepeatPassword.isErrorEnabled = true
        binding.layoutPlatform.error =
            getString(R.string.error_text_empty, binding.layoutPlatform.hint)
        binding.layoutUsername.error =
            getString(R.string.error_text_empty, binding.layoutUsername.hint)
        binding.layoutPassword.error =
            getString(R.string.error_text_empty, binding.layoutPassword.hint)
        binding.layoutRepeatPassword.error =
            getString(R.string.error_text_empty, binding.layoutRepeatPassword.hint)
    }

    /**
     * Persists the account if there are no errors on all the TextInputLayout present in the form.
     * If account is persisted successfully, display a success message as a Toast.
     * Otherwise, display appropriate error message as a Toast.
     */
    private fun saveAccount() {
        val platform = binding.inputPlatform.text.toString()
        val username = binding.inputUsername.text.toString()
        val password = binding.inputPassword.text.toString()

        if (isNoneTextInputLayoutErrorEnabled(
                binding.layoutPlatform,
                binding.layoutUsername,
                binding.layoutPassword,
                binding.layoutRepeatPassword
            )
        ) {
            val account = Account(platform, username, password)

            CoroutineScope(IO).launch {
                viewModel.insertAccount(account)
                withContext(Main) {
                    clearFields()
                    Toast.makeText(
                        context,
                        getString(R.string.success_save_account, platform, username),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.error_save_unsuccessful), Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * Clears all the input fields.
     */
    private fun clearFields() {
        binding.inputPlatform.setText("")
        binding.inputUsername.setText("")
        binding.inputPassword.setText("")
        binding.inputRepeatPassword.setText("")
    }
}