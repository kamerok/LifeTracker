package lifetracker.common.auth

import android.accounts.Account
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.android.gms.auth.api.signin.GoogleSignIn


class AuthData(
    private val context: Context,
    private val sharedPrefs: SharedPreferences
) {

    var sheetId: String?
        get() = sharedPrefs.getString("sheet.id", null)
        set(value) = sharedPrefs.edit { putString("sheet.id", value) }

    val account: Account?
        get() = GoogleSignIn.getLastSignedInAccount(context)?.account

}
