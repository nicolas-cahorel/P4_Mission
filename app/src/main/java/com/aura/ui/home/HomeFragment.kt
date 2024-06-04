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
import com.aura.R
import com.aura.databinding.FragmentHomeBinding
import com.aura.ui.login.LoginFragment
import com.aura.ui.transfer.TransferFragment

/**
 * The home fragment for the app.
 */
class HomeFragment : Fragment() {

  /**
   * The binding for the home layout.
   */
  private var _binding: FragmentHomeBinding? = null
  private val binding get() = _binding!!

  /**
   * A callback for the result of starting the TransferActivity.
   */
  private val startTransferActivityForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      //TODO
    }

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val balance = binding.balance
    val transfer = binding.transfer

    balance.text = "2654,54€"

    transfer.setOnClickListener {
      // Replace the current fragment with TransferFragment
      requireActivity().supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, TransferFragment())
        .addToBackStack(null)
        .commit()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.home_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

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

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}