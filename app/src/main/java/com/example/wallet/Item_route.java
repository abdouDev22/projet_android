package com.example.wallet;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wallet.MyDatabaseHelper;

public class Item_route extends AppCompatActivity {

    private MenuItem action_popup_edit;
    private MenuItem action_popup_delete;
    private MenuItem action_popup_achat;
    private long productIdToDelete; // ID du produit à supprimer

    // Constructeur
    public Item_route(MenuItem action_popup_edit, MenuItem action_popup_delete, MenuItem action_popup_achat) {
        this.action_popup_edit = action_popup_edit;
        this.action_popup_delete = action_popup_delete;
        this.action_popup_achat = action_popup_achat;
    }

    // Getters et Setters
    public MenuItem getAction_popup_edit() {
        return action_popup_edit;
    }

    public void setAction_popup_edit(MenuItem action_popup_edit) {
        this.action_popup_edit = action_popup_edit;
    }

    public MenuItem getAction_popup_delete() {
        return action_popup_delete;
    }

    public void setAction_popup_delete(MenuItem action_popup_delete) {
        this.action_popup_delete = action_popup_delete;
    }

    public MenuItem getAction_popup_achat() {
        return action_popup_achat;
    }

    public void setAction_popup_achat(MenuItem action_popup_achat) {
        this.action_popup_achat = action_popup_achat;
    }

    // Méthode pour définir l'ID du produit à supprimer
    public void setProductIdToDelete(long productIdToDelete) {
        this.productIdToDelete = productIdToDelete;
    }

    // Méthode pour gérer le clic sur l'élément de menu "Delete"
    public void onDeleteMenuItemClicked() {
        // Supprimer le produit associé à l'ID
        MyDatabaseHelper myDB = new MyDatabaseHelper(this);
        boolean deleted = myDB.deleteProduct(productIdToDelete);
        if (deleted) {
            Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show();
        }
    }
}
