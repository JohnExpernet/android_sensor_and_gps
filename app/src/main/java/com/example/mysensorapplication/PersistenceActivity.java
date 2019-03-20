package com.example.mysensorapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mysensorapplication.models.MySQLiteOpenHelper;

import java.util.ArrayList;

public class PersistenceActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persistence);

        // this.initSharedPreferences();
        this.initSQLite();
    }

    @Override
    protected void onDestroy() {
        this.mySQLiteOpenHelper.close();
        super.onDestroy();
    }

    private void initSharedPreferences() {
        this.preferences = getPreferences(Context.MODE_PRIVATE);
        this.writeStringInSharedPreferences("username", "John");
        this.writeStringInSharedPreferences("location", "Le Port");
        String username = this.readStringInSharedPreferences("username");
        String location = this.readStringInSharedPreferences("dada");
        Log.d("PERSISTENCE_ACTIVITY", username + ":" + location);
    }

    private void writeStringInSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String readStringInSharedPreferences(String key) {
        return this.preferences.getString(key, null);
    }

    private void initSQLite() {
        this.mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        /*this.mySQLiteOpenHelper.write("John");
        this.mySQLiteOpenHelper.write("Matthieu");
        this.mySQLiteOpenHelper.write("Bob");*/
        ArrayList<String> list = this.mySQLiteOpenHelper.read("John");
        Log.d("PERSISTENCE_ACTIVITY", list.toString());
    }
}
