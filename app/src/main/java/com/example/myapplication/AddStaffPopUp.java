package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddStaffPopUp extends AppCompatActivity {

    public static EditText newStaffNameText, newStaffSalaryText;
    public static Spinner newStaffOccupationSpinner, newStaffHospitalSpinner;
    private static Button saveNewStaffButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff_pop_up);

        newStaffNameText = findViewById(R.id.newStaffNameText);
        newStaffSalaryText = findViewById(R.id.newStaffSalaryText);
        newStaffOccupationSpinner = findViewById(R.id.newStaffOccupationSpinner);
        newStaffHospitalSpinner = findViewById(R.id.newStaffHospitalSpinner);
        saveNewStaffButton = findViewById(R.id.saveNewStaffButton);

        String[] occupations = new String[]{"Doctor", "Nurse", "Receptionist", "Volunteer", "Administrator"};
        ArrayAdapter<String> occupationValues;
        occupationValues = new ArrayAdapter<>(this, R.layout.spinner_design, occupations);
        occupationValues.setDropDownViewResource(R.layout.spinner_items_design);
        newStaffOccupationSpinner.setAdapter(occupationValues);

        String[] hospitals = SQLQueries.retrieveAllHospitalsNamesForNewAppointmentPage(false);
        ArrayAdapter<String> hospitalValues;
        hospitalValues = new ArrayAdapter<>(this, R.layout.spinner_design, hospitals);
        hospitalValues.setDropDownViewResource(R.layout.spinner_items_design);
        newStaffHospitalSpinner.setAdapter(hospitalValues);

        //----------Initializes the objects used for creating the size of this activity. This is used for making this activity appear like a pop-up, instead of a new page.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  // Retrieves the width of the screen in pixels
        int height = dm.heightPixels;  // Retrieves the height of the screen in pixels
        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));  // Sets the screen size of the activity.

        saveNewStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateEntries()) {
                    SQLQueries.addStaff();
                    finish();
                }
            }
        });
    }


    private boolean validateEntries() {
        String newStaffNameTextString = newStaffNameText.getText().toString().trim();
        String newStaffSalaryTextString = newStaffSalaryText.getText().toString().trim();

        if (newStaffNameTextString.equals("")) {
            Toast.makeText(this, "Please provide a name", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newStaffNameTextString.split(" ").length == 1) {
            Toast.makeText(this, "Please ensure there is a first and last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newStaffNameTextString.split(" ").length > 2) {
            Toast.makeText(this, "Please ensure there is only a first name and last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newStaffSalaryTextString.equals("")) {
            Toast.makeText(this, "Please provide an address", Toast.LENGTH_SHORT).show();
            return false;
        }

        else {
            return true;
        }
    }

}