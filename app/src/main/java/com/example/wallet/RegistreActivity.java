package com.example.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistreActivity extends AppCompatActivity {

    TextInputEditText nom, email, password;
    Button sInscrire;
    MyDatabaseHelper dbHelper; // Create an instance of MyDatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registre);

        TextInputLayout nomLayout = findViewById(R.id.name);
        TextInputLayout emailLayout = findViewById(R.id.email);
        TextInputLayout passwordLayout = findViewById(R.id.password);
        nom = (TextInputEditText) nomLayout.getEditText();
        email = (TextInputEditText) emailLayout.getEditText();
        password = (TextInputEditText) passwordLayout.getEditText();
        sInscrire = findViewById(R.id.sincrire);

        // Initialize dbHelper
        dbHelper = new MyDatabaseHelper(this);

        sInscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les valeurs saisies
                String nomValue = nom.getText().toString().trim();
                String emailValue = email.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();

                // Vérifier si les champs ne sont pas vides
                if (!nomValue.isEmpty() && !emailValue.isEmpty() && !passwordValue.isEmpty()) {
                    // Insérer les données dans la base de données
                    long id = dbHelper.insertUser(nomValue, emailValue, passwordValue); // Utilisez dbHelper pour insérer l'utilisateur
                    if (id != -1) {
                        // Inscription réussie
                        Toast.makeText(RegistreActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                        // Rediriger vers l'écran de connexion
                        startActivity(new Intent(RegistreActivity.this, Login_activity.class));
                    } else {
                        // Erreur lors de l'inscription
                        Toast.makeText(RegistreActivity.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Afficher un message si des champs sont vides
                    Toast.makeText(RegistreActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
