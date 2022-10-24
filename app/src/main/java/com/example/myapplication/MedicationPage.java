package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

public class MedicationPage extends AppCompatActivity {

    public static Spinner medicationNameSortSpinner;
    public static EditText medicationPageSearch;
    private static TableLayout medicationTableLayout;
    private static Button addMedicationButton;
    private static Cursor medicationCursor;
    private static String[][] medicationData, spinnerValues;
    private static String[] neededHeaders;
    private static SharedPreferences appDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_page);

        medicationPageSearch = findViewById(R.id.medicationPageSearch);
        medicationTableLayout = findViewById(R.id.medicationTableLayout);
        addMedicationButton = findViewById(R.id.addMedicationButton);
        medicationNameSortSpinner = findViewById(R.id.medicationNameSortSpinner);

        medicationNameSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        medicationPageSearch.setOnKeyListener(new View.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && ((i == KeyEvent.KEYCODE_ENTER) || (i == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    createTable();
                    return true;
                }
                return false;
            }
        });

        addMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicationPage.this, AddMedicationPopUp.class);
                startActivity(intent);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTable() {
        medicationTableLayout.removeAllViews();
        medicationCursor = SQLQueries.retrieveAllMedication();
        if (medicationCursor.moveToFirst()) {
            medicationCursor.moveToFirst();

            medicationData = new String[medicationCursor.getCount() + 1][neededHeaders.length + 1];
            medicationData[0] = neededHeaders;
            int count = 1;
            String ID, name, volume, cost, description;
            while (!medicationCursor.isAfterLast()) {
                ID = medicationCursor.getString(0);
                name = medicationCursor.getString(1);
                volume = medicationCursor.getString(2);
                cost = medicationCursor.getString(3);
                description = medicationCursor.getString(4);

                medicationData[count] = new String[]{ID, name, volume, cost, description};
                count++;
                medicationCursor.moveToNext();
            }
            medicationTableLayout.addView(new SimpleTextTableWithBorders(MedicationPage.this, medicationData, medicationTableLayout, "Medication", neededHeaders));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        medicationTableLayout.removeAllViews();
        Database.appDatabase = openOrCreateDatabase("app database", Context.MODE_PRIVATE, null);
        neededHeaders = new String[]{"ID", "Medication Name", "Quantity Per Supply", "Cost", "Description"};

        spinnerValues = new String[3][];
        spinnerValues[0] = new String[]{"", "A-Z", "Z-A"};
        spinnerValues[1] = SQLQueries.retrieveAllStaff();
        spinnerValues[2] = SQLQueries.retrieveAllUpcomingAppointmentDates();

        ArrayAdapter<String> alphabetValues, doctorValues, appointmentValues;

        alphabetValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[0]);
        alphabetValues.setDropDownViewResource(R.layout.spinner_items_design);

        doctorValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[1]);
        doctorValues.setDropDownViewResource(R.layout.spinner_items_design);

        appointmentValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[2]);
        appointmentValues.setDropDownViewResource(R.layout.spinner_items_design);

        medicationNameSortSpinner.setAdapter(alphabetValues);

        createTable();
        appDetails = getSharedPreferences("appDetails", Context.MODE_PRIVATE);
        Boolean isAdmin = appDetails.getBoolean("adminRights", false);
        applyToWidgets(isAdmin);
    }


    // Applies the correct admin rights to the widgets
    private static void applyToWidgets(Boolean status) {
        addMedicationButton.setEnabled(status);
    }
}