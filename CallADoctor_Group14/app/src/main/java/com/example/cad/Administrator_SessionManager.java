
package com.example.cad;

import static com.example.cad.utils.EncryptionUtils.getEncryptedSharedPreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.cad.utils.EncryptionUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class Administrator_SessionManager {
    SharedPreferences sharedPreferences;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "ALOGIN";
    private static final String LOGIN = "AIS_LOGIN";
    public static final String ID = "AID";

    public Administrator_SessionManager(Context context) {
        this._context = context;
        try {
            sharedPreferences = getEncryptedSharedPreferences(context, PREF_NAME);
        } catch (Exception e) {  // Catching all exceptions as a fallback
            e.printStackTrace();
        }


        editor = sharedPreferences.edit();
    }

    public void createSession(String id) {
        editor.putBoolean(LOGIN, true);
        editor.putString(ID, EncryptionUtils.encrypt(id)); // Encrypt ID before storing
        editor.apply();
    }

    public boolean isloggin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin() {
        if (!this.isloggin()) {
            // User is not logged in, redirect to Login Activity
            Intent i = new Intent(_context, AdministratorLogin.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetail() {
        HashMap<String, String> user = new HashMap<>();
        String encryptedId = sharedPreferences.getString(ID, null);
        if (encryptedId != null) {
            user.put(ID, EncryptionUtils.decrypt(encryptedId)); // Decrypt ID when retrieving
        }
        return user;
    }

    public void logout() {
        editor.clear();
        editor.commit();

        // After logout, redirect user to Login Activity
        Intent i = new Intent(_context, AdministratorLogin.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }
}
