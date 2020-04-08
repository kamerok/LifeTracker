package lifetracker.common.auth

import android.content.Intent
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.common.UserRecoverableException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException


suspend fun <T> checkForRecover(
    recover: suspend (Intent) -> Unit,
    checked: MutableSet<String> = mutableSetOf(),
    block: suspend () -> T
): T {
    return try {
        block()
    } catch (e: UserRecoverableAuthIOException) {
        val name = e::class.qualifiedName.orEmpty()
        if (checked.contains(name)) throw e else checked.add(name)
        recover(e.intent)
        checkForRecover(recover, checked, block)
    } catch (e: UserRecoverableAuthException) {
        val name = e::class.qualifiedName.orEmpty()
        if (checked.contains(name)) throw e else checked.add(name)
        recover(e.intent)
        checkForRecover(recover, checked, block)
    } catch (e: UserRecoverableException) {
        val name = e::class.qualifiedName.orEmpty()
        if (checked.contains(name)) throw e else checked.add(name)
        recover(e.intent)
        checkForRecover(recover, checked, block)
    }
}

