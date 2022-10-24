package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class SelectedMedicationPage extends AppCompatActivity {

    private static String ID, nextApptTime, residentialAddress, sectionedHospital, sectionedStatus, sectioned, homeCare, address;
    private static Cursor medicationCursor;
    public static EditText selectedMedicationVolumeText, selectedMedicationCostText, selectedMedicationNotesText;
    private static TextView selectedMedicationIDText, selectedMedicationNameText, selectedMedicationAdministeredDoctorsText, selectedMedicationPrescribedPatientsText;
    private static SharedPreferences appDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_medication_page);

        selectedMedicationVolumeText = findViewById(R.id.selectedMedicationVolumeText);
        selectedMedicationCostText = findViewById(R.id.selectedMedicationCostText);
        selectedMedicationAdministeredDoctorsText = findViewById(R.id.selectedMedicationAdministeredDoctorsText);
        selectedMedicationNotesText = findViewById(R.id.selectedMedicationNotesText);
        selectedMedicationIDText = findViewById(R.id.selectedMedicationIDText);
        selectedMedicationNameText = findViewById(R.id.selectedMedicationNameText);
        selectedMedicationPrescribedPatientsText = findViewById(R.id.selectedMedicationPrescribedPatientsText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Database.appDatabase = openOrCreateDatabase("app database", Context.MODE_PRIVATE, null);
        ID = String.valueOf(getIntent().getIntExtra("ID", 0));
        String name, volume, cost, description, administeredDoctors, prescribedPatients;
        prescribedPatients = "";

        medicationCursor = SQLQueries.retrieveSelectedMedicationData(ID);
        if (medicationCursor.moveToFirst()) {
            medicationCursor.moveToFirst();

            ID = medicationCursor.getString(0);
            name = medicationCursor.getString(1);
            volume = medicationCursor.getString(2);
            cost = medicationCursor.getString(3);
            description = medicationCursor.getString(4);
            administeredDoctors = SQLQueries.retrieveAllAdministeredDoctors(ID);
            prescribedPatients = SQLQueries.retrieveAllPrescribedPatients(ID);

            selectedMedicationIDText.setText(ID);
            selectedMedicationNameText.setText(name);
            selectedMedicationVolumeText.setText(volume);
            selectedMedicationCostText.setText(cost);
            selectedMedicationNotesText.setText(description);
            selectedMedicationPrescribedPatientsText.setText(prescribedPatients);
            selectedMedicationAdministeredDoctorsText.setText(administeredDoctors);
        }
        appDetails = getSharedPreferences("appDetails", Context.MODE_PRIVATE);
        Boolean isAdmin = appDetails.getBoolean("adminRights", false);
        applyToWidgets(isAdmin);
    }


    // Applies the correct admin rights to the widgets
    private static void applyToWidgets(Boolean status) {
        selectedMedicationVolumeText.setEnabled(status);
        selectedMedicationCostText.setEnabled(status);
        selectedMedicationNotesText.setEnabled(status);
    }

    @Override
    protected void onPause() {
        SQLQueries.updateSelectedMedication(ID);
        super.onPause();
    }

}