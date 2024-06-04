package com.aura.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aura.R
import com.aura.databinding.FragmentLoginBinding
import com.aura.ui.home.HomeFragment

/**
 * The login fragment for the app.
 */
class LoginFragment : Fragment() {

  /**
   * The binding for the login layout.
   */
  private var _binding: FragmentLoginBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentLoginBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val login = binding.login
    val loading = binding.loading

    login.setOnClickListener {
      loading.visibility = View.VISIBLE

      // Navigate to HomeFragment
      requireActivity().supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, HomeFragment())
        .addToBackStack(null)
        .commit()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}