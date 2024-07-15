package com.example.wallet;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Nom de la base de données
    private static final String DATABASE_NAME = "wallet.db";
    private Context context;
    // Version de la base de données
    private static final int DATABASE_VERSION = 1;

    // Noms des tables
    private static final String TABLE_USER = "utilisateur";
    private static final String TABLE_PRODUCT = "produit";
    private static final String TABLE_TRANSACTION = "transaction_data";

    // Colonnes de la table utilisateur
    private static final String USER_ID = "id_utilisateur";
    private static final String USER_NOM = "nom";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";

    // Colonnes de la table produit
    private static final String PRODUCT_ID = "id_produit";
    public static final String PRODUCT_NOM = "nom";
    public static final String PRODUCT_PRIX = "prix";


    // Colonnes de la table transaction
    static final String TRANSACTION_PRODUCT_ID = "id_produit";
    private static final String TRANSACTION_USER_ID = "id_utilisateur";
    private static final String TRANSACTION_DATE = "date";
    public static final String TRANSACTION_QUANTITY = " quantite ";
    public static final String TRANSACTION_ID = "id_transaction";


    // Requêtes SQL pour créer les tables
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USER_NOM + " TEXT,"
            + USER_EMAIL + " TEXT,"
            + USER_PASSWORD + " TEXT" + ")";

    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + "("
            + PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PRODUCT_NOM + " TEXT,"
            + PRODUCT_PRIX + " REAL" + ")";


    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTION + "("
            + TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TRANSACTION_PRODUCT_ID + " INTEGER,"
            + TRANSACTION_USER_ID + " INTEGER,"
            + TRANSACTION_DATE + " TEXT,"
            + TRANSACTION_QUANTITY + " INTEGER,"
            + "FOREIGN KEY(" + TRANSACTION_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCT + "(" + PRODUCT_ID + "),"
            + "FOREIGN KEY(" + TRANSACTION_USER_ID + ") REFERENCES " + TABLE_USER + "(" + USER_ID + ")"
            + ")";


    MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création des tables
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Suppression des anciennes tables si elles existent
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

        // Recréation des tables
        onCreate(db);
    }

    public long insertProduct(String nom, double prix) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NOM, nom);
        values.put(PRODUCT_PRIX, prix);
        long id = db.insert(TABLE_PRODUCT, null, values);
        db.close();
        return id;
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public long insertUser(String nom, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NOM, nom);
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, password);
        long id = db.insert(TABLE_USER, null, values);
        db.close();
        return id;
    }

    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isAuthenticated = false;

        try {
            String selection = USER_EMAIL + " = ? AND " + USER_PASSWORD + " = ?";
            String[] selectionArgs = {email, password};

            cursor = db.query(TABLE_USER, null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                isAuthenticated = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return isAuthenticated;
    }

    @SuppressLint("Range")
    public long checkCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        long userId = -1;

        Cursor cursor = db.rawQuery("SELECT " + USER_ID + " FROM " + TABLE_USER +
                        " WHERE " + USER_EMAIL + " = ? AND " + USER_PASSWORD + " = ?",
                new String[]{email, password});

        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndex(USER_ID));
        }

        cursor.close();
        db.close();

        return userId;
    }

    public void manageSession(long userId) {
        // Utiliser SharedPreferences pour stocker l'ID de l'utilisateur dans la session
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("user_id", userId);
        editor.apply();
    }

    public long getUserIdFromSession() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong("user_id", -1); // -1 est une valeur par défaut si l'ID n'est pas trouvé
    }

    public boolean deleteProduct(long productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PRODUCT, PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
        return rowsDeleted > 0;
    }


        // Méthode pour mettre à jour un produit en fonction de son ID
        public void updateProductById(int id, String newNom, double newPrix) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PRODUCT_NOM, newNom);
            values.put(PRODUCT_PRIX, newPrix);
            db.update(TABLE_PRODUCT, values, PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
            db.close();
        }


    public Cursor getProductById(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Effectuer une requête pour récupérer les informations du produit en fonction de l'ID
            String[] columns = {PRODUCT_ID, PRODUCT_NOM, PRODUCT_PRIX};
            String selection = PRODUCT_ID + "=?";
            String[] selectionArgs = {productId};
            cursor = db.query(TABLE_PRODUCT, columns, selection, selectionArgs, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching product details: " + e.getMessage());
        }

        return cursor;
    }


    public boolean updateProduct(String productId, String newNom, double newPrix) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NOM, newNom);
        values.put(PRODUCT_PRIX, newPrix);

        int rowsAffected = db.update(TABLE_PRODUCT, values, PRODUCT_ID + " = ?", new String[]{productId});
        return rowsAffected > 0;
    }

    public long insertTransaction(long productId, long userId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRANSACTION_PRODUCT_ID, productId);
        values.put(TRANSACTION_USER_ID, userId);
        values.put(TRANSACTION_DATE, getDateTime()); // Utilisation de la méthode getDateTime() pour obtenir la date actuelle
        values.put(TRANSACTION_QUANTITY, quantity);
        long id = db.insert(TABLE_TRANSACTION, null, values);
        db.close();
        return id;
    }

    // Méthode pour obtenir la date et l'heure actuelles au format texte
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }




    public Cursor getTransactionsByUserAndDate(long userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Effectuer une requête pour récupérer les transactions de l'utilisateur pour une date donnée
            String query = "SELECT " + TABLE_TRANSACTION + "." + TRANSACTION_ID + ", " +
                    TABLE_TRANSACTION + "." + TRANSACTION_PRODUCT_ID + ", " +
                    TABLE_TRANSACTION + "." + TRANSACTION_QUANTITY + ", " +
                    TABLE_PRODUCT + "." + PRODUCT_NOM + " AS product_name " +
                    " FROM " + TABLE_TRANSACTION +
                    " INNER JOIN " + TABLE_PRODUCT +
                    " ON " + TABLE_TRANSACTION + "." + TRANSACTION_PRODUCT_ID +
                    " = " + TABLE_PRODUCT + "." + PRODUCT_ID +
                    " WHERE " + TRANSACTION_USER_ID + "=? AND " + TRANSACTION_DATE + "=?";
            String[] selectionArgs = {String.valueOf(userId), date};
            cursor = db.rawQuery(query, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user transactions for date: " + e.getMessage());
        }

        return cursor;
    }

    public boolean deleteTransaction(long transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_TRANSACTION, TRANSACTION_ID + " = ?", new String[]{String.valueOf(transactionId)});
        db.close();
        return rowsDeleted > 0;
    }

    public Cursor getTransactionsByUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Effectuer une requête pour récupérer toutes les transactions de l'utilisateur
            String[] columns = {TRANSACTION_ID, TRANSACTION_PRODUCT_ID, TRANSACTION_QUANTITY};
            String selection = TRANSACTION_USER_ID + "=?";
            String[] selectionArgs = {String.valueOf(userId)};
            cursor = db.query(TABLE_TRANSACTION, columns, selection, selectionArgs, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching user transactions: " + e.getMessage());
        }

        return cursor;
    }






}




