package com.aura.ui.transfer

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
import com.aura.databinding.FragmentTransferBinding
import com.aura.ui.account.AccountFragment
import com.aura.ui.account.AccountViewModel
import com.aura.ui.login.LoginState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Fragment responsible for handling money transfer operations.
 */
class TransferFragment : Fragment() {

    /**
     * The binding for the transfer layout.
     */
    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel for user account.
     */
    private val viewModel: TransferViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the state flow
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        // Show initial state
                        is TransferState.Initial -> {

                            binding.pbTransferLoading.visibility = View.GONE

                            // Update the ViewModel with changes in the transfer recipient field
                            binding.fieldRecipient.addTextChangedListener { text ->
                                viewModel.onFieldTransferRecipientChanged(
                                    text.toString()
                                )
                            }

                            // Update the ViewModel with changes in the transfer amount field
                            binding.fieldAmount.addTextChangedListener { text ->
                                val amount = text?.toString()?.toDoubleOrNull()
                                viewModel.onFieldTransferAmountChanged(amount ?: 0.0)
                            }


                            // Observe the login make transfer enabled state and update the UI
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.isButtonMakeTransferEnabled.collectLatest { isEnabled ->
                                    binding.buttonMakeTransfer.isEnabled = isEnabled
                                }
                            }

                            // Handle the login button click
                            binding.buttonMakeTransfer.setOnClickListener {
                                viewModel.onButtonMakeTransferClicked()
                            }
                        }

                        // Show loading state
                        is TransferState.Loading -> {
                            binding.pbTransferLoading.visibility = View.VISIBLE
                            binding.buttonMakeTransfer.isEnabled = false
                        }

                        // Show success state
                        is TransferState.Success -> {

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
                        is TransferState.Error -> {

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}