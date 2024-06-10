package com.aura.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.FragmentLoginBinding
import com.aura.ui.home.HomeFragment
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
    private val viewModel: LoginViewModel by viewModels()

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
        binding.FieldUserIdentifier.addTextChangedListener { text ->
            viewModel.onFieldUserIdentifierChanged(
                text.toString()
            )
        }

        // Update the ViewModel with changes in the password field
        binding.FieldUserPassword.addTextChangedListener { text ->
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
            val loading = binding.loading
            loading.visibility = View.VISIBLE
            viewModel.onButtonLoginClicked()
        }

        // Observe the navigateToHomeEvent event from the ViewModel
        viewModel.navigateToHomeEvent.onEach {
            // Replace the current fragment with HomeFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
