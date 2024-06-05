package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.FragmentHomeBinding
import com.aura.ui.login.LoginFragment
import com.aura.ui.transfer.TransferFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


/**
 * The home fragment for the app.
 */
class HomeFragment : Fragment() {

    /**
     * The binding for the home layout.
     */
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel associated with this fragment
    private val viewModel: HomeViewModel by viewModels()

    /**
     * A callback for the result of starting the TransferActivity.
     */
    private val startTransferActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //TODO: Handle the result
        }

    /**
     * Creates and returns the view hierarchy associated with this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called when the fragment's activity has been created and the fragment's view hierarchy instantiated.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  Observe the balance flow
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.balance
                    .onEach { balance ->
                        binding.balance.text = balance
                    }
                    .collect() // Collect the flow
            }
        }

        // Set click listener for the transfer button
        binding.transfer.setOnClickListener {
            viewModel.onTransferClicked()
            // Replace the current fragment with TransferFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TransferFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    /**
     * Called to do initial creation of the fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /**
     * Initialize the contents of the fragment's standard options menu.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * This hook is called whenever an item in the options menu is selected.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.disconnect -> {
                startActivity(Intent(requireActivity(), LoginFragment::class.java))
                requireActivity().finish()
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