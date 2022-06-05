package com.vinsonb.password.manager.kotlin.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.databinding.FragmentLoginBinding
import com.vinsonb.password.manager.kotlin.viewmodels.LoginViewModel

private const val TAG = "LoginFragment"

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val viewModel: LoginViewModel by lazy {
        LoginViewModel()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var buttonList: MutableList<Button> = mutableListOf()
    private var circleList: MutableList<ImageView> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Number 0 - 9 buttons
        populateButtonList()
        buttonList.forEach { it ->
            it.setOnClickListener {
                numberButtonClick(it)
            }
        }

        // Clear button
        binding.buttonClear.setOnClickListener {
            viewModel.clearLastDigit()
        }

        // TODO - Forgot password button

        // ImageViews 5 circles
        populateCircleList()
        setCircleIcon()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Puts all number buttons in one collection for ease of access.
     */
    private fun populateButtonList() {
        buttonList.add(binding.button0)
        buttonList.add(binding.button1)
        buttonList.add(binding.button2)
        buttonList.add(binding.button3)
        buttonList.add(binding.button4)
        buttonList.add(binding.button5)
        buttonList.add(binding.button6)
        buttonList.add(binding.button7)
        buttonList.add(binding.button8)
        buttonList.add(binding.button9)
    }

    /**
     * Click listener for number buttons to enter the digit clicked as part of password.
     */
    private fun numberButtonClick(view: View) {
        val digit = (view as Button).text.toString().toInt()
        viewModel.addDigitToPassword(digit)
    }

    /**
     * Puts all circles in one collection for ease of access.
     */
    private fun populateCircleList() {
        circleList.add(binding.circle1)
        circleList.add(binding.circle2)
        circleList.add(binding.circle3)
        circleList.add(binding.circle4)
        circleList.add(binding.circle5)
    }

    /**
     * Dynamically fill / un-fill circle icons depending on number press and clear.
     */
    private fun setCircleIcon() {
        viewModel.getPassword().observe(viewLifecycleOwner) {
            for (i in 0 until 5) {
                if (i < it.size) {
                    circleList[i].setImageResource(R.drawable.ic_circle_filled)
                } else {
                    circleList[i].setImageResource(R.drawable.ic_circle_outlined)
                }
            }
        }
    }
}