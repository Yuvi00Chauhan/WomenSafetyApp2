package com.shubham.womensafety

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.arrayListOf
import androidx.navigation.findNavController
import androidx.room.Room
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.shubham.womensafety.FirebaseAuth.LoginViewModel
import com.shubham.womensafety.database.Guardian
import com.shubham.womensafety.database.GuardianDatabase
import com.shubham.womensafety.databinding.FragmentDashBoardBinding
import kotlinx.coroutines.*

class DashBoardFragment : Fragment() {

    private lateinit var binding: FragmentDashBoardBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var Latitude: String = ""
    private var Longitude: String = ""

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    companion object {
        const val TAG = "DashBoardFragment"
        const val SIGN_IN_RESULT_CODE = 1001
        private const val PERMISSION_SEND_SMS = 2
    }

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dash_board, container, false
        )

        getLocation()

        binding.guardianButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(DashBoardFragmentDirections.actionDashBoardFragmentToGuardianInfo())
        }

        binding.locButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_dashBoardFragment_to_mapsActivity)
        }

        binding.emerButton.setOnClickListener {
            getLocation()
            if (Longitude.isBlank()) {
                Toast.makeText(
                    requireActivity(),
                    "Click on Location button and try again",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
                } else {
                    uiScope.launch {
                        withContext(Dispatchers.IO) {
                            emergencyFun()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    binding.textView.text =
                        ("Welcome, " + FirebaseAuth.getInstance().currentUser ?.displayName)
                }
                else -> {
                    launchSignInFlow()
                }
            }
        })
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).setTheme(R.style.LoginTheme_NoActionBar)
                .setLogo(R.drawable.women)
                .build(), SIGN_IN_RESULT_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_SEND_SMS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        emergencyFun()
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                Latitude = location.latitude.toString()
                Longitude = location.longitude.toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.DONUT)
    private fun emergencyFun() {
        val db = Room.databaseBuilder(requireActivity(), GuardianDatabase::class.java, "GuardianDB").build()
        val emailList: List<Guardian> = db.guardianDatabaseDao().getEmail()

        var maillist: String = ""
        val subject: String = "From Women Safety App"
        val text: String = resources.getString(R.string.problem)
        val text1 = "$text https://www.google.com/maps/search/?api=1&query=$Latitude,$Longitude"

        maillist = emailList.joinToString(separator = ",") { it.guardianEmail }

        emailList.forEach {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(it.guardianPhoneNo, null, text1, null, null)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(maillist))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text1)
        }
        startActivity(Intent.createChooser(shareIntent, "Send mail using.."))
    }
}