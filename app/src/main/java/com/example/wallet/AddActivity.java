package com.example.wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    EditText nom_produit, prix_produit;
    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérifier si l'utilisateur est connecté
        if (!isUserLoggedIn()) {
            // Rediriger vers l'écran de connexion
            Intent intent = new Intent(this, Login_activity.class);
            startActivity(intent);
            finish(); // Terminer l'activité en cours pour empêcher l'utilisateur de revenir en arrière
            return; // Sortir de la méthode onCreate pour éviter l'exécution du reste du code
        }

        setContentView(R.layout.activity_add);

        nom_produit = findViewById(R.id.nom_produit);
        prix_produit = findViewById(R.id.prix_produit);
        add_button = findViewById(R.id.add_button);

        add_button.setOnClickListener(view -> {
            String productName = nom_produit.getText().toString().trim();
            String productPriceString = prix_produit.getText().toString().trim();
            if (!productName.isEmpty() && !productPriceString.isEmpty()) {
                try {
                    double productPrice = Double.parseDouble(productPriceString);
                    MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
                    long id = myDB.insertProduct(productName, productPrice);
                    if (id != -1) {
                        Toast.makeText(AddActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();

                        // Rediriger vers le HomeFragment après avoir ajouté le produit
                        Intent intent = new Intent(AddActivity.this, MainActivity.class);
                        intent.putExtra("showHomeFragment", true);
                        startActivity(intent);

                        finish(); // Close the activity after adding the product
                    } else {
                        Toast.makeText(AddActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(AddActivity.this, "Invalid number format for price", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Méthode pour vérifier si l'utilisateur est connecté
    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        long userId = preferences.getLong("user_id", -1); // -1 est la valeur par défaut si l'ID n'est pas trouvé
        return userId != -1; // Si l'ID est différent de -1, l'utilisateur est connecté
    }
}
