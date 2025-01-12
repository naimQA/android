package android.security.checking;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Récupérer le bouton Log Out
        Button logoutButton = findViewById(R.id.logoutButton);
        Button viewUsersButton = findViewById(R.id.viewUsersBtn);

       // Vérifier si l'utilisateur connecté est admin
        if (UserManager.isUserAdmin()) {
            // Si l'utilisateur est admin, rendre le bouton visible
            viewUsersButton.setVisibility(View.VISIBLE);

            // Ajouter un gestionnaire d'événements au bouton "View Users"
            viewUsersButton.setOnClickListener(v -> {
                Intent intent = new Intent(LoginPage.this, UserListActivity.class);
                startActivity(intent);
            });
        } else {
            // Sinon, masquer le bouton
            viewUsersButton.setVisibility(View.GONE);
        }

        // Ajouter un gestionnaire d'événements au bouton Log Out
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(LoginPage.this, "You have logged out!", Toast.LENGTH_SHORT).show();
            // Retourner à l'écran précédent ou effectuer une autre action
            finish();
        });
    }
}

