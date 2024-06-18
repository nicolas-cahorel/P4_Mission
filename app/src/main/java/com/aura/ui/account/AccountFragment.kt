package com.aura.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.FragmentAccountBinding
import com.aura.ui.login.LoginFragment
import com.aura.ui.transfer.TransferFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Fragment for displaying the user's account information.
 */
class AccountFragment : Fragment() {

    /**
     * The binding for the account layout.
     */
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel for user account.
     */
    private val viewModel: UserAccountViewModel by viewModel()

    /**
     * Creates and returns the view hierarchy associated with this fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called when the fragment's activity has been created and the fragment's view hierarchy instantiated.
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
                        is AccountState.Loading -> {
                            // Show loading state
                            binding.pbAccountLoading.visibility = View.VISIBLE
                            binding.tvBalance.visibility = View.GONE
                            binding.buttonTransfer.isEnabled = false
                            binding.buttonReload.visibility = View.GONE
                        }

                        is AccountState.Success -> {
                            // Show success state
                            binding.pbAccountLoading.visibility = View.GONE
                            binding.tvBalance.visibility = View.VISIBLE
                            binding.tvBalance.text = getString(R.string.account_balance, state.balance)
                            binding.buttonTransfer.isEnabled = true

                            // Handle the transfer button click
                            binding.buttonTransfer.setOnClickListener {
                                viewModel.navigateToTransfer()

                                // Observe the navigateToTransferEvent event from the ViewModel
                                viewModel.navigateToTransferEvent.onEach {
                                    // Replace the current fragment with TransferFragment
                                    requireActivity().supportFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, TransferFragment())
                                        .addToBackStack(null)
                                        .commit()
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }
                        }

                        is AccountState.Error -> {
                            // Show error state
                            binding.pbAccountLoading.visibility = View.GONE
                            binding.buttonReload.visibility = View.VISIBLE
                            binding.buttonReload.isEnabled = true

                            viewModel.errorMessage.collect { message ->
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                                // Handle the login button click
                                binding.buttonReload.setOnClickListener {
                                    binding.pbAccountLoading.visibility = View.VISIBLE
                                    viewModel.onButtonReloadClicked()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called to do initial creation of the fragment.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /**
     * Initialize the contents of the fragment's standard options menu.
     * @param menu The options menu in which you place your items.
     * @param inflater The MenuInflater object that can be used to inflate any views in the fragment.
     */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * This hook is called whenever an item in the options menu is selected.
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.disconnect -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }

            else -> super.onOptionsItemSelected(item)
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
