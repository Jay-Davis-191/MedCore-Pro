package com.example.myapplication;

import static com.example.myapplication.Database.appDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class AddPatient extends AppCompatActivity {

    public static EditText newPatientName, newPatientAddress, newPatientNextOfKin, newPatientNotes;
    public static Button newPatientDOB, saveNewPatientButton;
    private DatePickerDialog datePickerDialog;
    public static String DOB;
    private static Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        initDatePicker();

        newPatientName = findViewById(R.id.newPatientName);
        newPatientAddress = findViewById(R.id.newPatientAddress);
        newPatientNextOfKin = findViewById(R.id.newPatientNextOfKin);
        newPatientNotes = findViewById(R.id.newPatientNotes);
        newPatientDOB = findViewById(R.id.newPatientDOB);
        saveNewPatientButton = findViewById(R.id.saveNewPatientButton);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        //----------Initializes the objects used for creating the size of this activity. This is used for making this activity appear like a pop-up, instead of a new page.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  // Retrieves the width of the screen in pixels
        int height = dm.heightPixels;  // Retrieves the height of the screen in pixels
        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));  // Sets the screen size of the activity.

        newPatientDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view);
            }
        });

        saveNewPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEntries()) {
                    int patientID = SQLQueries.addPatient();
                    cursor = appDatabase.rawQuery("SELECT AppointmentID FROM APPOINTMENTS ORDER BY AppointmentID DESC LIMIT 1", null);
                    cursor.moveToFirst();
                    int numberOfAppointments = Integer.parseInt(cursor.getString(0)) + 1;
                    appDatabase.execSQL("INSERT INTO APPOINTMENTS VALUES('" + numberOfAppointments + "', '9:00', '01/01/2000', '...', 'true', 'true', '1', '" + patientID + "', '1', 0)");
                    finish();
                }
            }
        });
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                DOB = day + "/" + month + "/" + year;
                newPatientDOB.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }


    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }


    public void openDatePicker(View view) {
        datePickerDialog.show();
    }


    private boolean validateEntries() {
        String newPatientNameText = newPatientName.getText().toString().trim();
        String newPatientAddressText = newPatientAddress.getText().toString().trim();
        String newPatientNextOfKinText = newPatientNextOfKin.getText().toString().trim();
        String newPatientNotesText = newPatientNotes.getText().toString().trim();
        String newPatientDOBText = newPatientDOB.getText().toString();

        if (newPatientNameText.equals("")) {
            Toast.makeText(this, "Please provide a name", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newPatientNameText.split(" ").length == 1) {
            Toast.makeText(this, "Please ensure there is a first and last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newPatientNameText.split(" ").length > 2) {
            Toast.makeText(this, "Please ensure there is only a first name and last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newPatientAddressText.equals("")) {
            Toast.makeText(this, "Please provide an address", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (newPatientNextOfKinText.equals("")) {
            Toast.makeText(this, "Please provide a next of kin", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newPatientNextOfKinText.split(" ").length == 1) {
            Toast.makeText(this, "Please ensure there is a first and last name for the next of kin", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newPatientNextOfKinText.split(" ").length > 2) {
            Toast.makeText(this, "Please ensure there is only a first name and last name for the next of kin", Toast.LENGTH_SHORT).show();
            return false;
        }


        else if (newPatientNotesText.equals("")) {
            Toast.makeText(this, "Please provide notes for the patient", Toast.LENGTH_SHORT).show();
            return false;
        }

        else {
            return true;
        }
    }
}