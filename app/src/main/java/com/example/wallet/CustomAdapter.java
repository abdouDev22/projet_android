package com.example.wallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> PRODUCT_ID, PRODUCT_NOM, PRODUCT_PRIX;
    private static final String TAG = "CustomAdapter";

    public CustomAdapter(Context context,
                         ArrayList<String> PRODUCT_ID,
                         ArrayList<String> PRODUCT_NOM,
                         ArrayList<String> PRODUCT_PRIX) {
        this.context = context;
        this.PRODUCT_ID = PRODUCT_ID;
        this.PRODUCT_NOM = PRODUCT_NOM;
        this.PRODUCT_PRIX = PRODUCT_PRIX;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.produit_id_txt.setText(PRODUCT_ID.get(position));
        holder.produit_nom_txt.setText(PRODUCT_NOM.get(position));
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.edit) {
                            // Add edit action here
                            String productId = PRODUCT_ID.get(holder.getBindingAdapterPosition());
                            Intent intent = new Intent(context, Edit_produit.class);
                            intent.putExtra("productId", productId);
                            context.startActivity(intent);
                            return true;
                        } else if (id == R.id.delete) {
                            int positionToDelete = holder.getBindingAdapterPosition();
                            deleteItem(positionToDelete);
                            return true;
                        } else if (id == R.id.achat) {
                            String productId = PRODUCT_ID.get(holder.getBindingAdapterPosition());
                            int quantityToBuy = 1;
                            if (isUserLoggedIn()) {
                                long userId = getCurrentUserId();
                                MyDatabaseHelper mydb = new MyDatabaseHelper(context);
                                long transactionId = mydb.insertTransaction(Long.parseLong(productId), userId, quantityToBuy);
                                if (transactionId != -1) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("Transaction réussie !");
                                    builder.setPositiveButton("OK", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("Erreur lors de la transaction. Veuillez réessayer.");
                                    builder.setPositiveButton("OK", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            } else {
                                Intent intent = new Intent(context, Login_activity.class);
                                context.startActivity(intent);
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        String prix = PRODUCT_PRIX.get(position) + ".DJF";
        holder.produit_prix_txt.setText(prix);
    }

    @Override
    public int getItemCount() {
        return PRODUCT_ID.size();
    }

    private void deleteItem(int position) {
        String productIdToDelete = PRODUCT_ID.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Êtes-vous sûr de vouloir supprimer cet élément ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper mydb = new MyDatabaseHelper(context);
                mydb.deleteProduct(Long.parseLong(productIdToDelete));
                PRODUCT_ID.remove(position);
                PRODUCT_NOM.remove(position);
                PRODUCT_PRIX.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, PRODUCT_ID.size());
            }
        });
        builder.setNegativeButton("Non", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView produit_id_txt, produit_nom_txt, produit_prix_txt;
        ImageButton imageButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            produit_id_txt = itemView.findViewById(R.id.produit_id_txt);
            produit_nom_txt = itemView.findViewById(R.id.produit_nom_txt);
            produit_prix_txt = itemView.findViewById(R.id.produit_prix_txt);
            imageButton = itemView.findViewById(R.id.imageButton);

            imageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private long getCurrentUserId() {
        SharedPreferences preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return preferences.getLong("user_id", -1);
    }

    private void clearUserSession() {
        SharedPreferences preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        preferences.edit().remove("user_id").apply();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        long userId = preferences.getLong("user_id", -1);
        return userId != -1;
    }
}
