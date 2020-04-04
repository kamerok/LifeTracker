package com.kamer.lifetracker.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.sheets.v4.SheetsScopes
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.FragmentLoginBinding
import com.kamer.lifetracker.viewBinding


class LoginFragment(
    private val onLoginSuccess: () -> Unit
) : Fragment(R.layout.fragment_login) {

    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.signInView.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SIGN_IN -> {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                if (task.isSuccessful) onLoginSuccess()
            }
        }
    }

    companion object {
        private const val SIGN_IN = 10
    }

}
