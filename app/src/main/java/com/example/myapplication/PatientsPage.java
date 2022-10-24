package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatientsPage extends AppCompatActivity {
    public static TableLayout patientsTableLayout;
    private Cursor patientCursor;
    private String[] neededHeaders, patientData;
    private String[][] spinnerValues;
    private static String[][] patientDataNew;
    public static EditText patientsPageSearch;
    public static Button addPatientButton;
    public static Spinner patientsProfilePatientNameSort, patientsProfilePatientMedicationFilter, patientsProfileDoctorFilter, patientsProfileAppointmentFilter;
    private static SharedPreferences appDetails;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        patientsTableLayout = findViewById(R.id.patientsTableLayout);
        patientsPageSearch = findViewById(R.id.patientsPageSearch);
        patientsProfilePatientNameSort = findViewById(R.id.patientsProfilePatientNameSort);
        patientsProfilePatientMedicationFilter = findViewById(R.id.patientsProfilePatientMedicationFilter);
        patientsProfileDoctorFilter = findViewById(R.id.patientsProfileDoctorFilter);
        patientsProfileAppointmentFilter = findViewById(R.id.patientsProfileAppointmentFilter);
        addPatientButton = findViewById(R.id.addPatientButton);

        patientsProfilePatientNameSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        patientsProfilePatientMedicationFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        patientsProfileDoctorFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        patientsProfileAppointmentFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        patientsPageSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && ((i == KeyEvent.KEYCODE_ENTER) || (i == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    createTable();
                    return true;
                }
                return false;
            }
        });


        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientsPage.this, AddPatient.class);
                startActivity(intent);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        patientsTableLayout.removeAllViews();
        Database.appDatabase = openOrCreateDatabase("app database", Context.MODE_PRIVATE, null);
        neededHeaders = new String[]{"ID", "Patient Name", "Age", "Next Appt", "Notes"};

        spinnerValues = new String[4][];

        // Assign spinner values
        spinnerValues[0] = new String[]{"", "A-Z", "Z-A"};
        spinnerValues[1] = SQLQueries.retrieveAllMedicationNames(true);
        spinnerValues[2] = SQLQueries.retrieveAllStaff();
        spinnerValues[3] = SQLQueries.retrieveAllUpcomingAppointmentDates();

        ArrayAdapter<String> alphabetValues, medicationValues, doctorValues, appointmentValues;

        alphabetValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[0]);
        alphabetValues.setDropDownViewResource(R.layout.spinner_items_design);

        medicationValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[1]);
        medicationValues.setDropDownViewResource(R.layout.spinner_items_design);

        doctorValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[2]);
        doctorValues.setDropDownViewResource(R.layout.spinner_items_design);

        appointmentValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[3]);
        appointmentValues.setDropDownViewResource(R.layout.spinner_items_design);

        patientsProfilePatientNameSort.setAdapter(alphabetValues);
        patientsProfilePatientMedicationFilter.setAdapter(medicationValues);
        patientsProfileDoctorFilter.setAdapter(doctorValues);
        patientsProfileAppointmentFilter.setAdapter(appointmentValues);

        createTable();

        appDetails = getSharedPreferences("appDetails", Context.MODE_PRIVATE);
        Boolean isAdmin = appDetails.getBoolean("adminRights", false);
        applyToWidgets(isAdmin);
    }


    // Applies the correct admin rights to the widgets
    private static void applyToWidgets(Boolean status) {
        addPatientButton.setEnabled(status);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTable() {
        patientsTableLayout.removeAllViews();
        patientCursor = SQLQueries.retrieveAllPatients();
        patientCursor.moveToFirst();

        patientDataNew = new String[patientCursor.getCount() + 1][neededHeaders.length + 1];
        patientDataNew[0] = neededHeaders;

        int count = 1;
        String ID, firstName, lastName, DOB, nextAppt, notes;

        while (!patientCursor.isAfterLast()) {
            ID = patientCursor.getString(0);
            firstName = patientCursor.getString(1);
            lastName = patientCursor.getString(2);
            DOB  = patientCursor.getString(6);
            notes = patientCursor.getString(5);

            Cursor nextAppointment = SQLQueries.retrieveNextAppointment(patientCursor.getString(0));

            if (nextAppointment.moveToFirst()) {
                nextAppointment.moveToFirst();
                nextAppt = nextAppointment.getString(1);
            }
            else {
                nextAppt = "";
            }

            patientDataNew[count] = new String[]{ID, firstName, lastName, DOB, nextAppt, notes};
            count++;
            patientCursor.moveToNext();
        }

        patientsTableLayout.addView(new SimpleTextTableWithBorders(PatientsPage.this, patientDataNew, patientsTableLayout, "Patients", neededHeaders));
    }
}