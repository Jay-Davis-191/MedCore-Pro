package com.example.myapplication;

import static com.example.myapplication.Database.appDatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class SQLQueries {


    private static final String DATABASE_NAME = "mentcare-db";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Passwords.1";
    private static final String URL = "jdbc:mysql://mentcare-db.c74mjnzto9oz.us-east-1.rds.amazonaws.com:3306/" + DATABASE_NAME;
    //+ "?useTimezone=true&serverTimezone=UTC" +
    //"&user=admin" +
    //"&password=Passwords.1" +
    //+ "?autoReconnect=true&useSSL=false";
    public static Connection myDbConn;

    private static final String TABLE_NAME = "Users";

    private static Cursor cursor;

    public static Cursor retrieveUserAccounts(String username) {
        cursor = appDatabase.rawQuery("SELECT * FROM USERS WHERE Username = '" + username + "'", null);
        return cursor;
    }


    public static Connection connectionClass() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:mysql://cp3407.mysql.database.azure.com:3306;databaseName=mydb;user=cp3407;password=Password.1;";
            connection = DriverManager.getConnection(ConnectionURL);

        } catch (ClassNotFoundException e) {
            Log.e("error here 1 : ", e.getMessage());
        } catch (SQLException e) {
            Log.e("error here 2 : ", e.getMessage());
        } catch (Exception e) {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }

    public static void testDatabase() throws SQLException {

        new Thread(() -> {
            StringBuilder records = new StringBuilder();

            try {
                //Class.forName("com.mysql.jdbc.Driver");
                Class.forName("net.sourceforge.jtds.jdbc.Driver");

                String url = "jdbc:mysql://cp3407.mysql.database.azure.com:3306/mydb?useSSL=true";
                //myDbConn = connectionClass();
                myDbConn = DriverManager.getConnection(url, "cp3407", "Password.1");
                Statement statement = myDbConn.createStatement();

                ResultSet rs = statement.executeQuery("SELECT * FROM APPOINTMENTS");
                while (rs.next()) {
                    //records.append("Name: ").append(rs.getString(1));
                    LoginPage.usernameEditText.setText("1");
                    LoginPage.passwordEditText.setText("ABC");
                }
                myDbConn.close();
            } catch (Exception e) {
                e.printStackTrace();
                LoginPage.usernameEditText.setText("TEST");
            }
        }).start();

    }


    // Retrieves all patients for the Patient table
    public static Cursor retrieveAllPatients() {
        String query = "SELECT PATIENTS.* FROM PATIENTS " +
                "CROSS JOIN APPOINTMENTS ON APPOINTMENTS.PatientID = PATIENTS.PatientID " +
                "CROSS JOIN PRESCRIPTIONS ON APPOINTMENTS.PrescriptionID = PRESCRIPTIONS.PrescriptionID " +
                "CROSS JOIN STAFF ON APPOINTMENTS.StaffID = STAFF.StaffID " +
                "CROSS JOIN MEDICATION ON PRESCRIPTIONS.MedicationID = MEDICATION.MedicationID";

        boolean whereUsed = false;

        // Applies the Search function
        // Allows the Search bar to accept spacing between two given String values
        String searchText = PatientsPage.patientsPageSearch.getText().toString().trim().toLowerCase();
        if (!searchText.equals("")) {
            if (searchText.split(" ").length == 2) {
                String[] searchTextSplit = searchText.split(" ");
                query += " WHERE (LOWER(PATIENTS.FirstName) LIKE '%" + searchTextSplit[0] + "%' OR LOWER(PATIENTS.LastName) LIKE '%" + searchTextSplit[1] + "%' OR LOWER(STAFF.FirstName) LIKE '%" + searchTextSplit[0] + "%' OR LOWER(STAFF.LastName) LIKE '%" + searchTextSplit[1] + "%')";
                whereUsed = true;
            } else if (searchText.split(" ").length == 1) {
                query += " WHERE (LOWER(PATIENTS.FirstName) LIKE '%" + searchText + "%' OR LOWER(PATIENTS.LastName) LIKE '%" + searchText + "%' OR LOWER(STAFF.FirstName) LIKE '%" + searchText + "%' OR LOWER(STAFF.LastName) LIKE '%" + searchText + "%')";
                whereUsed = true;
            }
        }

        // Applies the Medication filter if necessary
        String selectedMedication = PatientsPage.patientsProfilePatientMedicationFilter.getSelectedItem().toString();
        if (!selectedMedication.equals("")) {
            Cursor findMedication = appDatabase.rawQuery("SELECT PRESCRIPTIONS.PrescriptionID FROM PRESCRIPTIONS INNER JOIN MEDICATION ON PRESCRIPTIONS.MedicationID = MEDICATION.MedicationID WHERE MEDICATION.Name = '" + selectedMedication + "'", null);
            findMedication.moveToFirst();
            String prescriptionID = "";

            if (findMedication.getCount() > 0) {
                prescriptionID = findMedication.getString(0);
            } else {
                prescriptionID = "-1";
            }

            query += applyWhereUsed(whereUsed);

            query += " APPOINTMENTS.PrescriptionID = '" + prescriptionID + "'";
            whereUsed = true;
        }


        // Applies the Doctor filter if necessary
        String selectedDoctor = PatientsPage.patientsProfileDoctorFilter.getSelectedItem().toString();
        if (!selectedDoctor.equals("")) {
            String[] doctorNames = selectedDoctor.split(" ");

            Cursor findDoctor = null;
            if (doctorNames.length == 3) {
                findDoctor = appDatabase.rawQuery("SELECT StaffID FROM Staff WHERE FirstName = '" + doctorNames[1].trim() + "' AND LastName = '" + doctorNames[2].trim() + "'", null);
            } else if (doctorNames.length == 2) {
                findDoctor = appDatabase.rawQuery("SELECT StaffID FROM Staff WHERE FirstName = '" + doctorNames[0].trim() + "' AND LastName = '" + doctorNames[1].trim() + "'", null);
            }
            if (findDoctor.moveToFirst()) {
                findDoctor.moveToFirst();

                String doctorID = findDoctor.getString(0);
                query += applyWhereUsed(whereUsed);
                query += "STAFF.StaffID = '" + doctorID + "'";
                whereUsed = true;
            }
        }

        // Applies the Appointment Date filter if necessary
        String selectedAppointmentDate = PatientsPage.patientsProfileAppointmentFilter.getSelectedItem().toString();
        if (!selectedAppointmentDate.equals("")) {
            Cursor findAppointmentDate = appDatabase.rawQuery("SELECT AppointmentID FROM APPOINTMENTS WHERE Date = '" + selectedAppointmentDate + "'", null);
            int cursorSize = findAppointmentDate.getCount();
            findAppointmentDate.moveToFirst();

            query += applyWhereUsed(whereUsed);

            String appointmentID = findAppointmentDate.getString(0);
            query += "(";

            if (cursorSize == 1) {
                query += "APPOINTMENTS.AppointmentID = '" + appointmentID + "'";
            } else if (cursorSize > 1) {
                while (!findAppointmentDate.isAfterLast()) {
                    appointmentID = findAppointmentDate.getString(0);
                    if (findAppointmentDate.isLast()) {
                        query += "APPOINTMENTS.AppointmentID = '" + appointmentID + "'";
                    } else {
                        query += "APPOINTMENTS.AppointmentID = '" + appointmentID + "' OR ";
                    }
                    findAppointmentDate.moveToNext();
                }
            } else {
                appointmentID = "-1";
                query += "APPOINTMENTS.AppointmentID = '" + appointmentID + "'";
            }

            query += ")";
            whereUsed = true;
        }

        // Prevents replicate data rows from appearing
        query += " GROUP BY PATIENTS.PatientID";

        // Sorts the entries from A-Z or Z-A
        if (PatientsPage.patientsProfilePatientNameSort.getSelectedItem().toString().equals("A-Z")) {
            query += " ORDER BY PATIENTS.LastName ASC, PATIENTS.FirstName ASC";
        } else if (PatientsPage.patientsProfilePatientNameSort.getSelectedItem().toString().equals("Z-A")) {
            query += " ORDER BY PATIENTS.LastName DESC, PATIENTS.FirstName DESC";
        }

        cursor = appDatabase.rawQuery(query, null);
        return cursor;
    }


    // Checks if the variable whereUsed equals true, and then applies the correct String for the SQL Query string
    private static String applyWhereUsed(Boolean whereUsed) {
        if (whereUsed) {
            return " AND ";
        } else {
            return " WHERE ";
        }
    }


    // Retrieves all upcoming appointments for the selected patient
    public static Cursor retrieveNextAppointment(String patientID) {
        String query = "SELECT APPOINTMENTS.AppointmentID, APPOINTMENTS.Date, APPOINTMENTS.Time FROM PATIENTS INNER JOIN APPOINTMENTS ON APPOINTMENTS.PatientID = PATIENTS.PatientID WHERE PATIENTS.PatientID = '" + patientID + "' AND APPOINTMENTS.Date >= CAST(CURRENT_TIMESTAMP AS DATE) AND APPOINTMENTS.Completed = 'false' AND APPOINTMENTS.Missed = 'false' ORDER BY APPOINTMENTS.Date ASC, CAST(APPOINTMENTS.Time AS INT) ASC, APPOINTMENTS.Time ASC";
        cursor = appDatabase.rawQuery(query, null);
        return cursor;
    }


    // Retrieves all information from the Patient entity for the individual patient
    public static Cursor retrieveIndividualPatientData(String patientID) {
        cursor = appDatabase.rawQuery("SELECT * FROM PATIENTS WHERE PatientID = '" + patientID + "'", null);
        return cursor;
    }


    // Retrieves all information from the Patient entity for the individual patient
    public static Cursor retrieveIndividualStaffData(String staffID) {
        cursor = appDatabase.rawQuery("SELECT * FROM STAFF WHERE StaffID = '" + staffID + "'", null);
        return cursor;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////
    // Retrieves the name of the sectional hospital the patient is sectioned in
    public static Cursor retrieveSectionedHospital(String patientID) {
        cursor = appDatabase.rawQuery("SELECT HospitalID FROM APPOINTMENTS WHERE PatientID = '" + patientID + "' ORDER BY AppointmentID DESC", null);
        cursor.moveToFirst();
        String hospitalID = cursor.getString(0);
        cursor = appDatabase.rawQuery("SELECT Name FROM HOSPITALS WHERE HospitalID = '" + hospitalID + "'", null);
        return cursor;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////



    // Retrieves the 2 next appointments for the selected patient, or 1 depending on how many upcoming appointments the patient has
    public static String[] retrieveIndividualPatientsNextAppointment(String patientID, int number) {
        String[] nextAppointmentDetails = new String[7];
        cursor = retrieveNextAppointment(patientID);

        if (number == 0) {
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                nextAppointmentDetails = obtainIndividualPatientData(nextAppointmentDetails);
            }
        } else if (number == 1) {
            cursor.moveToNext();
            if (cursor != null && cursor.moveToNext()) {
                nextAppointmentDetails = obtainIndividualPatientData(nextAppointmentDetails);
            }
        }

        return nextAppointmentDetails;
    }


    // Retrieves the individual patient's data. Used in retrieveIndividualPatientsNextAppointment()
    private static String[] obtainIndividualPatientData(String[] list) {
        list[0] = cursor.getString(0);
        list[1] = cursor.getString(1);
        list[2] = cursor.getString(2);

        cursor = appDatabase.rawQuery("SELECT STAFF.StaffID, STAFF.FirstName, STAFF.LastName FROM STAFF INNER JOIN APPOINTMENTS ON STAFF.StaffID = APPOINTMENTS.StaffID WHERE APPOINTMENTS.AppointmentID = '" + list[0] + "'", null);
        cursor.moveToFirst();
        list[3] = cursor.getString(0);
        list[4] = cursor.getString(1);
        list[5] = cursor.getString(2);

        cursor = appDatabase.rawQuery("SELECT HOSPITALS.Name FROM HOSPITALS INNER JOIN APPOINTMENTS ON HOSPITALS.HospitalID = APPOINTMENTS.HospitalID WHERE APPOINTMENTS.AppointmentID = '" + list[0] + "'", null);
        cursor.moveToFirst();
        list[6] = cursor.getString(0);
        return list;
    }


    // Retrieves the names of all medication.
    // Used as a PatientPage filter
    public static String[] retrieveAllMedicationNames(boolean requiresEmptyFirstString) {
        cursor = appDatabase.rawQuery("SELECT Name FROM MEDICATION WHERE Name != 'TBC' ORDER BY Name", null);
        String[] allMedicationNames = new String[]{""};
        if (cursor.moveToFirst()) {
            if (requiresEmptyFirstString) {
                allMedicationNames = new String[cursor.getCount() + 2];
                allMedicationNames[0] = "";
                allMedicationNames[1] = "TBC";
                int count = 2;
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    allMedicationNames[count] = cursor.getString(0);
                    count++;
                    cursor.moveToNext();
                }
            } else {
                allMedicationNames = new String[cursor.getCount() + 1];
                allMedicationNames[0] = "TBC";
                int count = 1;
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    allMedicationNames[count] = cursor.getString(0);
                    count++;
                    cursor.moveToNext();
                }
            }
        }
        return allMedicationNames;
    }


    // Retrieves the names of all doctors
    // Used as a PatientPage filter
    public static String[] retrieveAllStaff() {
        cursor = appDatabase.rawQuery("SELECT FirstName, LastName, Occupation FROM STAFF WHERE Occupation = 'Doctor' OR Occupation = 'Nurse' ORDER BY Occupation, LastName, FirstName", null);
        String[] allDoctorsNames = new String[cursor.getCount() + 1];
        int count = 0;
        allDoctorsNames[0] = "";
        count++;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(2).equals("Doctor")) {
                allDoctorsNames[count] = "Dr " + cursor.getString(0) + " " + cursor.getString(1);
            } else {
                allDoctorsNames[count] = cursor.getString(0) + " " + cursor.getString(1);
            }
            count++;
            cursor.moveToNext();
        }

        return allDoctorsNames;
    }


    // Retrieves the dates of all upcoming appointments
    // Used as a PatientPage filter
    public static String[] retrieveAllUpcomingAppointmentDates() {
        cursor = appDatabase.rawQuery("SELECT Date FROM APPOINTMENTS WHERE (APPOINTMENTS.Completed = 'false' AND APPOINTMENTS.Missed = 'false') GROUP BY Date", null);
        //cursor = appDatabase.rawQuery("SELECT Date FROM APPOINTMENTS WHERE APPOINTMENTS.Date >= CAST(CURRENT_TIMESTAMP AS DATE) GROUP BY Date", null);
        String[] allUpcomingAppointmentDates = new String[cursor.getCount() + 1];
        allUpcomingAppointmentDates[0] = "";
        int count = 1;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            allUpcomingAppointmentDates[count] = cursor.getString(0);
            count++;
            cursor.moveToNext();
        }

        return allUpcomingAppointmentDates;
    }


    // Retrieves the names of all patients
    // Used as an AppointmentsPage filter
    public static String[] retrieveAllPatientNames() {
        cursor = appDatabase.rawQuery("SELECT PATIENTS.FirstName, PATIENTS.LastName FROM PATIENTS INNER JOIN APPOINTMENTS ON PATIENTS.PatientID = APPOINTMENTS.PatientID GROUP BY PATIENTS.PatientID ORDER BY PATIENTS.LastName ASC, PATIENTS.FirstName ASC", null);
        String[] allPatientsNames = new String[cursor.getCount() + 1];
        allPatientsNames[0] = "";
        int count = 1;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            allPatientsNames[count] = cursor.getString(0) + " " + cursor.getString(1);
            count++;
            cursor.moveToNext();
        }
        return allPatientsNames;
    }


    public static String retrieveIndividualPatientMedication(String patientID) {
        String currentMedication = "";
        cursor = appDatabase.rawQuery("SELECT MEDICATION.Name FROM MEDICATION " +
                "INNER JOIN PRESCRIPTIONS ON MEDICATION.MedicationID = PRESCRIPTIONS.MedicationID " +
                "INNER JOIN APPOINTMENTS ON PRESCRIPTIONS.PrescriptionID = APPOINTMENTS.PrescriptionID WHERE APPOINTMENTS.PatientID = '" + patientID + "' AND APPOINTMENTS.Date >= CAST(CURRENT_TIMESTAMP AS DATE) GROUP BY MEDICATION.Name ORDER BY APPOINTMENTS.Date ASC, APPOINTMENTS.Time ASC LIMIT 2 ", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.isLast()) {
                currentMedication += cursor.getString(0);
            } else {
                currentMedication += cursor.getString(0) + ", ";
            }
            cursor.moveToNext();
        }
        return currentMedication;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Cursor retrieveAllAppointments() {
        String query = "SELECT APPOINTMENTS.AppointmentID, APPOINTMENTS.Date, APPOINTMENTS.Time, PATIENTS.FirstName, PATIENTS.LastName, STAFF.FirstName, STAFF.LastName, MEDICATION.Name, APPOINTMENTS.Description, STAFF.Occupation " +
                "FROM APPOINTMENTS " +
                "INNER JOIN PATIENTS ON PATIENTS.PatientID = APPOINTMENTS.PatientID " +
                "INNER JOIN STAFF ON STAFF.StaffID = APPOINTMENTS.StaffID " +
                "INNER JOIN PRESCRIPTIONS ON PRESCRIPTIONS.PrescriptionID = APPOINTMENTS.PrescriptionID " +
                "INNER JOIN MEDICATION ON PRESCRIPTIONS.MedicationID = MEDICATION.MedicationID ";

        // Applies the Unmarked filter if necessary
        // Used as a filter for the Appointments Page
        String selectedStatus = AppointmentsPage.appointmentsPreviousFilter.getSelectedItem().toString();
        if (!selectedStatus.equals("")) {
            query += "WHERE (APPOINTMENTS.Completed = 'true' OR APPOINTMENTS.Missed = 'true') AND substr(APPOINTMENTS.Date, -4, 4) != '2000' ";
        } else {
            query += "WHERE (APPOINTMENTS.Completed = 'false' AND APPOINTMENTS.Missed = 'false') ";
        }

        boolean whereUsed = true;

        // Applies the Search function
        // Allows the Search bar to accept spacing between two given String values
        String searchText = AppointmentsPage.appointmentsPageSearch.getText().toString().trim().toLowerCase();
        if (!searchText.equals("")) {
            if (searchText.split(" ").length == 2) {
                String[] searchTextSplit = searchText.split(" ");
                query += "AND (LOWER(PATIENTS.FirstName) LIKE '%" + searchTextSplit[0] + "%' OR LOWER(PATIENTS.LastName) LIKE '%" + searchTextSplit[1] + "%' OR LOWER(STAFF.FirstName) LIKE '%" + searchTextSplit[0] + "%' OR LOWER(STAFF.LastName) LIKE '%" + searchTextSplit[1] + "%')";
            } else if (searchText.split(" ").length == 1) {
                query += "AND (LOWER(PATIENTS.FirstName) LIKE '%" + searchText + "%' OR LOWER(PATIENTS.LastName) LIKE '%" + searchText + "%' OR LOWER(STAFF.FirstName) LIKE '%" + searchText + "%' OR LOWER(STAFF.LastName) LIKE '%" + searchText + "%')";
            }
        }

        // Applies the Patient filter if necessary
        // Used as a filter for the Appointments Page
        String selectedPatient = AppointmentsPage.appointmentsPatientNameSort.getSelectedItem().toString().trim();
        if (!selectedPatient.equals("")) {
            String[] selectedPatientSplit = selectedPatient.split(" ");
            Cursor findPatient = appDatabase.rawQuery("SELECT PatientID FROM PATIENTS WHERE FirstName = '" + selectedPatientSplit[0] + "' AND LastName = '" + selectedPatientSplit[1] + "'", null);

            findPatient.moveToFirst();
            String patientID = findPatient.getString(0);
            query += applyWhereUsed(whereUsed);
            query += "APPOINTMENTS.PatientID = '" + patientID + "'";

        }


        // Applies the Doctor filter if necessary
        // Used as a filter for the Appointments Page
        String selectedDoctor = AppointmentsPage.appointmentsDoctorFilter.getSelectedItem().toString();
        if (!selectedDoctor.equals("")) {
            String[] doctorNames = selectedDoctor.split(" ");
            Cursor findDoctor = null;
            if (doctorNames.length == 3) {
                findDoctor = appDatabase.rawQuery("SELECT StaffID FROM Staff WHERE FirstName = '" + doctorNames[1].trim() + "' AND LastName = '" + doctorNames[2].trim() + "'", null);
            } else if (doctorNames.length == 2) {
                findDoctor = appDatabase.rawQuery("SELECT StaffID FROM Staff WHERE FirstName = '" + doctorNames[0].trim() + "' AND LastName = '" + doctorNames[1].trim() + "'", null);
            }
            if (findDoctor.moveToFirst()) {
                findDoctor.moveToFirst();

                String doctorID = findDoctor.getString(0);
                query += applyWhereUsed(whereUsed);
                query += "STAFF.StaffID = '" + doctorID + "'";
            }

        }


        // Applies the Date filter if necessary
        // Used as a filter for the Appointments Page
        String selectedDate = AppointmentsPage.appointmentsDateFilter.getSelectedItem().toString();
        if (!selectedDate.equals("")) {
            query += applyWhereUsed(whereUsed);
            query += "APPOINTMENTS.Date = '" + selectedDate + "'";
        }

        query += " ORDER BY substr(APPOINTMENTS.Date, -4, 4) ASC, substr(APPOINTMENTS.Date, -7, 2) ASC, substr(APPOINTMENTS.Date, -10, 2) ASC, APPOINTMENTS.Time ASC";

        /////--------------------------------------------------------------------------------------------------------
        cursor = appDatabase.rawQuery(query, null);
        return cursor;
    }


    // Retrieves all data necessary for a selected appointment
    public static Cursor retrieveSelectedAppointmentData(String appointmentID) {
        String query = "SELECT PATIENTS.PatientID, PATIENTS.FirstName, PATIENTS.LastName, STAFF.Occupation, STAFF.StaffID, STAFF.FirstName, STAFF.LastName, HOSPITALS.Name, HOSPITALS.Address, APPOINTMENTS.Date, APPOINTMENTS.Time, PRESCRIPTIONS.PrescriptionID, MEDICATION.Name, MEDICATION.Cost, PRESCRIPTIONS.Description, APPOINTMENTS.Description, APPOINTMENTS.HospitalID, MEDICATION.MedicationID " +
                "FROM APPOINTMENTS " +
                "INNER JOIN PATIENTS ON APPOINTMENTS.PatientID = PATIENTS.PatientID " +
                "INNER JOIN STAFF ON APPOINTMENTS.StaffID = STAFF.StaffID " +
                "INNER JOIN HOSPITALS ON APPOINTMENTS.HospitalID = HOSPITALS.HospitalID " +
                "INNER JOIN PRESCRIPTIONS ON APPOINTMENTS.PrescriptionID = PRESCRIPTIONS.PrescriptionID " +
                "INNER JOIN MEDICATION ON PRESCRIPTIONS.MedicationID = MEDICATION.MedicationID " +
                "WHERE APPOINTMENTS.AppointmentID = '" + appointmentID + "'";
        cursor = appDatabase.rawQuery(query, null);
        return cursor;
    }


    // Retrieves the sectioned status of the selected patient
    public static String retrieveSelectedPatientsSectionedStatus(String patientID) {
        cursor = appDatabase.rawQuery("SELECT Sectioned, HomeCare FROM PATIENTS WHERE PatientID = '" + patientID + "'", null);
        cursor.moveToFirst();
        String sectionedStatus = cursor.getString(0);
        String homeCareStatus = cursor.getString(1);
        String currentStatus = "";

        if (sectionedStatus.equals("Yes")) {
            currentStatus = "Sectioned";
        } else if (homeCareStatus.equals("Yes")) {
            currentStatus = "HomeCared";
        } else {
            currentStatus = "Neither";
        }

        return currentStatus;

    }


    // Updates the sectioned status for the selected patient when changed
    public static void updateSectionedStatusForSelectedPatient(String sectionedStatus, String patientID) {
        String sectionedUpdate;

        if (sectionedStatus.equals("HomeCared")) {
            sectionedUpdate = "UPDATE PATIENTS SET Sectioned = 'No', HomeCare = 'Yes' WHERE PatientID = '" + patientID + "';";
        } else if (sectionedStatus.equals("Sectioned")) {
            sectionedUpdate = "UPDATE PATIENTS SET Sectioned = 'Yes', HomeCare = 'No' WHERE PatientID = '" + patientID + "';";
        } else {
            sectionedUpdate = "UPDATE PATIENTS SET Sectioned = 'No', HomeCare = 'No' WHERE PatientID = '" + patientID + "';";
        }
        appDatabase.execSQL(sectionedUpdate);
    }


    // Updates all data for the selected patient after closing their profile
    public static void updateSelectedPatientsData(String appointmentStatus, String appointmentID) {
        String patientsNewConditions = SelectedPatientPage.adminPatientConditionsText.getText().toString();
        String patientsNewNextOfKin = SelectedPatientPage.adminPatientNextOfKinText.getText().toString();

        String query = "UPDATE PATIENTS SET Notes = '" + patientsNewConditions + "', NextOfKin = '" + patientsNewNextOfKin + "'";
        int count = 0;

        if (!appointmentStatus.equals("Sectioned")) {
            String patientAddress = SelectedPatientPage.adminPatientAddressText.getText().toString().trim();

            if (patientAddress.contains("Residential") || patientAddress.contains("Address")) {
                patientAddress = patientAddress.replaceAll("Residential", "");
                patientAddress = patientAddress.replaceAll("Address", "");
            }

            if (patientAddress.length() != 0) {
                // Checks if the patient address textfield contains the required colon character
                for (int i = 0; i < patientAddress.length(); i++) {
                    if (patientAddress.charAt(i) == ':') {
                        count++;
                    }
                }

                if (count == 0) {

                    patientAddress = ": " + patientAddress;
                    count++;
                }

                if (count == 1) {
                    String[] patientsNewAddressSplit = patientAddress.split(": ");
                    String patientsNewAddress = patientsNewAddressSplit[1];
                    query += ", HomeAddress = '" + patientsNewAddress + "'";
                }
            }
        }

        query += " WHERE PatientID = '" + appointmentID + "';";
        appDatabase.execSQL(query);
    }


    // Retrieves the names of all doctors
    public static String[] retrieveAllDoctorsNamesForNewAppointmentPage(String hospitalID) {
        cursor = appDatabase.rawQuery("SELECT FirstName, LastName, Occupation FROM STAFF WHERE (Occupation = 'Doctor' OR Occupation = 'Nurse') AND HospitalID = '" + hospitalID + "' ORDER BY Occupation ASC, LastName ASC, FirstName ASC", null);
        String[] allDoctorsNames = new String[cursor.getCount()];
        int count = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(2).equals("Doctor")) {
                allDoctorsNames[count] = "Dr " + cursor.getString(0) + " " + cursor.getString(1);
            } else {
                allDoctorsNames[count] = cursor.getString(0) + " " + cursor.getString(1);
            }
            count++;
            cursor.moveToNext();
        }
        return allDoctorsNames;
    }


    public static int findSpecificDoctorForNewAppointmentPage(int index, String hospitalID) {
        cursor = appDatabase.rawQuery("SELECT StaffID FROM STAFF WHERE (Occupation = 'Doctor' OR Occupation = 'Nurse') AND HospitalID = '" + hospitalID + "' ORDER BY LastName ASC, LastName ASC LIMIT " + index + ", 1", null);
        cursor.moveToFirst();
        return Integer.parseInt(cursor.getString(0));
    }


    // Retrieves the name of the sectional hospital the patient is sectioned in
    public static Cursor retrieveSectionedHospitalForNewAppointmentPage(String patientID) {
        cursor = appDatabase.rawQuery("SELECT HospitalID FROM APPOINTMENTS WHERE PatientID = '" + patientID + "' ORDER BY Date DESC", null);
        cursor.moveToFirst();
        String hospitalID = cursor.getString(0);
        cursor = appDatabase.rawQuery("SELECT HOSPITALS.Name, HOSPITALS.Address, HOSPITALS.HospitalID FROM HOSPITALS " +
                "INNER JOIN APPOINTMENTS ON HOSPITALS.HospitalID = APPOINTMENTS.HospitalID " +
                "WHERE HOSPITALS.HospitalID = '" + hospitalID + "' ORDER BY APPOINTMENTS.Date DESC", null);
        return cursor;
    }


    // Retrieves the names of all hospitals
    public static String[] retrieveAllHospitalsNamesForNewAppointmentPage(boolean requiresEmptyFirstString) {
        cursor = appDatabase.rawQuery("SELECT Name, Address, HospitalID FROM HOSPITALS ORDER BY Name", null);
        String[] hospitalNames = new String[]{};
        if (cursor.moveToFirst()) {
            if (requiresEmptyFirstString) {
                cursor.moveToFirst();
                hospitalNames = new String[cursor.getCount() + 1];
                hospitalNames[0] = "";
                int count = 1;
                while (!cursor.isAfterLast()) {
                    hospitalNames[count] = cursor.getString(0) + ": " + cursor.getString(1);
                    count++;
                    cursor.moveToNext();
                }
            } else {
                cursor.moveToFirst();
                hospitalNames = new String[cursor.getCount()];
                int count = 0;
                while (!cursor.isAfterLast()) {
                    hospitalNames[count] = cursor.getString(0) + ": " + cursor.getString(1);
                    count++;
                    cursor.moveToNext();
                }
            }
        }
        return hospitalNames;
    }


    // Retrieves the names of all hospitals
    public static Cursor retrieveAllHospitalsNamesForNewAppointmentPage2() {
        cursor = appDatabase.rawQuery("SELECT Name, Address, HospitalID FROM HOSPITALS ORDER BY Name", null);
        return cursor;
    }


    public static void addAppointment(int patientID, String hospitalID, int doctorID, String date, String time) {
        cursor = appDatabase.rawQuery("SELECT AppointmentID FROM APPOINTMENTS ORDER BY AppointmentID DESC LIMIT 1", null);
        cursor.moveToFirst();
        int numberOfAppointments = Integer.parseInt(cursor.getString(0)) + 1;
        appDatabase.execSQL("INSERT INTO APPOINTMENTS VALUES('" + numberOfAppointments + "', '" + time + "', '" + date + "', '...', 'false', 'false', '" + Integer.parseInt(hospitalID) + "', '" + patientID + "', '" + doctorID + "', 0)");
    }


    // Deletes the selected appointment from the Appointment entity
    public static void removeSelectedAppointment(String appointmentID) {
        appDatabase.execSQL("DELETE FROM APPOINTMENTS WHERE AppointmentID = '" + appointmentID + "'");
    }


    // Updates the sectioned status for the selected patient when changed
    public static void updateSelectedAppointment(String appointmentID) {
        String query, completedStatus, missedStatus, date, time, medicationName, doctorID, prescriptionDescription, appointmentNotes, appointmentStatus, prescriptionID;
        completedStatus = "";
        missedStatus = "";

        date = SelectedAppointmentPage.selectedAppointmentDateButton.getText().toString();
        time = SelectedAppointmentPage.selectedAppointmentTimeButton.getText().toString();
        medicationName = SelectedAppointmentPage.medicationName;
        doctorID = SelectedAppointmentPage.doctorID;
        prescriptionDescription = SelectedAppointmentPage.selectedAppointmentPrescriptionDescriptionText.getText().toString();
        appointmentNotes = SelectedAppointmentPage.selectedAppointmentAppointmentNotesText.getText().toString();
        appointmentStatus = SelectedAppointmentPage.appointmentStatus;
        prescriptionID = SelectedAppointmentPage.prescriptionID;

        query = "SELECT PatientID FROM APPOINTMENTS WHERE AppointmentID = '" + appointmentID + "'";
        cursor = appDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            String patientID = cursor.getString(0);

            cursor = appDatabase.rawQuery("SELECT MedicationID FROM Medication WHERE Name = '" + medicationName + "'", null);
            cursor.moveToFirst();
            String medicationID = cursor.getString(0);

            // Completed status, missed status, time, date, doctor name, medication, prescription description, appointment notes

            if (appointmentStatus.equals("Completed")) {
                completedStatus = "true";
                missedStatus = "false";
                query = "UPDATE PATIENTS SET TotalAppointments = TotalAppointments + 1 WHERE PatientID = '" + patientID + "';";
                appDatabase.execSQL(query);
            } else if (appointmentStatus.equals("Missed")) {
                completedStatus = "false";
                missedStatus = "true";
                query = "UPDATE PATIENTS SET MissedAppointments = MissedAppointments + 1, TotalAppointments = TotalAppointments + 1 WHERE PatientID = '" + patientID + "';";
                appDatabase.execSQL(query);
            } else {
                completedStatus = "false";
                missedStatus = "false";
            }

            if (prescriptionID.equals("0") && !medicationName.equals("TBC")) {
                cursor = appDatabase.rawQuery("SELECT PrescriptionID FROM PRESCRIPTIONS ORDER BY PrescriptionID DESC LIMIT 1", null);
                cursor.moveToFirst();
                int numberOfPrescriptions = Integer.parseInt(cursor.getString(0)) + 1;
                appDatabase.execSQL("INSERT INTO PRESCRIPTIONS VALUES('" + numberOfPrescriptions + "', '...', '" + medicationID + "')");
                prescriptionID = String.valueOf(numberOfPrescriptions);
                query = "UPDATE APPOINTMENTS SET PrescriptionID = '" + prescriptionID + "' WHERE AppointmentID = '" + appointmentID + "';";
                appDatabase.execSQL(query);
            }

            query = "UPDATE PRESCRIPTIONS SET Description = '" + prescriptionDescription + "', MedicationID = '" + medicationID + "' WHERE PrescriptionID = '" + prescriptionID + "';";
            appDatabase.execSQL(query);

            query = "UPDATE APPOINTMENTS SET Time = '" + time + "', Date = '" + date + "', StaffID = '" + doctorID + "', Completed = '" + completedStatus + "', Missed = '" + missedStatus + "', Description = '" + appointmentNotes + "', PrescriptionID = '" + prescriptionID + "' WHERE AppointmentID = '" + appointmentID + "';";
            appDatabase.execSQL(query);
        }

        // Time, Date, Description, Completed, Missed, HospitalID, PatientID, StaffID, PrescriptionID

        //prescriptionDescription = SelectedAppointmentPage.prescriptionDescription;
        //prescriptionID = SelectedAppointmentPage.prescriptionID;

    }


    // Retrieves the sectioned status of the selected patient
    public static String retrieveSelectedAppointmentsCompletedStatus(String appointmentID) {
        cursor = appDatabase.rawQuery("SELECT Completed, Missed FROM APPOINTMENTS WHERE AppointmentID = '" + appointmentID + "'", null);
        cursor.moveToFirst();
        String completedStatus = cursor.getString(0);
        String missedStatus = cursor.getString(1);
        String currentStatus = "";

        if (completedStatus.equals("true")) {
            currentStatus = "Completed";
        } else if (missedStatus.equals("true")) {
            currentStatus = "Missed";
        } else {
            currentStatus = "Neither";
        }

        return currentStatus;
    }


    // Retrieves the names of all hospitals
    public static String retrieveDoctorID(String doctorFirstName, String doctorLastName, String hospitalID) {
        String doctorID = "N/A";
        cursor = appDatabase.rawQuery("SELECT STAFF.StaffID FROM STAFF " +
                "INNER JOIN APPOINTMENTS ON APPOINTMENTS.StaffID = STAFF.StaffID " +
                "WHERE STAFF.FirstName = '" + doctorFirstName + "' AND STAFF.LastName = '" + doctorLastName + "' AND APPOINTMENTS.HospitalID = '" + hospitalID + "'", null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            doctorID = cursor.getString(0);
        }
        return doctorID;
    }


    public static int addPatient() {
        String name, address, nextOfKin, notes, dob, firstName, lastName;
        String nameSplit[];

        cursor = appDatabase.rawQuery("SELECT * FROM PATIENTS ORDER BY PatientID DESC LIMIT 1", null);
        cursor.moveToFirst();
        int numberOfPatients = Integer.parseInt(cursor.getString(0)) + 1;

        name = AddPatient.newPatientName.getText().toString();
        address = AddPatient.newPatientAddress.getText().toString();
        nextOfKin = AddPatient.newPatientNextOfKin.getText().toString();
        notes = AddPatient.newPatientNotes.getText().toString();
        dob = AddPatient.DOB;

        nameSplit = name.split(" ");
        firstName = nameSplit[0];
        lastName = nameSplit[1];

        // PatientID, FirstName, LastName, Sectioned, HomeCare, Notes, DateOfBirth, HomeAddress, TotalAppointments, MissedAppointments, NextOfKin

        appDatabase.execSQL("INSERT INTO PATIENTS VALUES('" + numberOfPatients + "', '" + firstName + "', '" + lastName + "', 'No', 'No', '" + notes + "', '" + dob + "', '" + address + "', '0', '0', '" + nextOfKin + "')");
        return numberOfPatients;
    }


    public static Cursor getPatientsForNewAppointment() {
        cursor = appDatabase.rawQuery("SELECT PATIENTS.PatientID, PATIENTS.FirstName, PATIENTS.LastName, PATIENTS.HomeAddress FROM PATIENTS INNER JOIN APPOINTMENTS ON PATIENTS.PatientID = APPOINTMENTS.AppointmentID ORDER BY PATIENTS.LastName, PATIENTS.FirstName", null);
        return cursor;
    }

    public static String[] retrieveAllOccupations() {
        String[] occupations = new String[]{};
        cursor = appDatabase.rawQuery("SELECT Occupation FROM STAFF GROUP BY Occupation ORDER BY Occupation", null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            occupations = new String[cursor.getCount() + 1];
            occupations[0] = "";
            int count = 1;
            while (!cursor.isAfterLast()) {
                occupations[count] = cursor.getString(0);
                count++;
                cursor.moveToNext();
            }
        }
        return occupations;
    }


    public static Cursor retrieveAllStaffForStaffPage() {
        String query = "SELECT STAFF.StaffID, STAFF.FirstName, STAFF.LastName, STAFF.Occupation, STAFF.HospitalID, HOSPITALS.Name, HOSPITALS.Address FROM STAFF " +
                "INNER JOIN HOSPITALS ON STAFF.HospitalID = HOSPITALS.HospitalID";

        boolean whereUsed = false;
        // Applies the Search function
        // Allows the Search bar to accept spacing between two given String values
        String searchText = StaffPage.staffPageSearch.getText().toString().trim().toLowerCase();
        if (!searchText.equals("")) {
            if (searchText.split(" ").length == 2) {
                String[] searchTextSplit = searchText.split(" ");
                query += " WHERE (LOWER(STAFF.FirstName) LIKE '%" + searchTextSplit[0] + "%' OR LOWER(STAFF.LastName) LIKE '%" + searchTextSplit[1] + "%')";
                whereUsed = true;
            } else if (searchText.split(" ").length == 1) {
                query += " WHERE (LOWER(STAFF.FirstName) LIKE '%" + searchText + "%' OR LOWER(STAFF.LastName) LIKE '%" + searchText + "%')";
                whereUsed = true;
            }
        }
        // Applies the Hospital filter if necessary
        String selectedHospital = StaffPage.staffProfileHospitalFilter.getSelectedItem().toString();
        String[] selectedHospitalSplit = selectedHospital.split(":");
        String selectedHospitalName = selectedHospitalSplit[0];

        if (!selectedHospital.equals("")) {
            Cursor findHospital = appDatabase.rawQuery("SELECT HospitalID FROM HOSPITALS WHERE Name = '" + selectedHospitalName + "'", null);
            findHospital.moveToFirst();
            String hospitalID = "";
            if (findHospital.getCount() > 0) {
                hospitalID = findHospital.getString(0);
            } else {
                hospitalID = "-1";
            }
            query += applyWhereUsed(whereUsed);
            query += " HOSPITALS.HospitalID = '" + hospitalID + "'";
            whereUsed = true;
        }
        // Applies the Occupation filter if necessary
        String selectedOccupation = StaffPage.staffProfileOccupationFilter.getSelectedItem().toString();
        if (!selectedOccupation.equals("")) {
            query += applyWhereUsed(whereUsed);
            query += "STAFF.Occupation = '" + selectedOccupation + "'";
            whereUsed = true;
        }

        // Prevents replicate data rows from appearing
        query += " GROUP BY STAFF.StaffID";
        // Sorts the entries from A-Z or Z-A
        if (StaffPage.staffProfileStaffNameSort.getSelectedItem().toString().equals("A-Z")) {
            query += " ORDER BY STAFF.LastName ASC, STAFF.FirstName ASC";
        } else if (StaffPage.staffProfileStaffNameSort.getSelectedItem().toString().equals("Z-A")) {
            query += " ORDER BY STAFF.LastName DESC, STAFF.FirstName DESC";
        }
        cursor = appDatabase.rawQuery(query, null);
        return cursor;
    }


    public static String retrieveSelectedHospital(String staffID) {
        cursor = appDatabase.rawQuery("SELECT HospitalID FROM STAFF WHERE StaffID = '" + staffID + "'", null);
        String hospitalID = "";
        String selectedHospital = "";
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            hospitalID = cursor.getString(0);
            cursor = appDatabase.rawQuery("SELECT Name, Address FROM HOSPITALS WHERE HospitalID = '" + hospitalID + "'", null);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                selectedHospital = cursor.getString(0) + ": " + cursor.getString(1);
            }
        }
        return selectedHospital;
    }


    // "ID", "Patient", "Appointment Date & Time", "Location"};
    public static Cursor retrieveIndividualStaffDataForSelectedStaffPage(String staffID) {
        cursor = appDatabase.rawQuery("SELECT * FROM STAFF WHERE StaffID = '" + staffID + "'", null);
        return cursor;
    }


    public static void updateStaffProfile(String staffID) {
        String staffOccupation = SelectedStaffPage.selectedStaffOccupationSpinner.getSelectedItem().toString();
        String staffSalary = SelectedStaffPage.selectedStaffSalaryText.getText().toString().replace(",", "");
        String staffHospital = SelectedStaffPage.selectedStaffHospitalSpinner.getSelectedItem().toString();
        String[] staffHospitalSplit = staffHospital.split(": ");
        cursor = appDatabase.rawQuery("SELECT HospitalID FROM HOSPITALS WHERE Name = '" + staffHospitalSplit[0] + "'", null);
        cursor.moveToFirst();
        String selectedHospital = cursor.getString(0);
        String query = "UPDATE STAFF SET Occupation = '" + staffOccupation + "', Salary = '" + staffSalary + "', HospitalID = '" + selectedHospital + "' WHERE StaffID = '" + staffID + "'";
        appDatabase.execSQL(query);
    }


    public static void addStaff() {
        String firstName, lastName, name, occupation, salary, hospital;
        String nameSplit[];

        // StaffID, FirstName, LastName, Occupation, Salary, HospitalID
        cursor = appDatabase.rawQuery("SELECT * FROM STAFF ORDER BY StaffID DESC LIMIT 1", null);
        cursor.moveToFirst();
        int numberOfStaff = Integer.parseInt(cursor.getString(0)) + 1;

        name = AddStaffPopUp.newStaffNameText.getText().toString();
        nameSplit = name.split(" ");
        firstName = nameSplit[0];
        lastName = nameSplit[1];
        occupation = AddStaffPopUp.newStaffOccupationSpinner.getSelectedItem().toString();
        salary = AddStaffPopUp.newStaffSalaryText.getText().toString();
        hospital = AddStaffPopUp.newStaffHospitalSpinner.getSelectedItem().toString();
        String[] hospitalSplit = hospital.split(": ");
        cursor = appDatabase.rawQuery("SELECT HospitalID FROM HOSPITALS WHERE Name = '" + hospitalSplit[0] + "'", null);
        cursor.moveToFirst();
        String selectedHospital = cursor.getString(0);
        // PatientID, FirstName, LastName, Sectioned, HomeCare, Notes, DateOfBirth, HomeAddress, TotalAppointments, MissedAppointments, NextOfKin
        appDatabase.execSQL("INSERT INTO STAFF VALUES('" + numberOfStaff + "', '" + firstName + "', '" + lastName + "', '" + occupation + "', '" + salary + "', '" + selectedHospital + "')");
    }


    public static Cursor retrieveAllMedication() {
        //String query = "SELECT MEDICATION.* FROM MEDICATION " +
        //        "CROSS JOIN PRESCRIPTIONS ON MEDICATION.MedicationID = PRESCRIPTIONS.MedicationID " +
        //        "CROSS JOIN APPOINTMENTS ON APPOINTMENTS.PrescriptionID = PRESCRIPTIONS.PrescriptionID " +
        //        "WHERE MEDICATION.Name != 'TBC'";

        String query = "SELECT * FROM MEDICATION WHERE Name != 'TBC'";
        boolean whereUsed = true;


        String searchText = MedicationPage.medicationPageSearch.getText().toString().trim().toLowerCase();
        if (!searchText.equals("")) {

            query += " AND (LOWER(MEDICATION.Name) LIKE '%" + searchText + "%' OR LOWER(MEDICATION.Description) LIKE '%" + searchText + "%')";
            whereUsed = true;
        }

        // Prevents replicate data rows from appearing
        query += " GROUP BY MEDICATION.MedicationID";
        // Sorts the entries from A-Z or Z-A
        if (MedicationPage.medicationNameSortSpinner.getSelectedItem().toString().equals("A-Z")) {
            query += " ORDER BY MEDICATION.Name ASC";
        } else if (MedicationPage.medicationNameSortSpinner.getSelectedItem().toString().equals("Z-A")) {
            query += " ORDER BY MEDICATION.Name DESC";
        }
        cursor = appDatabase.rawQuery(query, null);
        return cursor;
    }


    public static Cursor retrieveSelectedMedicationData(String medicationID) {
        cursor = appDatabase.rawQuery("SELECT * FROM MEDICATION WHERE MedicationID = '" + medicationID + "'", null);
        return cursor;
    }


    public static String retrieveAllAdministeredDoctors(String medicationID) {
        cursor = appDatabase.rawQuery("SELECT STAFF.FirstName, STAFF.LastName, STAFF.Occupation FROM STAFF " +
                "LEFT JOIN APPOINTMENTS ON STAFF.StaffID = APPOINTMENTS.StaffID " +
                "INNER JOIN PRESCRIPTIONS ON APPOINTMENTS.PrescriptionID = PRESCRIPTIONS.PrescriptionID " +
                "INNER JOIN MEDICATION ON MEDICATION.MedicationID = PRESCRIPTIONS.MedicationID " +
                "WHERE MEDICATION.MedicationID = '" + medicationID + "' AND APPOINTMENTS.Completed = 'false' AND APPOINTMENTS.Missed = 'false'", null);
        String staff = "";
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.getString(2).equals("Doctor")) {
                    staff += "Dr ";
                }
                staff += cursor.getString(0) + " " + cursor.getString(1) + ", ";
                cursor.moveToNext();
            }
        }
        return staff;
    }


    public static String retrieveAllPrescribedPatients(String medicationID) {
        cursor = appDatabase.rawQuery("SELECT PATIENTS.FirstName, PATIENTS.LastName FROM PATIENTS " +
                "INNER JOIN APPOINTMENTS ON PATIENTS.PatientID = APPOINTMENTS.PatientID " +
                "INNER JOIN PRESCRIPTIONS ON APPOINTMENTS.PrescriptionID = PRESCRIPTIONS.PrescriptionID " +
                "INNER JOIN MEDICATION ON MEDICATION.MedicationID = PRESCRIPTIONS.MedicationID " +
                "WHERE MEDICATION.MedicationID = '" + medicationID + "' AND APPOINTMENTS.Completed = 'false' AND APPOINTMENTS.Missed = 'false' " +
                "GROUP BY PATIENTS.PatientID", null);

        String patient = "";
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                patient += cursor.getString(0) + " " + cursor.getString(1) + ", ";
                cursor.moveToNext();
            }
        }
        return patient;
    }


    public static void updateSelectedMedication(String medicationID) {
        String cost = SelectedMedicationPage.selectedMedicationCostText.getText().toString().trim();
        if (cost.equals("")) {
            cost = "0";
        }
        String volume = SelectedMedicationPage.selectedMedicationVolumeText.getText().toString().trim();
        if (volume.equals("")) {
            volume = "0";
        }
        String notes = SelectedMedicationPage.selectedMedicationNotesText.getText().toString().trim();
        if (notes.equals("")) {
            notes = "...";
        }

        String query = "UPDATE MEDICATION SET Cost = '" + cost + "', Volume = '" + volume + "', Description = '" + notes + "' WHERE MedicationID = '" + medicationID + "'";
        appDatabase.execSQL(query);
    }


    public static void addMedication() {
        String newMedicationNameTextString = AddMedicationPopUp.newMedicationNameText.getText().toString().trim();
        String newMedicationCostTextString = AddMedicationPopUp.newMedicationCostText.getText().toString().trim();
        String newMedicationVolumeTextString = AddMedicationPopUp.newMedicationVolumeText.getText().toString().trim();
        String newMedicationDescriptionTextString = AddMedicationPopUp.newMedicationDescriptionText.getText().toString().trim();
        cursor = appDatabase.rawQuery("SELECT MedicationID FROM MEDICATION ORDER BY MedicationID DESC LIMIT 1", null);
        cursor.moveToFirst();
        int numberOfPrescriptions = Integer.parseInt(cursor.getString(0)) + 1;
        appDatabase.execSQL("INSERT INTO MEDICATION VALUES('" + numberOfPrescriptions + "', '" + newMedicationNameTextString + "', '" + newMedicationCostTextString + "', '" + newMedicationVolumeTextString + "', '" + newMedicationDescriptionTextString + "')");
    }


    public static int findSpecificPatient(String patientData) {
        String[] patientDataSplit = patientData.split(", ");
        String patientName = patientDataSplit[0];
        String[] patientNameSplit = patientName.split(" ");

        cursor = appDatabase.rawQuery("SELECT PatientID FROM PATIENTS WHERE FirstName = '" + patientNameSplit[0] + "' AND LastName = '" + patientNameSplit[1] + "'", null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            return Integer.valueOf(cursor.getString(0));
        }
        return 0;
    }


    public static String[] retrieveAllYears() {
        cursor = appDatabase.rawQuery("SELECT substr(Date,-4,4) AS year FROM APPOINTMENTS WHERE (Completed = 'true' OR Missed = 'true') AND year != '2000' GROUP BY year ORDER BY year", null);
        String[] years = new String[]{};
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            years = new String[cursor.getCount()];
            int count = 0;
            while (!cursor.isAfterLast()) {
                years[count] = cursor.getString(0);
                count++;
                cursor.moveToNext();
            }
        } else {
            years = new String[1];
            years[0] = "N/A";
        }
        return years;
    }


    public static String[] retrievePatientDataForReportPage() {
        String[] patientData = new String[3];
        cursor = appDatabase.rawQuery("SELECT * FROM PATIENTS", null);
        cursor.moveToFirst();
        patientData[0] = String.valueOf(cursor.getCount());

        cursor = appDatabase.rawQuery("SELECT COUNT(*) FROM PATIENTS WHERE Sectioned = 'Yes'", null);
        cursor.moveToFirst();
        patientData[1] = String.valueOf(cursor.getString(0));

        cursor = appDatabase.rawQuery("SELECT COUNT(*) FROM PATIENTS WHERE HomeCare = 'Yes'", null);
        cursor.moveToFirst();
        patientData[2] = String.valueOf(cursor.getString(0));

        return patientData;
    }


    public static Cursor retrieveAllDrugsAndCostsForReportsPage(String month, String year) {
        String query = "SELECT MEDICATION.Name, MEDICATION.Cost, SUM(MEDICATION.Cost) " +
                "FROM MEDICATION " +
                "INNER JOIN PRESCRIPTIONS ON MEDICATION.MedicationID = PRESCRIPTIONS.MedicationID " +
                "INNER JOIN APPOINTMENTS ON PRESCRIPTIONS.PrescriptionID = APPOINTMENTS.PrescriptionID ";
        if (month.length() == 2) {
            query += "WHERE APPOINTMENTS.Completed = 'true' AND MEDICATION.Name != 'TBC' AND substr(APPOINTMENTS.Date,-4,4) = '" + year + "' AND substr(APPOINTMENTS.Date, -7, 2) = '" + month + "' ";
        } else {
            query += "WHERE APPOINTMENTS.Completed = 'true' AND MEDICATION.Name != 'TBC' AND substr(APPOINTMENTS.Date,-4,4) = '" + year + "' AND substr(APPOINTMENTS.Date, -6, 1) = '" + month + "' ";
        }
        //"WHERE year = '" + year + "' AND month = '" + month + "' AND APPOINTMENTS.Completed = 'true' " +

        query += "GROUP BY MEDICATION.MedicationID ORDER BY MEDICATION.Name ASC";
        cursor = appDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            return cursor;
        } else {
            return null;
        }
    }


    public static int retrieveMonthDataForGraph(String month, String year) {
        String query = "SELECT COUNT(APPOINTMENTS.Completed) " +
                "FROM APPOINTMENTS " +
                "INNER JOIN HOSPITALS ON APPOINTMENTS.HospitalID = HOSPITALS.HospitalID ";

        if (month.length() == 2) {
            query += "WHERE APPOINTMENTS.Completed = 'true' AND APPOINTMENTS.Date != '01/01/2000' AND substr(APPOINTMENTS.Date,-4,4) = '" + year + "' AND substr(APPOINTMENTS.Date, -7, 2) = '" + month + "';";
        } else {
            query += "WHERE APPOINTMENTS.Completed = 'true' AND APPOINTMENTS.Date != '01/01/2000' AND substr(APPOINTMENTS.Date,-4,4) = '" + year + "' AND substr(APPOINTMENTS.Date, -6, 1) = '" + month + "';";
        }
        //"WHERE year = '" + year + "' AND month = '" + month + "' AND APPOINTMENTS.Completed = 'true' " +
        cursor = appDatabase.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return Integer.valueOf(cursor.getString(0));
        }
        return 0;
    }
}