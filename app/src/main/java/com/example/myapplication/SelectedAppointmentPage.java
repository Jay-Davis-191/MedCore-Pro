package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SelectedAppointmentPage extends AppCompatActivity {
    private static String ID, patientID, patientName, doctorName, hospitalName, hospitalAddress, medicationCost, selectedDoctor, hospitalID;
    public static String date, time, medicationName, doctorID, prescriptionDescription, appointmentNotes, appointmentStatus, prescriptionID, medicationID;
    public static EditText selectedAppointmentDoctorNameText, selectedAppointmentMedicationNameText,
            selectedAppointmentPrescriptionDescriptionText, selectedAppointmentAppointmentNotesText;
    public static TextView selectedAppointmentPatientIDText, selectedAppointmentPatientNameText, selectedAppointmentDoctorIDText, selectedAppointmentHospitalNameText, selectedAppointmentHospitalAddressText, selectedAppointmentMedicationCostText;
    public static Spinner selectedAppointmentStatusSpinner, selectedAppointmentDoctorNameSpinner, selectedAppointmentMedicationNameSpinner;
    public static Button selectedAppointmentTimeButton, selectedAppointmentDateButton, selectedPatientDeleteAppointmentButton;
    private static String[] selectedAppointmentDetails, spinnerValues, doctorSpinnerValues, medicationSpinnerValues;
    private static TextView selectedAppointmentHeader;
    private static Cursor selectedAppointmentCursor;
    private static SharedPreferences appDetails;
    private static int doctorIDPosition;
    private DatePickerDialog datePickerDialog;
    private int hour, minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_appointment_page);
        initDatePicker();

        selectedAppointmentHeader = findViewById(R.id.selectedAppointmentHeader);
        selectedAppointmentPatientIDText = findViewById(R.id.selectedAppointmentPatientIDText);
        selectedAppointmentPatientNameText = findViewById(R.id.selectedAppointmentPatientNameText);
        selectedAppointmentDoctorIDText = findViewById(R.id.selectedAppointmentDoctorIDText);
        selectedAppointmentHospitalNameText = findViewById(R.id.selectedAppointmentHospitalNameText);
        selectedAppointmentHospitalAddressText = findViewById(R.id.selectedAppointmentHospitalAddressText);
        selectedAppointmentMedicationCostText = findViewById(R.id.selectedAppointmentMedicationCostText);
        selectedAppointmentPrescriptionDescriptionText = findViewById(R.id.selectedAppointmentPrescriptionDescriptionText);
        selectedAppointmentAppointmentNotesText = findViewById(R.id.selectedAppointmentAppointmentNotesText);
        selectedPatientDeleteAppointmentButton = findViewById(R.id.selectedPatientDeleteAppointmentButton);
        selectedAppointmentTimeButton = findViewById(R.id.selectedAppointmentTimeButton);
        selectedAppointmentDateButton = findViewById(R.id.selectedAppointmentDateButton);
        selectedAppointmentStatusSpinner = findViewById(R.id.selectedAppointmentStatusSpinner);
        selectedAppointmentDoctorNameSpinner = findViewById(R.id.selectedAppointmentDoctorNameSpinner);
        selectedAppointmentMedicationNameSpinner = findViewById(R.id.selectedAppointmentMedicationNameSpinner);

        // Still need to update this so it only accepts after the saved date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


        // Prevent UPDATRE SQL Query from updating the PrescriptionDescription if the doctor changes it.
        // Might make a button that opens a pop-up to change the prescription description, and if its changed, it creates a new prescription entry in Prescriptions and changes the assigned PrescriptionID value for the selected appointment

        selectedAppointmentStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                appointmentStatus = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        selectedAppointmentDoctorNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDoctor = adapterView.getSelectedItem().toString();
                String[] selectedDoctorSplit = selectedDoctor.split(" ");
                if (selectedDoctorSplit.length == 3) {
                    doctorID = SQLQueries.retrieveDoctorID(selectedDoctorSplit[1], selectedDoctorSplit[2], hospitalID);
                }
                else {
                    doctorID = SQLQueries.retrieveDoctorID(selectedDoctorSplit[0], selectedDoctorSplit[1], hospitalID);
                }
                selectedAppointmentDoctorIDText.setText(doctorID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectedAppointmentMedicationNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                medicationName = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        selectedPatientDeleteAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLQueries.removeSelectedAppointment(ID);
                finish();
            }
        });

        selectedAppointmentTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker(view);
            }
        });

        selectedAppointmentDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //-----------Retrieves data from database
        ID = String.valueOf(getIntent().getIntExtra("ID", 0));
        selectedAppointmentHeader.setText("Appointment " + ID);

        // Retrieves all data for the selected appointment
        selectedAppointmentCursor = SQLQueries.retrieveSelectedAppointmentData(ID);
        selectedAppointmentHeader.setText(String.valueOf(selectedAppointmentCursor.getCount()));
        selectedAppointmentCursor.moveToFirst();

        // Sets the values for the status spinner
        spinnerValues = new String[]{"Neither", "Completed", "Missed"};
        ArrayAdapter<String> completedValues;
        completedValues = new ArrayAdapter<>(this, R.layout.sectioned_spinner_design, spinnerValues);
        completedValues.setDropDownViewResource(R.layout.sectioned_spinner_items_design);
        selectedAppointmentStatusSpinner.setAdapter(completedValues);
        appointmentStatus = SQLQueries.retrieveSelectedAppointmentsCompletedStatus(ID);
        selectedAppointmentStatusSpinner.setSelection(Arrays.asList(spinnerValues).indexOf(appointmentStatus));

        // Retrieves all doctors for the selected hospital
        hospitalID = selectedAppointmentCursor.getString(16);
        doctorSpinnerValues = SQLQueries.retrieveAllDoctorsNamesForNewAppointmentPage(hospitalID);
        selectedAppointmentDoctorNameSpinner.setEnabled(true);
        selectedAppointmentDoctorNameSpinner.setClickable(true);

        //----------Updates the edittext widgets to the specific information
        patientID = selectedAppointmentCursor.getString(0);
        patientName = selectedAppointmentCursor.getString(1) + " " +  selectedAppointmentCursor.getString(2);

        if (selectedAppointmentCursor.getString(3).equals("Doctor")) {
            doctorName = "Dr ";
        }

        doctorID = selectedAppointmentCursor.getString(4);
        doctorName += selectedAppointmentCursor.getString(5) + " " + selectedAppointmentCursor.getString(6);

        hospitalName = selectedAppointmentCursor.getString(7);
        hospitalAddress = selectedAppointmentCursor.getString(8);
        date = selectedAppointmentCursor.getString(9);
        time = selectedAppointmentCursor.getString(10);

        prescriptionID = selectedAppointmentCursor.getString(11);
        medicationName = selectedAppointmentCursor.getString(12);
        medicationCost = "$" + selectedAppointmentCursor.getString(13);
        prescriptionDescription = selectedAppointmentCursor.getString(14);
        appointmentNotes = selectedAppointmentCursor.getString(15);
        medicationID = selectedAppointmentCursor.getString(17);

        // Requires String values for Patient name, doctor name, hospital name and address, date & time, prescription (including medication name, medication cost, prescription details), and an editable appointment notes EditText


        //------------Assigns the values to the appropriate EditText widgets
        selectedAppointmentHeader.setText("Appointment " + ID);
        selectedAppointmentTimeButton.setText(time);
        selectedAppointmentDateButton.setText(date);
        selectedAppointmentPatientIDText.setText(patientID);
        selectedAppointmentPatientNameText.setText(patientName);
        selectedAppointmentDoctorIDText.setText(doctorID);
        selectedAppointmentHospitalNameText.setText(hospitalName);
        selectedAppointmentHospitalAddressText.setText(hospitalAddress);
        selectedAppointmentMedicationCostText.setText(medicationCost);
        selectedAppointmentPrescriptionDescriptionText.setText(prescriptionDescription);
        selectedAppointmentAppointmentNotesText.setText(appointmentNotes);

        ArrayAdapter<String> doctorValues;
        doctorValues = new ArrayAdapter<>(this, R.layout.sectioned_spinner_design, doctorSpinnerValues);
        doctorValues.setDropDownViewResource(R.layout.sectioned_spinner_items_design);
        selectedAppointmentDoctorNameSpinner.setAdapter(doctorValues);
        selectedAppointmentDoctorNameSpinner.setSelection(Arrays.asList(doctorSpinnerValues).indexOf(doctorName));

        medicationSpinnerValues = SQLQueries.retrieveAllMedicationNames(false);
        ArrayAdapter<String> medicationValues;
        medicationValues = new ArrayAdapter<>(this, R.layout.sectioned_spinner_design, medicationSpinnerValues);
        medicationValues.setDropDownViewResource(R.layout.sectioned_spinner_items_design);
        selectedAppointmentMedicationNameSpinner.setAdapter(medicationValues);
        selectedAppointmentMedicationNameSpinner.setSelection(Arrays.asList(medicationSpinnerValues).indexOf(medicationName));
        // Still need to add setEnabled() code for each edittext and buttons like SelectedPatientPage

        appDetails = getSharedPreferences("appDetails", Context.MODE_PRIVATE);
        Boolean isAdmin = appDetails.getBoolean("adminRights", false);
        applyToWidgets(isAdmin);
    }


    // Applies the correct admin rights to the widgets
    private static void applyToWidgets(Boolean status) {
        selectedAppointmentPrescriptionDescriptionText.setEnabled(status);
        selectedAppointmentAppointmentNotesText.setEnabled(status);
        selectedAppointmentStatusSpinner.setEnabled(status);
        selectedAppointmentDoctorNameSpinner.setEnabled(status);
        selectedAppointmentMedicationNameSpinner.setEnabled(status);
        selectedAppointmentTimeButton.setEnabled(status);
        selectedAppointmentDateButton.setEnabled(status);
        selectedPatientDeleteAppointmentButton.setEnabled(status);
    }


    @Override
    protected void onPause() {
        SQLQueries.updateSelectedAppointment(ID);
        super.onPause();
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
                selectedAppointmentTimeButton.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

        // int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date1 = makeDateString(day, month, year);
                date1 = day + "/" + month + "/" + year;

                selectedAppointmentDateButton.setText(date1);
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

}