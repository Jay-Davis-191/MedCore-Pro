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
import android.widget.TextView;

public class AppointmentsPage extends AppCompatActivity {

    public static TableLayout appointmentsTableLayout;
    private Cursor appointmentCursor;
    private String[] neededHeaders, appointmentData;
    private String[][] allAppointmentData, appointmentDataNew, spinnerValues;
    public static EditText appointmentsPageSearch;
    public static Button addAppointmentButton;
    public static Spinner appointmentsPatientNameSort, appointmentsDateFilter, appointmentsDoctorFilter, appointmentsPreviousFilter;
    private static SharedPreferences appDetails;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_page);

        appointmentsTableLayout = findViewById(R.id.appointmentsTableLayout);
        appointmentsPageSearch = findViewById(R.id.appointmentsPageSearch);
        appointmentsPatientNameSort = findViewById(R.id.appointmentsPatientNameSort);
        appointmentsDateFilter = findViewById(R.id.appointmentsDateFilter);
        appointmentsDoctorFilter = findViewById(R.id.appointmentsDoctorFilter);
        appointmentsPreviousFilter = findViewById(R.id.appointmentsPreviousFilter);
        addAppointmentButton = findViewById(R.id.addAppointmentButton);

        appointmentsPatientNameSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SQLQueries.retrieveAllAppointments();
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        appointmentsDoctorFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SQLQueries.retrieveAllAppointments();
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        appointmentsDateFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SQLQueries.retrieveAllAppointments();
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        appointmentsPreviousFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SQLQueries.retrieveAllAppointments();
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        appointmentsPageSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && ((i == KeyEvent.KEYCODE_ENTER) || (i == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    SQLQueries.retrieveAllAppointments();
                    createTable();
                    return true;
                }
                return false;
            }
        });

        addAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentsPage.this, AddAppointmentPopUp.class);
                intent.putExtra("Patient ID", 0);
                startActivity(intent);
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        spinnerValues = new String[4][];

        // Assign spinner values
        spinnerValues[0] = SQLQueries.retrieveAllPatientNames();
        spinnerValues[1] = SQLQueries.retrieveAllUpcomingAppointmentDates();
        spinnerValues[2] = SQLQueries.retrieveAllStaff();
        spinnerValues[3] = new String[]{"", "Yes"};

        ArrayAdapter<String> patientValues, doctorValues, upcomingAppointmentValues, unmarkedValues;

        patientValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[0]);
        patientValues.setDropDownViewResource(R.layout.spinner_items_design);

        upcomingAppointmentValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[1]);
        upcomingAppointmentValues.setDropDownViewResource(R.layout.spinner_items_design);

        doctorValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[2]);
        doctorValues.setDropDownViewResource(R.layout.spinner_items_design);

        unmarkedValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[3]);
        unmarkedValues.setDropDownViewResource(R.layout.spinner_items_design);

        appointmentsPatientNameSort.setAdapter(patientValues);
        appointmentsDateFilter.setAdapter(upcomingAppointmentValues);
        appointmentsDoctorFilter.setAdapter(doctorValues);
        appointmentsPreviousFilter.setAdapter(unmarkedValues);

        appointmentsTableLayout.removeAllViews();
        Database.appDatabase = openOrCreateDatabase("app database", Context.MODE_PRIVATE, null);
        neededHeaders = new String[]{"ID", "Date & Time", "Patient Name", "Staff Name", "Medication", "Notes"};
        createTable();

        appDetails = getSharedPreferences("appDetails", Context.MODE_PRIVATE);
        Boolean isAdmin = appDetails.getBoolean("adminRights", false);
        applyToWidgets(isAdmin);
    }


    // Applies the correct admin rights to the widgets
    private static void applyToWidgets(Boolean status) {
        addAppointmentButton.setEnabled(status);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTable() {
        appointmentsTableLayout.removeAllViews();
        appointmentCursor = SQLQueries.retrieveAllAppointments();
        appointmentCursor.moveToFirst();

        appointmentDataNew = new String[appointmentCursor.getCount() + 1][appointmentCursor.getColumnCount() - 1];
        appointmentDataNew[0] = neededHeaders;

        int count = 1;
        String ID, Date, Time, patientFirstName, patientLastName, doctorFirstName, doctorLastName, medicationName, notes;

        while (!appointmentCursor.isAfterLast()) {
            ID = appointmentCursor.getString(0);
            Date = appointmentCursor.getString(1);
            Time = appointmentCursor.getString(2);
            patientFirstName = appointmentCursor.getString(3);
            patientLastName = appointmentCursor.getString(4);
            if (appointmentCursor.getString(9).equals("Doctor")) {
                doctorFirstName = "Dr " + appointmentCursor.getString(5);
            }
            else {
                doctorFirstName = appointmentCursor.getString(5);
            }
            doctorLastName = appointmentCursor.getString(6);
            medicationName = appointmentCursor.getString(7);
            notes = appointmentCursor.getString(8);

            appointmentDataNew[count] = new String[]{ID, Date, Time, patientFirstName, patientLastName, doctorFirstName, doctorLastName, medicationName, notes};
            count++;
            appointmentCursor.moveToNext();
        }

        appointmentsTableLayout.addView(new SimpleTextTableWithBorders(AppointmentsPage.this, appointmentDataNew, appointmentsTableLayout, "Appointments", neededHeaders));

    }
}