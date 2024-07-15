package com.example.wallet;
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

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<String> productIds = new ArrayList<>();
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productPrices = new ArrayList<>();
    private CustomAdapter customAdapter;
    private MyDatabaseHelper myDB;

    public HomeFragment() {
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        myDB = new MyDatabaseHelper(requireActivity());
        storeDataInArrays();

        customAdapter = new CustomAdapter(requireActivity(), productIds, productNames, productPrices);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        return view;
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_session", requireContext().MODE_PRIVATE);
        long userId = preferences.getLong("user_id", -1); // -1 est la valeur par défaut si l'ID n'est pas trouvé
        return userId != -1; // Si l'ID est différent de -1, l'utilisateur est connecté
    }

    private void storeDataInArrays() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            // Aucune donnée disponible
        } else {
            while (cursor.moveToNext()) {
                productIds.add(cursor.getString(0));
                productNames.add(cursor.getString(1));
                productPrices.add(cursor.getString(2));
            }
        }
    }
}
