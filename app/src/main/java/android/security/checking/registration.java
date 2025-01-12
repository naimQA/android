package android.security.checking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class registration extends AppCompatActivity {
    private static final CryptoUtils cryptoUtils = new CryptoUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EditText userNameField = findViewById(R.id.userName);
        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        EditText rePasswordField = findViewById(R.id.RePassword);
        Button signUpButton = findViewById(R.id.signUpBtn);

        signUpButton.setOnClickListener(v -> {
            String username = userNameField.getText().toString();
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String rePassword = rePasswordField.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(rePassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (UserManager.findUserByUsername(username) != null) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            } else {

                KeyGenerator keyGenerator = null;
                try {
                    keyGenerator = KeyGenerator.getInstance("AES");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                keyGenerator.init(256); // Use 256 bits for the key
                SecretKey secretKey = keyGenerator.generateKey();
                String keyString = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);

                // Get SharedPreferences and save the key
                SharedPreferences sharedPreferences = context.getSharedPreferences("MySecurePrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("secret_key", keyString);
                editor.apply();



                // Generate an initialization vector (IV)
                byte[] ivBytes = new byte[16]; // 16-byte IV for AES
                IvParameterSpec iv = new IvParameterSpec(ivBytes);
                String encryptedPassword = cryptoUtils.encryptData(password,this);
                User user = new User(username, email, encryptedPassword);
                UserManager.addUser(user);
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, login.class);
                startActivity(intent);
            }
        });
    }
}
