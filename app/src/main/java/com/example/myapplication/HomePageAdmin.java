package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class HomePageAdmin extends AppCompatActivity {

    private FrameLayout HomeAdminPatientsButton, HomeAdminAppointmentsButton, HomeAdminMedicationButton, HomeAdminStaffButton, HomeAdminReportsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_admin);

        HomeAdminPatientsButton = findViewById(R.id.HomeAdminPatientsButton);
        HomeAdminAppointmentsButton = findViewById(R.id.HomeAdminAppointmentsButton);
        HomeAdminMedicationButton = findViewById(R.id.HomeAdminMedicationButton);
        HomeAdminStaffButton = findViewById(R.id.HomeAdminStaffButton);
        HomeAdminReportsButton = findViewById(R.id.HomeAdminReportsButton);

        HomeAdminPatientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page activity = new Page(HomePageAdmin.this, "Patients");
                Intent intent = activity.changePage();
                startActivity(intent);
            }
        });

        HomeAdminAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page activity = new Page(HomePageAdmin.this, "Appointments");
                Intent intent = activity.changePage();
                startActivity(intent);
            }
        });

        HomeAdminMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page activity = new Page(HomePageAdmin.this, "Medication");
                Intent intent = activity.changePage();
                startActivity(intent);
            }
        });

        HomeAdminStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page activity = new Page(HomePageAdmin.this, "Staff");
                Intent intent = activity.changePage();
                startActivity(intent);
            }
        });

        HomeAdminReportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page activity = new Page(HomePageAdmin.this, "Reports");
                Intent intent = activity.changePage();
                startActivity(intent);
            }
        });




    }
}