package com.example.cad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES/GCM/NoPadding";  // Use AES in GCM mode for better security
    private static SecretKey secretKey;
    private static String masterKeyAlias;
    private static final String TAG = "EncryptionUtils";

    // Initialize the encryption key, ensuring it is persistent
    public static void init(Context context) {
        try {
            // Using Android Keystore system to create and store the master key
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            // Retrieve the actual secret key using Keystore
            secretKey = generateSecretKey(masterKeyAlias);
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error initializing encryption key: " + e.getMessage(), e);
        }
    }

    // Generate a SecretKey from the Keystore
    private static SecretKey generateSecretKey(String masterKeyAlias) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);  // Load the keystore

        // Get the key from the Keystore
        return (SecretKey) keyStore.getKey(masterKeyAlias, null);
    }

    // Get Encrypted SharedPreferences (you can still use this for shared preferences)
    public static SharedPreferences getEncryptedSharedPreferences(Context context, String prefName) {
        try {
            return EncryptedSharedPreferences.create(
                    prefName,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error creating encrypted shared preferences: " + e.getMessage(), e);
            return null;
        }
    }

    // Encrypt method with error handling
    public static String encrypt(String plainText) {
        try {
            Log.d(TAG, "Plaintext: " + plainText);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] iv = cipher.getIV(); // Initialization vector for GCM
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted data
            byte[] encryptedDataWithIV = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, encryptedDataWithIV, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, encryptedDataWithIV, iv.length, encryptedBytes.length);

            // Return Base64-encoded encrypted string (IV + encrypted data)
            String encryptedText = Base64.encodeToString(encryptedDataWithIV, Base64.DEFAULT);
            Log.d(TAG, "Encrypted Text: " + encryptedText);
            return encryptedText;
        } catch (Exception e) {
            Log.e(TAG, "Error encrypting text: " + e.getMessage(), e);
            return null;
        }
    }

    // Decrypt method with error handling
    public static String decrypt(String cipherText) {
        try {
            byte[] encryptedDataWithIV = Base64.decode(cipherText, Base64.DEFAULT);

            // Extract the IV and the encrypted data
            byte[] iv = new byte[12]; // GCM uses 12-byte IV
            System.arraycopy(encryptedDataWithIV, 0, iv, 0, iv.length);
            byte[] encryptedBytes = new byte[encryptedDataWithIV.length - iv.length];
            System.arraycopy(encryptedDataWithIV, iv.length, encryptedBytes, 0, encryptedBytes.length);

            // Initialize the cipher with the extracted IV
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);  // 128-bit tag length
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            Log.d(TAG, "Decrypted Text: " + decryptedText);
            return decryptedText;
        } catch (Exception e) {
            Log.e(TAG, "Error decrypting text: " + e.getMessage(), e);
            return null;
        }
    }
}
