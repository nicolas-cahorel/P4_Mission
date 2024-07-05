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
import com.aura.R
import com.aura.databinding.FragmentLoginBinding
import com.aura.ui.account.AccountFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Fragment responsible for handling user login.
 *
 * This fragment manages the UI for user login, including handling user input for
 * identifier and password, observing login state changes from [LoginViewModel],
 * and navigating to the account screen upon successful login.
 */
class LoginFragment : Fragment() {

    /**
     * The binding for the login layout.
     */
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // ViewModel associated with the fragment
    private val viewModel: LoginViewModel by viewModel()

    /**
     * Creates and returns the view hierarchy associated with this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called when the fragment's activity has been created and the fragment's view hierarchy instantiated.
     *
     * @param view The View returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the state flow
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {

                        // Show initial state
                        is LoginState.Initial -> {
                            binding.pbLoginLoading.visibility = View.GONE

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
                                viewModel.onButtonLoginClicked()
                            }
                        }

                        // Show loading state
                        is LoginState.Loading -> {
                            binding.pbLoginLoading.visibility = View.VISIBLE
                            binding.buttonLogin.isEnabled = false
                        }

                        // Show success state
                        is LoginState.Success -> {
                            // Observe the navigateToHomeEvent event from the ViewModel
                            viewModel.navigateToAccountEvent.onEach {
                                // Replace the current fragment with HomeFragment
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, AccountFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }.launchIn(viewLifecycleOwner.lifecycleScope)
                        }

                        // Show error state
                        is LoginState.Error -> {
                            // Hide the progress bar and re-enable the login button
                            binding.pbLoginLoading.visibility = View.GONE
                            binding.buttonLogin.isEnabled = true
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the fragment.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}