package android.security.checking;

import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class login extends AppCompatActivity {
    private static final CryptoUtils cryptoUtils = new CryptoUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button viewUsersButton = findViewById(R.id.viewUsersBtn);
        viewUsersButton.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, UserListActivity.class);
            startActivity(intent);
        });
        EditText userNameField = findViewById(R.id.loginUserName);
        EditText passwordField = findViewById(R.id.loginPassword);
        Button loginButton = findViewById(R.id.loginBtn);
        Button registerButton = findViewById(R.id.regBtn);

        loginButton.setOnClickListener(v -> {
            String username = userNameField.getText().toString();
            String password = passwordField.getText().toString();

            User user = UserManager.findUserByUsername(username);
            if (user != null) {
                String decryptedPassword = cryptoUtils.decryptData(user.getPassword(),this);
                if (password.equals(decryptedPassword)) {
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    UserManager.setCurrentUser(user);
                    Intent intent = new Intent(login.this, UserListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invalid Username!", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, registration.class);
            startActivity(intent);
        });

        // Biometric Authentication (facultatif)
        Button biometricButton = findViewById(R.id.biometricButton);
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(login.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                Toast.makeText(login.this, "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(login.this, LoginPage.class);
                startActivity(intent);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Cancel")
                .build();

        biometricButton.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));
    }

}
