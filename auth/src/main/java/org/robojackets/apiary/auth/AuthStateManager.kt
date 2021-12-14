package org.robojackets.apiary.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.AnyThread
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import net.openid.appauth.*
import org.json.JSONException
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

// Based on AuthStateManager.java from the AppAuth-Android example app
// https://github.com/openid/AppAuth-Android/blob/master/app/java/net/openid/appauthdemo/AuthStateManager.java
@ActivityRetainedScoped
class AuthStateManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val mPrefs: SharedPreferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)
    private val mPrefsLock: ReentrantLock = ReentrantLock()
    private val mCurrentAuthState: AtomicReference<AuthState> = AtomicReference()

    @get:NonNull
    @get:AnyThread
    val current: AuthState
        get() {
            if (mCurrentAuthState.get() != null) {
                return mCurrentAuthState.get()
            }
            val state: AuthState = readState()
            return if (mCurrentAuthState.compareAndSet(null, state)) {
                state
            } else {
                mCurrentAuthState.get()
            }
        }

    @AnyThread
    @NonNull
    fun replace(@NonNull state: AuthState?): AuthState? {
        writeState(state)
        mCurrentAuthState.set(state)
        return state
    }

    @AnyThread
    @NonNull
    fun updateAfterAuthorization(
        @Nullable response: AuthorizationResponse?,
        @Nullable ex: AuthorizationException?
    ): AuthState? {
        val current: AuthState = current
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    @NonNull
    fun updateAfterTokenResponse(
        @Nullable response: TokenResponse?,
        @Nullable ex: AuthorizationException?
    ): AuthState? {
        val current: AuthState = current
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    @NonNull
    fun updateAfterRegistration(
        response: RegistrationResponse?,
        ex: AuthorizationException?
    ): AuthState? {
        val current: AuthState = current
        if (ex != null) {
            return current
        }
        current.update(response)
        return replace(current)
    }

    @AnyThread
    @NonNull
    private fun readState(): AuthState {
        mPrefsLock.lock()
        return try {
            val currentState = mPrefs.getString(KEY_STATE, null)
                ?: return AuthState()
            try {
                AuthState.jsonDeserialize(currentState)
            } catch (ex: JSONException) {
                Timber.w(ex, "Failed to deserialize stored auth state - discarding")
                AuthState()
            }
        } finally {
            mPrefsLock.unlock()
        }
    }

    @AnyThread
    private fun writeState(@Nullable state: AuthState?) {
        mPrefsLock.lock()
        try {
            val editor = mPrefs.edit()
            if (state == null) {
                editor.remove(KEY_STATE)
            } else {
                editor.putString(KEY_STATE, state.jsonSerializeString())
            }
            check(editor.commit()) { "Failed to write state to shared prefs" }
        } finally {
            mPrefsLock.unlock()
        }
    }

    companion object {
        private val INSTANCE_REF: AtomicReference<WeakReference<AuthStateManager>> =
            AtomicReference(WeakReference(null))
        private const val STORE_NAME = "AuthState"
        private const val KEY_STATE = "state"
        @AnyThread
        fun getInstance(@NonNull context: Context): AuthStateManager {
            var manager: AuthStateManager? = INSTANCE_REF.get().get()
            if (manager == null) {
                manager = AuthStateManager(context.getApplicationContext())
                INSTANCE_REF.set(WeakReference(manager))
            }
            return manager
        }
    }
}
