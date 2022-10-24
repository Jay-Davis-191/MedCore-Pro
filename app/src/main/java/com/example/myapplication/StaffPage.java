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

public class StaffPage extends AppCompatActivity {

    public static TableLayout staffTableLayout;
    public static EditText staffPageSearch;
    public static Spinner staffProfileStaffNameSort, staffProfileHospitalFilter, staffProfileOccupationFilter, patientsProfileAppointmentFilter;
    private static Button addStaffButton;
    private String[] neededHeaders;
    private String[][] spinnerValues, staffData;
    private Cursor staffCursor;
    private static SharedPreferences appDetails;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_page);

        staffPageSearch = findViewById(R.id.staffPageSearch);
        staffProfileStaffNameSort = findViewById(R.id.staffProfileStaffNameSort);
        staffProfileHospitalFilter = findViewById(R.id.staffProfileHospitalFilter);
        staffProfileOccupationFilter = findViewById(R.id.staffProfileOccupationFilter);
        staffTableLayout = findViewById(R.id.staffTableLayout);
        addStaffButton = findViewById(R.id.addStaffButton);
    }


    @Override
    protected void onResume() {
        super.onResume();
        staffTableLayout.removeAllViews();
        Database.appDatabase = openOrCreateDatabase("app database", Context.MODE_PRIVATE, null);
        neededHeaders = new String[]{"ID", "Name", "Occupation", "Hospital", "Hospital Address"};

        spinnerValues = new String[4][];

        // Assign spinner values

        String[] hospitalNames = new String[]{};
        spinnerValues[0] = new String[]{"", "A-Z", "Z-A"};
        spinnerValues[1] = SQLQueries.retrieveAllHospitalsNamesForNewAppointmentPage(true);;
        spinnerValues[2] = SQLQueries.retrieveAllOccupations();

        ArrayAdapter<String> patientValues, hospitalValues, occupationValues;

        patientValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[0]);
        patientValues.setDropDownViewResource(R.layout.spinner_items_design);

        hospitalValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[1]);
        hospitalValues.setDropDownViewResource(R.layout.spinner_items_design);

        occupationValues = new ArrayAdapter<>(this, R.layout.spinner_design, spinnerValues[2]);
        occupationValues.setDropDownViewResource(R.layout.spinner_items_design);

        staffProfileStaffNameSort.setAdapter(patientValues);
        staffProfileHospitalFilter.setAdapter(hospitalValues);
        staffProfileOccupationFilter.setAdapter(occupationValues);

        staffProfileStaffNameSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SQLQueries.retrieveAllStaffForStaffPage();
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        staffProfileHospitalFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SQLQueries.retrieveAllStaffForStaffPage();
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        staffProfileOccupationFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SQLQueries.retrieveAllStaffForStaffPage();
                createTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        staffPageSearch.setOnKeyListener(new View.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && ((i == KeyEvent.KEYCODE_ENTER) || (i == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    SQLQueries.retrieveAllStaffForStaffPage();
                    createTable();
                    return true;
                }
                return false;
            }
        });

        addStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffPage.this, AddStaffPopUp.class);
                startActivity(intent);
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTable() {
        staffTableLayout.removeAllViews();
        staffCursor = SQLQueries.retrieveAllStaffForStaffPage();
        staffCursor.moveToFirst();

        staffData = new String[staffCursor.getCount() + 1][neededHeaders.length];
        staffData[0] = neededHeaders;

        int count = 1;
        String ID, firstName, lastName, occupation, hospitalName, hospitalAddress;

        while (!staffCursor.isAfterLast()) {
            ID = staffCursor.getString(0);
            firstName = staffCursor.getString(1);
            lastName = staffCursor.getString(2);
            occupation  = staffCursor.getString(3);
            hospitalName = staffCursor.getString(5);
            hospitalAddress = staffCursor.getString(6);

            staffData[count] = new String[]{ID, firstName, lastName, occupation, hospitalName, hospitalAddress};
            count++;
            staffCursor.moveToNext();
        }

        staffTableLayout.addView(new SimpleTextTableWithBorders(StaffPage.this, staffData, staffTableLayout, "Staff", neededHeaders));
        appDetails = getSharedPreferences("appDetails", Context.MODE_PRIVATE);
        Boolean isAdmin = appDetails.getBoolean("adminRights", false);
        applyToWidgets(isAdmin);
    }


    // Applies the correct admin rights to the widgets
    private static void applyToWidgets(Boolean status) {
        addStaffButton.setEnabled(status);
    }
}