package com.aura.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.aura.R
import com.aura.databinding.FragmentLoginBinding
import com.aura.ui.account.AccountFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


/**
 * Fragment responsible for handling user login.
 */
class LoginFragment : Fragment() {

    /**
     * The binding for the login layout.
     */
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // ViewModel associated with the fragment
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update the ViewModel with changes in the identifier field
        binding.fieldUserIdentifier.addTextChangedListener { text ->
            viewModel.onFieldUserIdentifierChanged(
                text.toString()
            )
        }

        // Update the ViewModel with changes in the password field
        binding.fieldUserPassword.addTextChangedListener { text ->
            viewModel.onFieldUserPasswordChanged(
                text.toString()
            )
        }

        // Observe the login button enabled state and update the UI
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isButtonLoginEnabled.collectLatest { isEnabled ->
                binding.buttonLogin.isEnabled = isEnabled
            }
        }

        // Handle the login button click
        binding.buttonLogin.setOnClickListener {
            val progressBarLoading = binding.loginProgressBarLoading
            progressBarLoading.visibility = View.VISIBLE

            binding.buttonLogin.isEnabled = false

            viewModel.onButtonLoginClicked()
        }

        // Observe the navigateToHomeEvent event from the ViewModel
        viewModel.navigateToAccountEvent.onEach {
            // Replace the current fragment with HomeFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AccountFragment())
                .addToBackStack(null)
                .commit()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        // Observe the errorMessage event from the ViewModel to show toast messages
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    // Hide the progress bar and re-enable the login button
                    binding.loginProgressBarLoading.visibility = View.GONE
                    binding.buttonLogin.isEnabled = true
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
