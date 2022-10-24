package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class SelectedPatientPage extends AppCompatActivity {
    
    private static String[][] upcomingAppointments;
    private static String[] patientData, spinnerValues;
    public static EditText adminPatientDOBText, adminPatientAddressText, adminPatientIDText, adminPatientConditionsText, adminPatientMedicationText, adminPatientVisitationText, adminPatientNextOfKinText;
    public static EditText adminPatientNameText;
    private TableLayout patientsProfileUpcomingAppointmentsTableLayout;
    private static Cursor patientProfileCursor, nextApptsCursor;
    private int NUMBER_OF_NEXT_APPOINTMENTS = 3;
    private static String ID, nextApptTime, residentialAddress, sectionedHospital, sectionedStatus, sectioned, homeCare, address;
    private static SharedPreferences appDetails;
    private static Spinner adminPatientSectionedSpinner;
    private static Button selectedPatientAddAppointmentButton;
    private static TextView patientSectionedStatusText;
    private static boolean isAdmin;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_patient_page);

        adminPatientNameText = findViewById(R.id.adminPatientNameText);
        adminPatientDOBText = findViewById(R.id.adminPatientDOBText);
        adminPatientAddressText = findViewById(R.id.adminPatientAddressText);
        adminPatientIDText = findViewById(R.id.adminPatientIDText);
        adminPatientConditionsText = findViewById(R.id.adminPatientConditionsText);
        adminPatientMedicationText = findViewById(R.id.adminPatientMedicationText);
        adminPatientVisitationText = findViewById(R.id.adminPatientVisitationText);
        adminPatientNextOfKinText = findViewById(R.id.adminPatientNextOfKinText);
        adminPatientSectionedSpinner = findViewById(R.id.adminPatientSectionedSpinner);
        patientsProfileUpcomingAppointmentsTableLayout = findViewById(R.id.patientsProfileUpcomingAppointmentsTableLayout);
        selectedPatientAddAppointmentButton = findViewById(R.id.selectedPatientAddAppointmentButton);
        patientSectionedStatusText = findViewById(R.id.patientSectionedStatusText);


        adminPatientSectionedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sectionedStatus = adapterView.getSelectedItem().toString();
                SQLQueries.updateSectionedStatusForSelectedPatient(sectionedStatus, ID);
                getAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectedPatientAddAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //////------->SQLQueries.updateSelectedPatientsData(sectionedStatus, ID);
                Intent intent = new Intent(SelectedPatientPage.this, AddAppointmentPopUp.class);
                intent.putExtra("Patient ID", Integer.parseInt(ID));
                startActivity(intent);
            }
        });
    }  // ends onCreate()


    private static void obtainDetails(int i) {
        String[] details = SQLQueries.retrieveIndividualPatientsNextAppointment(ID, i);

        String nextApptID = "";
        String nextApptDateTime = "";
        String nextApptStaffID = "";
        String nextApptStaffName = "";
        String nextApptHospitalName = "";

        if (details[0] != null) {

            nextApptID = details[0];

            if (details[2] != null && !details[2].isEmpty()) {
                String[] nextApptTimeList = details[2].split(":");
                nextApptTime = nextApptTimeList[0] + ":" + nextApptTimeList[1];
                nextApptDateTime = details[1] + " " + nextApptTime;
                nextApptStaffID = details[3];
                nextApptStaffName = "Dr " + details[4] + " " + details[5];
                nextApptHospitalName = details[6];
            }

            if (nextApptID.contains("null") || nextApptDateTime.contains("null") || nextApptHospitalName.contains("null") || nextApptStaffName.contains("null")) {
                nextApptID = "";
                nextApptDateTime = "";
                nextApptHospitalName = "";
                nextApptStaffName = "";
            }
        }
            upcomingAppointments[i + 1] = new String[]{nextApptID, nextApptDateTime, nextApptHospitalName, nextApptStaffName};
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        Database.appDatabase = openOrCreateDatabase("app database", Context.MODE_PRIVATE, null);
        ID = String.valueOf(getIntent().getIntExtra("ID", 0));
        String firstName, lastName, notes, DOB, totalAppointments, missedAppointments, nextOfKin;

        patientProfileCursor = SQLQueries.retrieveIndividualPatientData(ID);
        patientProfileCursor.moveToFirst();

        firstName = patientProfileCursor.getString(1);
        lastName = patientProfileCursor.getString(2);
        sectioned = patientProfileCursor.getString(3);
        homeCare = patientProfileCursor.getString(4);
        notes = patientProfileCursor.getString(5);
        DOB = patientProfileCursor.getString(6);
        residentialAddress = patientProfileCursor.getString(7);
        totalAppointments = patientProfileCursor.getString(8);
        missedAppointments = patientProfileCursor.getString(9);
        nextOfKin = patientProfileCursor.getString(10);

        adminPatientNameText.setText(firstName + " " + lastName);

        String years = SimpleTextTableWithBorders.retrieveAge(DOB);
        adminPatientDOBText.setText("Age: " + years + ", DOB: " + DOB);

        spinnerValues = new String[]{"Neither", "HomeCared", "Sectioned"};
        ArrayAdapter<String> sectionedValues;
        sectionedValues = new ArrayAdapter<>(this, R.layout.sectioned_spinner_design, spinnerValues);
        sectionedValues.setDropDownViewResource(R.layout.sectioned_spinner_items_design);
        adminPatientSectionedSpinner.setAdapter(sectionedValues);

        adminPatientIDText.setText("Unique Patient Identifier: " + ID);

        adminPatientConditionsText.setText(notes);
        String currentMedication = SQLQueries.retrieveIndividualPatientMedication(ID);
        adminPatientMedicationText.setText(currentMedication);
        adminPatientNextOfKinText.setText(nextOfKin);

        int differencePercentage;

        if (Integer.parseInt(totalAppointments) > 0) {
            if (Integer.parseInt(totalAppointments) <= Integer.parseInt(missedAppointments)) {
                adminPatientVisitationText.setText("Visitation Percentage: 0%");
            }
            else if (Integer.parseInt(totalAppointments) - Integer.parseInt(missedAppointments) <= 0) {
                adminPatientVisitationText.setText("Visitation Percentage: 0%");
            }
            else {
                differencePercentage = ((Integer.parseInt(totalAppointments) - Integer.parseInt(missedAppointments)) * 100) / Integer.parseInt(totalAppointments);
                adminPatientVisitationText.setText("Visitation Percentage: " + differencePercentage + "%");
            }
        }
        else {
            adminPatientVisitationText.setText("Visitation Percentage: N/A");
        }

        // Below code is for the 'upcoming appointments' table
        String[] neededHeaders = new String[]{"ID", "Appointment Date & Time", "Location", "Doctor"};
        upcomingAppointments = new String[NUMBER_OF_NEXT_APPOINTMENTS][neededHeaders.length];
        upcomingAppointments[0] = neededHeaders;

        obtainDetails(0);
        obtainDetails(1);

        patientsProfileUpcomingAppointmentsTableLayout.removeAllViews();
        // Prints the two upcoming appointments for the selected patient
        patientsProfileUpcomingAppointmentsTableLayout.addView(new SimpleTextTableWithBorders(SelectedPatientPage.this, upcomingAppointments, patientsProfileUpcomingAppointmentsTableLayout, "PatientsProfile", neededHeaders));


        appDetails = getSharedPreferences("appDetails", Context.MODE_PRIVATE);
        isAdmin = appDetails.getBoolean("adminRights", false);
        getAddress();
        applyToWidgets(isAdmin);
    }


    @Override
    protected void onPause() {
        SQLQueries.updateSelectedPatientsData(sectionedStatus, ID);
        super.onPause();
    }


    private static void getAddress() {
        sectionedStatus = SQLQueries.retrieveSelectedPatientsSectionedStatus(ID);
        adminPatientSectionedSpinner.setSelection(Arrays.asList(spinnerValues).indexOf(sectionedStatus));

        if (sectionedStatus.equals("Sectioned")) {
            patientProfileCursor = SQLQueries.retrieveSectionedHospital(ID);
            patientProfileCursor.moveToFirst();
            String sectionedHospital = patientProfileCursor.getString(0);
            patientSectionedStatusText.setText("Sectioned at: ");
            address = sectionedHospital;
            adminPatientAddressText.setEnabled(false);
        }

        else if (sectionedStatus.equals("HomeCared")) {
            patientSectionedStatusText.setText("HomeCared at: ");
            address = residentialAddress;
            adminPatientAddressText.setEnabled(true);
        }

        else {
            patientSectionedStatusText.setText("Residential Address: ");
            address = residentialAddress;
            adminPatientAddressText.setEnabled(true);
        }
        adminPatientAddressText.setText(address);
    }


    // Applies the correct admin rights to the EditText widgets
    private static void applyToWidgets(Boolean status) {
        adminPatientNameText.setEnabled(false);
        adminPatientDOBText.setEnabled(false);
        adminPatientAddressText.setEnabled(status);
        adminPatientIDText.setEnabled(false);
        adminPatientConditionsText.setEnabled(status);
        adminPatientMedicationText.setEnabled(false);
        adminPatientVisitationText.setEnabled(false);
        adminPatientNextOfKinText.setEnabled(status);
        adminPatientSectionedSpinner.setEnabled(status);
        selectedPatientAddAppointmentButton.setEnabled(status);
    }
}