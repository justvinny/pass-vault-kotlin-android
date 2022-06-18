package com.vinsonb.password.manager.kotlin.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.databinding.FragmentGeneratePasswordBinding
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.MAX_LENGTH
import com.vinsonb.password.manager.kotlin.viewmodels.GeneratePasswordViewModel

private const val TAG = "GeneratePasswordFragment"
private const val CLIP_LABEL = "Password Length"

class GeneratePasswordFragment : Fragment(R.layout.fragment_generate_password) {
    private var _binding: FragmentGeneratePasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GeneratePasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observer
        viewModel.generatedPassword.observe(viewLifecycleOwner) {
            binding.textGeneratedPassword.text = it
        }

        // Button listeners
        binding.imageCopy.setOnClickListener {
            copyToClipboard(view.context)
        }

        binding.buttonGeneratePassword.setOnClickListener {
            viewModel.generatePassword(
                binding.switchUppercase.isChecked,
                binding.switchLowercase.isChecked,
                binding.switchNumbers.isChecked,
                binding.switchSpecialSymbols.isChecked
            )
        }

        // EditText Listener
        binding.inputPasswordLength.addTextChangedListener {
            setInputPasswordLength(it)
        }

        // Slider Listener
        binding.sliderPasswordLength.addOnChangeListener { _, value, _ ->
            setSliderPasswordLength(value.toInt())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Copies the [GeneratePasswordViewModel.passwordLength] from [viewModel]
     * to the clipboard and notify user when successful.
     */
    private fun copyToClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(CLIP_LABEL, viewModel.generatedPassword.value.toString())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, getString(R.string.success_copied_to_clipboard), Toast.LENGTH_SHORT)
            .show()
    }

    /**
     * On change listener for inputPasswordLength.
     *
     * Sets [GeneratePasswordViewModel.passwordLength] on [viewModel] with input value.
     * Then syncs Slider with EditText value.
     */
    private fun setInputPasswordLength(editable: Editable?) {
        val passwordLength = editable.toString().toIntOrNull() ?: 0
        viewModel.setPasswordLength(passwordLength)
        if (passwordLength > MAX_LENGTH) binding.inputPasswordLength.setText(viewModel.passwordLength.value!!.toString())
        syncSliderWithEditText(viewModel.passwordLength.value!!.toFloat())
    }

    /**
     * Sync changes on inputPasswordLength value with sliderPasswordLength.
     */
    private fun syncSliderWithEditText(passwordLength: Float) {
        binding.sliderPasswordLength.value = passwordLength
    }

    /**
     * On change listener for sliderPasswordLength.
     *
     * Sets [GeneratePasswordViewModel.passwordLength] on [viewModel] with slider value.
     * Then syncs EditText with Slider value.
     */
    private fun setSliderPasswordLength(value: Int) {
        viewModel.setPasswordLength(value)
        syncEditTextWithSlider(value.toString())
    }

    /**
     * Sync changes on sliderPasswordLength value with inputPasswordLength.
     */
    private fun syncEditTextWithSlider(value: String) {
        binding.inputPasswordLength.setText(value)
        binding.inputPasswordLength.setSelection(value.length)
    }
}