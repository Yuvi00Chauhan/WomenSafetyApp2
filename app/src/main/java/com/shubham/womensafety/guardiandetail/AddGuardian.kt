package com.shubham.womensafety.guardiandetail

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shubham.womensafety.R
import com.shubham.womensafety.database.Guardian
import com.shubham.womensafety.databinding.FragmentAddGuardianBinding

class AddGuardian : Fragment() {

    private lateinit var binding: FragmentAddGuardianBinding
    private val model: GuardianInfoViewModel by viewModels() // Use the viewModels delegate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_guardian, container, false
        )

        binding.submitDetail.setOnClickListener {
            addData()
        }

        return binding.root
    }

    private fun addData() {
        if (TextUtils.isEmpty(binding.editName.text.toString())) {
            binding.editName.error = "This field cannot be empty"
            return
        } else if (TextUtils.isEmpty(binding.editRelation.text.toString())) {
            binding.editRelation.error = "This field cannot be empty"
            return
        } else if (TextUtils.isEmpty(binding.editPhone.text.toString())) {
            binding.editPhone.error = "This field cannot be empty"
            return
        } else if (TextUtils.isEmpty(binding.editEmail.text.toString())) {
            binding.editEmail.error = "This field cannot be empty"
            return
        }

        val name = binding.editName.text.toString()
        val relation = binding.editRelation.text.toString()
        val phone = binding.editPhone.text.toString()
        val email = binding.editEmail.text.toString()

        val guardian = Guardian(null, name, relation, phone, email)
        model.insert(guardian)

        Toast.makeText(requireActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show()

        findNavController().navigate(AddGuardianDirections.actionAddGuardianToGuardianInfo())
    }
}