package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class LoginPage extends AppCompatActivity {

    private Button loginButton;
    private Cursor loginCursor;
    public static EditText usernameEditText, passwordEditText;
    private TextView loginPrompt;
    private String usernameEntry, passwordEntry;
    public static String accountType;
    private static SharedPreferences appDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginPrompt = findViewById(R.id.loginPrompt);

        //try {
        //    SQLQueries.testDatabase();
        //} catch (SQLException throwables) {
        //    throwables.printStackTrace();
        //}

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                usernameEntry = usernameEditText.getText().toString().trim();
                passwordEntry = passwordEditText.getText().toString().trim();

                if(checkEntries()) {
                    if(validateCredentials()) {
                        String nextActivity = retrieveNextActivity();

                        // Moves to the appropriate page
                        Page page = new Page(LoginPage.this, nextActivity);
                        Intent intent = page.changePage();
                        startActivity(intent);
                    }
                }
            }
        });

    }

    private boolean checkEntries() {
        if (usernameEntry.isEmpty() && passwordEntry.isEmpty()) {
            loginPrompt.setText("Please enter your credentials");
            return false;
        }

        else if (usernameEntry.isEmpty()) {
            loginPrompt.setText("Please enter a username");
            return false;
        }

        else if (passwordEntry.isEmpty()) {
            loginPrompt.setText("Please enter a password");
            return false;
        }
        return true;
    }


    private boolean validateCredentials() {
        loginCursor = SQLQueries.retrieveUserAccounts(usernameEntry);
        if (loginCursor.getCount() == 0) {
            loginPrompt.setText("No entry was found for the given username");
            return false;
        }

        loginCursor.moveToFirst();
        String password = loginCursor.getString(2);
        if (passwordEntry.equals(password)) {
            accountType = loginCursor.getString(3);
            return true;
        }
        loginPrompt.setText("Password is incorrect");
        return false;
    }


    private String retrieveNextActivity() {
        String nextActivity = "";

        if (accountType.equals("Administrator") || accountType.equals("Receptionist") || accountType.equals("Doctor")) {
            nextActivity = "HomeAdmin";
            appDetails.edit().remove("adminRights").putBoolean("adminRights", true).apply();
        }
        else {
            nextActivity = "HomeStaff";
            appDetails.edit().remove("adminRights").putBoolean("adminRights", false).apply();
        }
        return nextActivity;
    }


    @Override
    protected void onResume() {
        super.onResume();
        passwordEditText.setText("");

        usernameEditText.setText("jdavis");
        passwordEditText.setText("testpassword1");

        appDetails = getSharedPreferences("appDetails", Context.MODE_PRIVATE);
        Database.appDatabase = openOrCreateDatabase("app database", Context.MODE_PRIVATE, null);
        Database.createEntities();
        if (appDetails.getBoolean("dataInserted", false) == false) {
            Database.insertInitialData();
            appDetails.edit().remove("dataInserted").putBoolean("dataInserted", true).apply();
        }
    }
}