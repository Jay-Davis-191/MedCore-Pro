package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageStaff extends AppCompatActivity {

    private FrameLayout HomeStaffPatientsButton, HomeStaffMedicationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_staff);

        HomeStaffPatientsButton = findViewById(R.id.HomeStaffPatientsButton);
        HomeStaffMedicationButton = findViewById(R.id.HomeStaffMedicationButton);

        HomeStaffPatientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page activity = new Page(HomePageStaff.this, "Patients");
                Intent intent = activity.changePage();
                startActivity(intent);
            }
        });

        HomeStaffMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page activity = new Page(HomePageStaff.this, "Medication");
                Intent intent = activity.changePage();
                startActivity(intent);
            }
        });


    }
}