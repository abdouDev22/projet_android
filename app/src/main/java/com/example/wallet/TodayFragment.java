package com.example.wallet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TodayFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<String> productIds = new ArrayList<>();
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productPrices = new ArrayList<>();
    private CustomAdapter customAdapter;
    private MyDatabaseHelper myDB;

    public TodayFragment() {
        // Constructeur public vide requis par Fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!isUserLoggedIn()) {
            // Rediriger vers l'écran de connexion
            Intent intent = new Intent(requireContext(), Login_activity.class);
            startActivity(intent);
            requireActivity().finish(); // Terminer l'activité en cours pour empêcher l'utilisateur de revenir en arrière
            return null; // Retourner null pour indiquer qu'aucune vue n'est affichée
        }

        View view = inflater.inflate(R.layout.fragment_today, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        myDB = new MyDatabaseHelper(requireActivity());
        storeDataInArrays();

        customAdapter = new CustomAdapter(requireActivity(), productIds, productNames, productPrices);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        return view;
    }

    private void storeDataInArrays() {
        long userId = getUserIdFromSession(); // Récupérer l'ID de l'utilisateur depuis la session
        Cursor cursor = myDB.getTransactionsByUser(userId); // Récupérer toutes les transactions de l'utilisateur

        if (cursor.getCount() == 0) {
            // Aucune transaction disponible
        } else {
            while (cursor.moveToNext()) {
                productIds.add(cursor.getString(0)); // ID de la transaction
                String productId = cursor.getString(1); // ID du produit
                // Maintenant, récupérez le nom du produit à partir de son ID
                Cursor productCursor = myDB.getProductById(productId);
                if (productCursor != null && productCursor.moveToFirst()) {
                    @SuppressLint("Range") String productName = productCursor.getString(productCursor.getColumnIndex(MyDatabaseHelper.PRODUCT_NOM));
                    productNames.add(productName);
                    // Vous pouvez également ajouter d'autres détails du produit si nécessaire
                }
                if (productCursor != null) {
                    productCursor.close();
                }
                // productPrices.add(cursor.getString(2)); // Prix du produit (si nécessaire)
            }
        }
    }


    private long getUserIdFromSession() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_session", requireContext().MODE_PRIVATE);
        return preferences.getLong("user_id", -1); // -1 est la valeur par défaut si l'ID n'est pas trouvé
    }

    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_session", requireContext().MODE_PRIVATE);
        long userId = preferences.getLong("user_id", -1); // -1 est la valeur par défaut si l'ID n'est pas trouvé
        return userId != -1; // Si l'ID est différent de -1, l'utilisateur est connecté
    }


}
