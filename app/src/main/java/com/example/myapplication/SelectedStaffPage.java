package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;

public class SelectedStaffPage extends AppCompatActivity {

    public static EditText selectedStaffIDText, selectedStaffSalaryText;
    private static TextView selectedStaffNameText;
    public static Spinner selectedStaffOccupationSpinner, selectedStaffHospitalSpinner;
    private static TableLayout selectedStaffUpcomingAppointmentsTableLayout;
    private static String[] occupations, hospitals;
    private static String ID, selectedHospital, selectedOccupation;
    private static Cursor staffProfileCursor;
    private static String[][] upcomingAppointments;
    private int NUMBER_OF_NEXT_APPOINTMENTS = 6;
    private static SharedPreferences appDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_staff_page);

        selectedStaffNameText = findViewById(R.id.selectedStaffNameText);
        selectedStaffOccupationSpinner = findViewById(R.id.selectedStaffOccupationSpinner);
        selectedStaffIDText = findViewById(R.id.selectedStaffIDText);
        selectedStaffSalaryText = findViewById(R.id.selectedStaffSalaryText);
        //selectedStaffUpcomingAppointmentsTableLayout = findViewById(R.id.selectedStaffUpcomingAppointmentsTableLayout);
        selectedStaffHospitalSpinner = findViewById(R.id.selectedStaffHospitalSpinner);

        occupations = new String[]{"Doctor", "Nurse", "Receptionist", "Administrator", "Volunteer"};
        ArrayAdapter<String> occupationValues;
        occupationValues = new ArrayAdapter<>(this, R.layout.sectioned_spinner_design, occupations);
        occupationValues.setDropDownViewResource(R.layout.sectioned_spinner_items_design);
        selectedStaffOccupationSpinner.setAdapter(occupationValues);

        hospitals = SQLQueries.retrieveAllHospitalsNamesForNewAppointmentPage(false);

        ArrayAdapter<String> hospitalValues;
        hospitalValues = new ArrayAdapter<>(this, R.layout.sectioned_spinner_design, hospitals);
        hospitalValues.setDropDownViewResource(R.layout.sectioned_spinner_items_design);
        selectedStaffHospitalSpinner.setAdapter(hospitalValues);

        selectedStaffOccupationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedOccupation = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        selectedStaffHospitalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedHospital = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        Database.appDatabase = openOrCreateDatabase("app database", Context.MODE_PRIVATE, null);
        ID = String.valueOf(getIntent().getIntExtra("ID", 0));
        String name, firstName, lastName, occupation, salary, hospitalID;

        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        staffProfileCursor = SQLQueries.retrieveIndividualStaffDataForSelectedStaffPage(ID);
        staffProfileCursor.moveToFirst();

        firstName = staffProfileCursor.getString(1);
        lastName = staffProfileCursor.getString(2);
        occupation = staffProfileCursor.getString(3);
        salary = staffProfileCursor.getString(4);
        hospitalID = staffProfileCursor.getString(5);

        name = "";
        if (occupation.equals("Doctor")) {
            name += "Dr ";
        }
        name += firstName + " " + lastName;

        selectedHospital = SQLQueries.retrieveSelectedHospital(ID);

        selectedStaffNameText.setText(name);
        selectedStaffOccupationSpinner.setSelection(Arrays.asList(occupations).indexOf(occupation));
        selectedStaffIDText.setText("Unique Staff Identifier: " + ID);
        selectedStaffSalaryText.setText(decimalFormat.format(Integer.valueOf(salary)));
        selectedStaffHospitalSpinner.setSelection(Arrays.asList(hospitals).indexOf(selectedHospital));

        //String[] neededHeaders = new String[]{"ID", "Patient", "Appointment Date & Time", "Location"};
        //upcomingAppointments = new String[NUMBER_OF_NEXT_APPOINTMENTS+1][neededHeaders.length];
        //upcomingAppointments[0] = neededHeaders;

        //String appointmentID, patientName, dateAndTime, hospitalAddress;

        //staffProfileCursor = SQLQueries.retrieveUpcomingAppointmentsForSelectedStaff(ID);
        //if (staffProfileCursor.moveToFirst()) {
        //    staffProfileCursor.moveToFirst();
        //    int count = 1;
        //    while (!staffProfileCursor.isAfterLast()) {
        //        upcomingAppointments[count][0] = staffProfileCursor.getString(0);
        //        upcomingAppointments[count][1] = staffProfileCursor.getString(1) + " " + staffProfileCursor.getString(2);
        //        upcomingAppointments[count][2] = staffProfileCursor.getString(4) + " " + staffProfileCursor.getString(3);
        //        upcomingAppointments[count][3] = staffProfileCursor.getString(5);
        //        count++;
        //        staffProfileCursor.moveToNext();
        //    }
        //}

        //selectedStaffUpcomingAppointmentsTableLayout.removeAllViews();
        // Prints the two upcoming appointments for the selected staff member
        //selectedStaffUpcomingAppointmentsTableLayout.addView(new SimpleTextTableWithBorders(SelectedStaffPage.this, upcomingAppointments, selectedStaffUpcomingAppointmentsTableLayout, "StaffProfile", neededHeaders));
        appDetails = getSharedPreferences("appDetails", Context.MODE_PRIVATE);
        Boolean isAdmin = appDetails.getBoolean("adminRights", false);
        applyToWidgets(isAdmin);
    }


    // Applies the correct admin rights to the widgets
    private static void applyToWidgets(Boolean status) {
        selectedStaffNameText.setEnabled(false);
        selectedStaffOccupationSpinner.setEnabled(status);
        selectedStaffIDText.setEnabled(false);
        selectedStaffSalaryText.setEnabled(status);
        selectedStaffHospitalSpinner.setEnabled(status);
    }


    @Override
    protected void onPause() {
        SQLQueries.updateStaffProfile(ID);
        super.onPause();
    }
}