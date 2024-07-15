package com.example.wallet;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Edit_produit extends AppCompatActivity {

    EditText nom_produit, prix_produit;
    Button edit_button;
    MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_produit);

        // Initialize les vues
        nom_produit = findViewById(R.id.nom_produit);
        prix_produit = findViewById(R.id.prix_produit);
        edit_button = findViewById(R.id.edit_button);

        // Initialize le helper de la base de données
        myDatabaseHelper = new MyDatabaseHelper(this);

        // Récupère l'ID du produit de l'Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String productId = extras.getString("productId");
            // Utilise l'ID du produit pour extraire les informations du produit depuis la base de données
            displayProductDetails(productId);

            // Ajoute un écouteur d'événements au bouton "Modifier"
            edit_button.setOnClickListener(view -> {
                // Récupère les nouvelles valeurs du nom et du prix du produit depuis les champs de texte
                String newNom = nom_produit.getText().toString();
                double newPrix = Double.parseDouble(prix_produit.getText().toString());

                // Met à jour les informations du produit dans la base de données
                boolean updateSuccessful = myDatabaseHelper.updateProduct(productId, newNom, newPrix);
                if (updateSuccessful) {
                    Toast.makeText(Edit_produit.this, "Produit mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Edit_produit.this, MainActivity.class));
                    finish();
                    // Vous pouvez également rediriger l'utilisateur vers une autre activité ou fermer cette activité après la mise à jour réussie
                } else {
                    Toast.makeText(Edit_produit.this, "Échec de la mise à jour du produit", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void displayProductDetails(String productId) {
        // Utilise l'ID du produit pour récupérer les informations du produit depuis la base de données
        Cursor cursor = myDatabaseHelper.getProductById(productId);

        if (cursor != null && cursor.moveToFirst()) {
            // Extrait les informations du produit depuis le curseur
            @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.PRODUCT_NOM));
            @SuppressLint("Range") double prix = cursor.getDouble(cursor.getColumnIndex(MyDatabaseHelper.PRODUCT_PRIX));

            // Affiche les détails du produit dans les vues correspondantes
            nom_produit.setText(nom);
            prix_produit.setText(String.valueOf(prix));
        } else {
            // Gère le cas où le produit n'est pas trouvé dans la base de données
            Toast.makeText(this, "Produit non trouvé", Toast.LENGTH_SHORT).show();
        }
    }
}
