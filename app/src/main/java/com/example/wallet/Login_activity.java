package com.example.wallet;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wallet.MainActivity;
import com.example.wallet.RegistreActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Login_activity extends AppCompatActivity {

    TextInputEditText email, password;
    Button login_button;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        TextInputLayout emailLayout = findViewById(R.id.email);
        TextInputLayout passwordLayout = findViewById(R.id.password);
        email = (TextInputEditText) emailLayout.getEditText();
        password = (TextInputEditText) passwordLayout.getEditText();
        login_button = findViewById(R.id.login_button);

        dbHelper = new MyDatabaseHelper(this); // Initialize dbHelper

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les informations saisies
                String emailValue = email.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();

                // Vérifier si les champs ne sont pas vides
                if (!emailValue.isEmpty() && !passwordValue.isEmpty()) {
                    // Vérifier les informations d'identification dans la base de données
                    long userId = dbHelper.checkCredentials(emailValue, passwordValue);
                    if (userId != -1) {
                        // Connexion réussie
                        Toast.makeText(Login_activity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                        // Mettre l'utilisateur dans une session
                        manageSession(userId);
                        // Rediriger vers l'activité principale ou une autre activité
                        startActivity(new Intent(Login_activity.this, MainActivity.class));
                        finish(); // Terminer l'activité de connexion pour empêcher l'utilisateur de revenir en arrière
                    } else {
                        // Informations d'identification incorrectes
                        Toast.makeText(Login_activity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Afficher un message si des champs sont vides
                    Toast.makeText(Login_activity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Méthode pour gérer la session de l'utilisateur (ex: en utilisant SharedPreferences)
    private void manageSession(long userId) {
        // Utiliser SharedPreferences pour stocker l'ID de l'utilisateur dans la session
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("user_id", userId);
        editor.apply();
    }

    public void registre(View view) {
        startActivity(new Intent(Login_activity.this, RegistreActivity.class));
    }
}
