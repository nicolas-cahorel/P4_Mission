package com.aura.ui.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aura.databinding.FragmentTransferBinding

/**
 * Fragment responsible for handling money transfer operations.
 */
class TransferFragment : Fragment() {

    /**
     * The binding for the transfer layout.
     */
    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipient = binding.recipient
        val amount = binding.amount
//        val transfer = binding.transfer
        val loading = binding.loading

//        transfer.setOnClickListener {
//            loading.visibility = View.VISIBLE
//
//            // Replace the current fragment with HomeFragment
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, UserAccountFragment())
//                .addToBackStack(null)
//                .commit()
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}