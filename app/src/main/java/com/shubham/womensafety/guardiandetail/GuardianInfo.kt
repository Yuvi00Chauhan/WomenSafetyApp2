package com.shubham.womensafety.guardiandetail
import GuardianAdapter

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shubham.womensafety.R
import com.shubham.womensafety.databinding.FragmentGuardianInfoBinding

class GuardianInfo : Fragment() {

    private lateinit var binding: FragmentGuardianInfoBinding
    private lateinit var model: GuardianInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_guardian_info, container, false)

        // Get the view model
        model = ViewModelProvider(this).get(GuardianInfoViewModel::class.java)

        // Specify layout for recycler view
        val linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.guardianList.layoutManager = linearLayoutManager

        // Observe the model
        model.allGuardians.observe(viewLifecycleOwner, Observer { guardians ->
            // Data bind the recycler view
            binding.guardianList.adapter = GuardianAdapter(guardians)
        })

        binding.addGuardian.setOnClickListener { openAddGuardian() }

        model.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_LONG
                ).show()
                model.doneShowingSnackbar()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun openAddGuardian() {
        findNavController().navigate(GuardianInfoDirections.actionGuardianInfoToAddGuardian())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_item -> {
                model.onClear()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}