package com.example.wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProduitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProduitFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProduitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProduitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public ProduitFragment newInstance(String param1, String param2) {

        if (!isUserLoggedIn()) {
            // Rediriger vers l'écran de connexion
            Intent intent = new Intent(requireContext(), Login_activity.class);
            startActivity(intent);
            requireActivity().finish(); // Terminer l'activité en cours pour empêcher l'utilisateur de revenir en arrière
            return null; // Retourner null pour indiquer qu'aucune vue n'est affichée
        }

        ProduitFragment fragment = new ProduitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_produit, container, false);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_session", requireContext().MODE_PRIVATE);
        long userId = preferences.getLong("user_id", -1); // -1 est la valeur par défaut si l'ID n'est pas trouvé
        return userId != -1; // Si l'ID est différent de -1, l'utilisateur est connecté
    }
}