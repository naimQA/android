package android.security.checking;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;

public class CryptoUtils {
    private static final String KEY_ALIAS = "MyKeyAlias";
    private static final String TRANSFORMATION = "AES/CBC/PKCS7Padding";
    private Cipher cipher;

    public CryptoUtils() {
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SecretKey getSecretKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
                keyGenerator.generateKey();
            }

            return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] encrypt(String plainText, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }

    // Decryption function
    public static String decrypt(byte[] cipherText, SecretKey secretKey, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decryptedData = cipher.doFinal(cipherText);
        return new String(decryptedData, "UTF-8");
    }
    public String encryptData(String data , Context context) {
        try {
            SecretKey secretKey = getSecretKey();
            String keyString = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);

            // Get SharedPreferences and save the key
            SharedPreferences sharedPreferences = context.getSharedPreferences("MySecurePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("secret_key", keyString);
            editor.apply();

            if (secretKey != null) {
                byte[] iv = new byte[cipher.getBlockSize()];
                new SecureRandom().nextBytes(iv);

                cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
                byte[] encryptedBytes = cipher.doFinal(data.getBytes());

                byte[] combined = new byte[iv.length + encryptedBytes.length];
                System.arraycopy(iv, 0, combined, 0, iv.length);
                System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

                return Base64.encodeToString(combined, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decryptData(String encryptedData,Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MySecurePrefs", Context.MODE_PRIVATE);
        String keyString = sharedPreferences.getString("secret_key", null);


            // Decode the Base64 string back into a byte array
            byte[] decodedKey = Base64.decode(keyString, Base64.DEFAULT);

            // Reconstruct the SecretKey
            SecretKey secretKey= new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            try {



            if (secretKey != null) {
                byte[] combined = Base64.decode(encryptedData, Base64.DEFAULT);
                byte[] iv = Arrays.copyOfRange(combined, 0, cipher.getBlockSize());
                byte[] encryptedBytes = Arrays.copyOfRange(combined, cipher.getBlockSize(), combined.length);

                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

                return new String(decryptedBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
