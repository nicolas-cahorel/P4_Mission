package com.aura.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aura.databinding.FragmentLoginBinding

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

        // Retrieving views from the binding
        val login = binding.login
        val loading = binding.loading

        // Handling click on the login button
        login.setOnClickListener {
            // Display loading
            loading.visibility = View.VISIBLE

            // Call the navigateToHome function of the ViewModel
            viewModel.navigateToHome()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
