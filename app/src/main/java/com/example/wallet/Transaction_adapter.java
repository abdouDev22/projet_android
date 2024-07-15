package com.example.wallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class Transaction_adapter extends RecyclerView.Adapter<Transaction_adapter.MyViewHolder> {

    Context context;
    ArrayList<String> TRANSACTION_ID, TRANSACTION_PRODUCT_ID, TRANSACTION_QUANTITY, TRANSACTION_DATE, TRANSACTION_PRIX;
    private static final String TAG = "Transaction_adapter";

    public Transaction_adapter(Context context,
                               ArrayList<String> TRANSACTION_ID,
                               ArrayList<String> TRANSACTION_PRODUCT_ID,
                               ArrayList<String> TRANSACTION_QUANTITY,
                               ArrayList<String> TRANSACTION_DATE,
                               ArrayList<String> TRANSACTION_PRIX) {
        this.context = context;
        this.TRANSACTION_ID = TRANSACTION_ID;
        this.TRANSACTION_PRODUCT_ID = TRANSACTION_PRODUCT_ID;
        this.TRANSACTION_QUANTITY = TRANSACTION_QUANTITY;
        this.TRANSACTION_DATE = TRANSACTION_DATE;
        this.TRANSACTION_PRIX = TRANSACTION_PRIX;
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

        holder.transaction_id_txt.setText(TRANSACTION_ID.get(position));
        holder.transaction_product_id_txt.setText(TRANSACTION_PRODUCT_ID.get(position));
        holder.transaction_quantity_txt.setText(TRANSACTION_QUANTITY.get(position));
        holder.transaction_date_txt.setText(TRANSACTION_DATE.get(position));
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
                        if (id == R.id.delete) {
                            int positionToDelete = holder.getBindingAdapterPosition();
                            deleteTransaction(positionToDelete);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        String prix = TRANSACTION_PRIX.get(position) + ".DJF";
        holder.transaction_prix_txt.setText(prix);
    }

    @Override
    public int getItemCount() {
        return TRANSACTION_ID.size();
    }

    private void deleteTransaction(int position) {
        String transactionIdToDelete = TRANSACTION_ID.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Êtes-vous sûr de vouloir supprimer cette transaction ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper mydb = new MyDatabaseHelper(context);
                mydb.deleteTransaction(Long.parseLong(transactionIdToDelete));
                TRANSACTION_ID.remove(position);
                TRANSACTION_PRODUCT_ID.remove(position);
                TRANSACTION_QUANTITY.remove(position);
                TRANSACTION_DATE.remove(position);
                TRANSACTION_PRIX.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, TRANSACTION_ID.size());
            }
        });
        builder.setNegativeButton("Non", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView transaction_id_txt, transaction_product_id_txt, transaction_quantity_txt, transaction_date_txt, transaction_prix_txt;
        ImageButton imageButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            transaction_id_txt = itemView.findViewById(R.id.transaction_id_txt);
            transaction_product_id_txt = itemView.findViewById(R.id.product_id_txt);
            transaction_quantity_txt = itemView.findViewById(R.id.quantity_txt);
            imageButton = itemView.findViewById(R.id.imageButton);

            imageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
