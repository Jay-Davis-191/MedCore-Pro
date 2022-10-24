package com.example.myapplication;

// DatePickerDialog code was taken from https://www.youtube.com/watch?v=qCoidM98zNk

import static com.example.myapplication.Database.appDatabase;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentPopUp extends AppCompatActivity {

    public static Button dateButton, timeButton;
    private static Button saveNewAppointmentButton;
    private DatePickerDialog datePickerDialog;
    private int hour, minute, patientID;
    public static Spinner newAppointmentPatientName, newAppointmentHospitalName, newAppointmentDoctorName;
    private static Cursor hospitalCursor, patientCursor;
    private static String hospitalID, appointmentDate, appointmentTime;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        timeButton = findViewById(R.id.timePickerButton);
        saveNewAppointmentButton = findViewById(R.id.saveNewAppointmentButton);
        newAppointmentPatientName = findViewById(R.id.newAppointmentPatientName);
        newAppointmentHospitalName = findViewById(R.id.newAppointmentHospitalName);
        newAppointmentDoctorName = findViewById(R.id.newAppointmentDoctorName);

        patientID = getIntent().getIntExtra("Patient ID", 0);

        newAppointmentDoctorName.setEnabled(false);
        newAppointmentDoctorName.setClickable(false);

        // Sets today's date as the text for the Date TextField
        dateButton.setText(getTodaysDate());
        // Sets today as the minimum date

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Occurs if the Add Appointment button is clicked from a selected Patient profile

        if (patientID > 0) {
            retrieveInformation(true);
            newAppointmentPatientName.setEnabled(false);
            newAppointmentPatientName.setClickable(false);
        }
        else {
            patientCursor = SQLQueries.getPatientsForNewAppointment();
            patientCursor.moveToFirst();
            ////////////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            patientID = Integer.valueOf(patientCursor.getString(0));
            String[] patientSpinnerValues = new String[patientCursor.getCount()];
            int count = 0;
            while(!patientCursor.isAfterLast()) {
                patientSpinnerValues[count] = patientCursor.getString(1) + " " + patientCursor.getString(2) + ", " + patientCursor.getString(3);
                count++;
                patientCursor.moveToNext();
            }

            ArrayAdapter<String> patientValues;
            patientValues = new ArrayAdapter<>(this, R.layout.spinner_design, patientSpinnerValues);
            patientValues.setDropDownViewResource(R.layout.spinner_items_design);
            newAppointmentPatientName.setAdapter(patientValues);
            newAppointmentPatientName.setSelection(0);
            retrieveInformation(false);
        }

        //----------Initializes the objects used for creating the size of this activity. This is used for making this activity appear like a pop-up, instead of a new page.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  // Retrieves the width of the screen in pixels
        int height = dm.heightPixels;  // Retrieves the height of the screen in pixels
        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));  // Sets the screen size of the activity.

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view);
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker(view);
            }
        });

        // ID, Date and Time, patient Name, doctor name, medication, notes,
        //AppointmentID, Time, Date, Description, Completed, Missed, HospitalID, PatientID, StaffID, PrescriptionID INT);");


        newAppointmentPatientName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                patientID = SQLQueries.findSpecificPatient(newAppointmentPatientName.getSelectedItem().toString());
                retrieveInformation(false);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        newAppointmentHospitalName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                hospitalID = "";
                String selectedHospital = newAppointmentHospitalName.getSelectedItem().toString();
                String currentHospital = "";

                ////////Continue working here

                hospitalCursor = SQLQueries.retrieveAllHospitalsNamesForNewAppointmentPage2();
                hospitalCursor.moveToFirst();
                while(!hospitalCursor.isAfterLast()) {
                    currentHospital = hospitalCursor.getString(0) + ", " + hospitalCursor.getString(1);
                    if (currentHospital.equals(selectedHospital)) {
                        hospitalID = hospitalCursor.getString(2);
                        break;
                    }
                    hospitalCursor.moveToNext();
                }
                retrieveDoctors();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        saveNewAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String selectedDoctor = newAppointmentDoctorName.getSelectedItem().toString();
                    int selectedDoctorIndex = newAppointmentDoctorName.getSelectedItemPosition();

                    int doctorID = SQLQueries.findSpecificDoctorForNewAppointmentPage(selectedDoctorIndex, hospitalID);

                    appointmentTime = timeButton.getText().toString();
                    String[] appointmentTimeSplit = appointmentTime.split(":");
                    String hours = appointmentTimeSplit[0];
                    String minutes = appointmentTimeSplit[1];

                    if (appointmentTime.contains("PM")) {
                        hours += Integer.parseInt(hours) + 12;
                    }

                    appointmentTime = hours + ":" + minutes;

                    SQLQueries.addAppointment(patientID, hospitalID, doctorID, appointmentDate, appointmentTime);
                    finish();
            }
        });
    }


    public void popTimePicker(View view)
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

        // int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }


    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        appointmentDate = day + "/" + month + "/" + year;
        return makeDateString(day, month, year);
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
                appointmentDate = day + "/" + month + "/" + year;

                dateButton.setText(date);
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


    private void retrieveInformation(boolean specificPatientSelected) {
        Cursor patientCursor = SQLQueries.retrieveIndividualPatientData(String.valueOf(patientID));
        patientCursor.moveToFirst();

        String[] patientSpinnerValues = new String[patientCursor.getColumnCount()];
        for (int i = 0; i < patientCursor.getColumnCount(); i++) {
            patientSpinnerValues[i] = patientCursor.getString(i);
        }

        String patientName = patientSpinnerValues[1] + " " + patientSpinnerValues[2] + ", " + patientSpinnerValues[7];
        String[] names = new String[1];
        names[0] = patientName;

        if (specificPatientSelected) {
            ArrayAdapter<String> nameValues;
            nameValues = new ArrayAdapter<>(this, R.layout.spinner_design, names);
            nameValues.setDropDownViewResource(R.layout.spinner_items_design);
            newAppointmentPatientName.setAdapter(nameValues);
            newAppointmentPatientName.setSelection(0);
        }

        String sectionedStatus = patientCursor.getString(3);
        String[] hospitalSpinnerValues;

        ////////////////////////////
        if (sectionedStatus.equals("Yes")) {
            hospitalCursor =  SQLQueries.retrieveSectionedHospitalForNewAppointmentPage(String.valueOf(patientID));
            hospitalCursor.moveToFirst();

            hospitalSpinnerValues = new String[hospitalCursor.getCount()];
            for (int i = 0; i < hospitalCursor.getCount(); i++) {
                hospitalSpinnerValues[i] = hospitalCursor.getString(0) + ", " + hospitalCursor.getString(1);
                hospitalCursor.moveToNext();
            }

            String hospitalName = hospitalSpinnerValues[1];
            String[] hospitalNames = new String[1];
            hospitalNames[0] = hospitalName;

            ArrayAdapter<String> hospitalValues;
            hospitalValues = new ArrayAdapter<>(this, R.layout.spinner_design, hospitalNames);
            hospitalValues.setDropDownViewResource(R.layout.spinner_items_design);
            newAppointmentHospitalName.setAdapter(hospitalValues);
            newAppointmentHospitalName.setSelection(0);
        }

        else {
            hospitalCursor =  SQLQueries.retrieveAllHospitalsNamesForNewAppointmentPage2();
            hospitalCursor.moveToFirst();

            hospitalSpinnerValues = new String[hospitalCursor.getCount()];
            for (int i = 0; i < hospitalCursor.getCount(); i++) {
                hospitalSpinnerValues[i] = hospitalCursor.getString(0) + ", " + hospitalCursor.getString(1);
                hospitalCursor.moveToNext();
            }

            ArrayAdapter<String> hospitalValues;
            hospitalValues = new ArrayAdapter<>(this, R.layout.spinner_design, hospitalSpinnerValues);
            hospitalValues.setDropDownViewResource(R.layout.spinner_items_design);
            newAppointmentHospitalName.setAdapter(hospitalValues);
        }
    }


    private void retrieveDoctors() {
        String[] allDoctorNames = SQLQueries.retrieveAllDoctorsNamesForNewAppointmentPage(hospitalID);
        newAppointmentDoctorName.setEnabled(true);
        newAppointmentDoctorName.setClickable(true);

        ArrayAdapter<String> doctorValues;
        doctorValues = new ArrayAdapter<>(this, R.layout.spinner_design, allDoctorNames);
        doctorValues.setDropDownViewResource(R.layout.spinner_items_design);
        newAppointmentDoctorName.setAdapter(doctorValues);
        newAppointmentDoctorName.setSelection(0);
    }


}