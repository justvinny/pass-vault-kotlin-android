package com.vinsonb.password.manager.kotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vinsonb.password.manager.kotlin.ui.features.passwordgenerator.PasswordGeneratorScreen
import com.vinsonb.password.manager.kotlin.utilities.withComposeView

class GeneratePasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = withComposeView(requireContext()) {
        PasswordGeneratorScreen()
    }
}
