package com.example.wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AddFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Vérifier si l'utilisateur est connecté
        if (!isUserLoggedIn()) {
            // Rediriger vers l'écran de connexion
            Intent intent = new Intent(requireContext(), Login_activity.class);
            startActivity(intent);
            requireActivity().finish(); // Terminer l'activité en cours pour empêcher l'utilisateur de revenir en arrière
            return null; // Retourner null pour indiquer qu'aucune vue n'est affichée
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialisation des vues
        EditText nom_produit = rootView.findViewById(R.id.nom_produit);
        EditText prix_produit = rootView.findViewById(R.id.prix_produit);
        Button add_button = rootView.findViewById(R.id.add_button);

        // Gestionnaire de clic pour le bouton Ajouter
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = nom_produit.getText().toString().trim();
                String productPriceString = prix_produit.getText().toString().trim();

                if (!productName.isEmpty() && !productPriceString.isEmpty()) {
                    try {
                        double productPrice = Double.parseDouble(productPriceString);
                        MyDatabaseHelper myDB = new MyDatabaseHelper(requireContext());
                        long id = myDB.insertProduct(productName, productPrice);
                        if (id != -1) {
                            Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                            // Rediriger vers le HomeFragment après avoir ajouté le produit
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new HomeFragment())
                                    .commit();
                        } else {
                            Toast.makeText(requireContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Invalid number format for price", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    // Méthode pour vérifier si l'utilisateur est connecté
    private boolean isUserLoggedIn() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_session", requireContext().MODE_PRIVATE);
        long userId = preferences.getLong("user_id", -1); // -1 est la valeur par défaut si l'ID n'est pas trouvé
        return userId != -1; // Si l'ID est différent de -1, l'utilisateur est connecté
    }
}
