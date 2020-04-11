package lifetracker.feature.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import lifetracker.feature.login.databinding.FragmentLoginBinding


class LoginFragment(
    private val onLoginSuccess: () -> Unit
) : Fragment(R.layout.fragment_login) {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(
                Scope("https://www.googleapis.com/auth/spreadsheets"),
                Scope("https://www.googleapis.com/auth/drive")
            )
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        FragmentLoginBinding.bind(view).apply {
            signInView.setOnClickListener {
                startActivityForResult(
                    googleSignInClient.signInIntent,
                    SIGN_IN
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SIGN_IN -> {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                if (task.isSuccessful) {
                    onLoginSuccess()
                } else {
                    Log.d(this::class.qualifiedName, "Login Error", task.exception)
                }
            }
        }
    }

    companion object {
        private const val SIGN_IN = 10
    }

}
