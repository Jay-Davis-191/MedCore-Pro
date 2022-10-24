package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.database.sqlite.SQLiteDatabase;

public class Database {
    public static SQLiteDatabase appDatabase;

    public static void createEntities() {
        //appDatabase.execSQL("CREATE TABLE IF NOT EXISTS USERS(UserID INT NOT NULL PRIMARY KEY, Name VARCHAR, Username VARCHAR NOT NULL UNIQUE, Password VARCHAR NOT NULL, AccountType VARCHAR NOT NULL)");
        appDatabase.execSQL("CREATE TABLE IF NOT EXISTS USERS(Username VARCHAR NOT NULL PRIMARY KEY, Name VARCHAR, Password VARCHAR NOT NULL, AccountType VARCHAR NOT NULL)");
        appDatabase.execSQL("CREATE TABLE IF NOT EXISTS PATIENTS(PatientID INT NOT NULL PRIMARY KEY, FirstName VARCHAR, LastName VARCHAR, Sectioned VARCHAR, HomeCare VARCHAR, Notes VARCHAR, DateOfBirth VARCHAR, HomeAddress VARCHAR,  TotalAppointments INT, MissedAppointments INT, NextOfKin VARCHAR);");
        appDatabase.execSQL("CREATE TABLE IF NOT EXISTS APPOINTMENTS(AppointmentID INT NOT NULL PRIMARY KEY, Time TIME, Date DATE, Description VARCHAR, Completed VARCHAR, Missed VARCHAR, HospitalID INT, PatientID INT, StaffID INT, PrescriptionID INT);");
        appDatabase.execSQL("CREATE TABLE IF NOT EXISTS STAFF(StaffID INT NOT NULL PRIMARY KEY, FirstName VARCHAR, LastName VARCHAR, Occupation VARCHAR, Salary INT, HospitalID INT);");
        appDatabase.execSQL("CREATE TABLE IF NOT EXISTS HOSPITALS(HospitalID INT NOT NULL PRIMARY KEY, Name VARCHAR, Address VARCHAR, Notes VARCHAR);");
        appDatabase.execSQL("CREATE TABLE IF NOT EXISTS PRESCRIPTIONS(PrescriptionID INT NOT NULL PRIMARY KEY, Description VARCHAR, MedicationID INT);");
        appDatabase.execSQL("CREATE TABLE IF NOT EXISTS MEDICATION(MedicationID INT NOT NULL PRIMARY KEY, Name VARCHAR, Volume INT, Cost INT, Description VARCHAR);");
    }

    public static void insertInitialData() {
        // Insert User Data
        String[] allHeaderNames = new String[]{"Username", "Name", "Password", "Account Type"};
        String[][] allUserData = new String[3][allHeaderNames.length];
        allUserData[0] = new String[]{"jdavis", "Jay Davis", "testpassword1", "Administrator"};
        allUserData[1] = new String[]{"mbennett", "Mark Bennett", "testpassword2", "Nurse"};
        allUserData[2] = new String[]{"glopez", "George Lopez", "testpassword2", "Receptionist"};

        for (int count = 0; count < allUserData.length; count++) {
            appDatabase.execSQL("INSERT INTO USERS VALUES('" + allUserData[count][0] + "', '" + allUserData[count][1] + "', '" + allUserData[count][2] + "', '" + allUserData[count][3] + "');");
        }


        // Insert Patient Data
        allHeaderNames = new String[]{"ID", "FirstName", "LastName", "Sectioned", "HomeCare", "Notes", "DOB", "Address", "TotalAppointments", "MissedAppointments", "NextOfKin"};
        String[][] allPatientData = new String[4][allHeaderNames.length];
        allPatientData[0] = new String[]{"1", "Max", "Carter", "No", "Yes", "Suffers from emphysema and gastrointestinal complications", "21/5/1977",
                "29 Rudelgin St, Skinnet, Scotland", "30", "3", "Linda Carter"};
        allPatientData[1] = new String[]{"2", "Sarah", "Lane", "Yes", "No", "Shown aggressive behaviour in previous psychiatric sessions", "13/3/1997",
                "The State Hospital", "50", "4", "John Lane"};
        allPatientData[2] = new String[]{"3", "Jon", "Smith", "Yes", "No", "Frequent outbursts regular instances of violence", "01/6/1952",
                "The State Hospital", "140", "9", "Anthony Smith"};
        allPatientData[3] = new String[]{"4", "Brian", "Smith", "No", "No", "Shows positive behaviour every week. An outstanding patient who is doing better everyday. Also, helps out other patients with their problems by chatting to them", "01/6/1980", "31 Stenhouse Crescent, Saughton, Scotland", "150", "3", "Michael Smith"};

        for (int count = 0; count < allPatientData.length; count++) {
            appDatabase.execSQL("INSERT INTO PATIENTS VALUES('" + Integer.parseInt(allPatientData[count][0]) + "', '" + allPatientData[count][1] + "', '" + allPatientData[count][2] + "', '" + allPatientData[count][3] + "', '" + allPatientData[count][4] + "', '" + allPatientData[count][5] + "', '" + allPatientData[count][6] + "', '" + allPatientData[count][7] + "', '" + Integer.parseInt(allPatientData[count][8]) + "', '" + Integer.parseInt(allPatientData[count][9]) + "', '" + allPatientData[count][10] + "');");
        }


        //------------------------------------------------------------------------------------------
        // Insert Appointment Data
        allHeaderNames = new String[]{"Appointment ID", "Time", "Date", "Description", "Completed", "Missed", "HospitalID", "PatientID", "StaffID", "PrescriptionID"};

        String[][] allAppointmentData = new String[5][allHeaderNames.length];
        allAppointmentData[0] = new String[]{"1", "10:00", "20/8/2022", "No aggressive behaviour today", "true", "false", "1", "1", "1", "1"};
        allAppointmentData[1] = new String[]{"2", "14:00", "20/8/2022", "Seems to be getting better", "false", "true", "1", "2", "1", "2"};
        allAppointmentData[2] = new String[]{"3", "10:00", "22/8/2022", "...", "false", "false", "1", "1", "1", "3", "1"};
        allAppointmentData[3] = new String[]{"4", "12:00", "23/8/2022", "6 week follow-up", "false", "false", "2", "3", "2", "3"};
        allAppointmentData[4] = new String[]{"5", "14:00", "23/8/2022", "Weekly follow-up", "false", "false", "2", "4", "2", "3"};

        for (int count = 0; count < allAppointmentData.length; count++) {
            appDatabase.execSQL("INSERT INTO APPOINTMENTS VALUES('" + Integer.parseInt(allAppointmentData[count][0]) + "', '9:00', '01/01/2000', '...', 'true', 'true', '1', '" + (count + 1) + "', '1', 0)");
        }


        //------------------------------------------------------------------------------------------
        // Insert Staff Data
        allHeaderNames = new String[]{"Staff ID", "FirstName", "LastName", "Occupation", "Salary", "Hospital ID"};

        String[][] allStaffData = new String[3][allHeaderNames.length];
        allStaffData[0] = new String[]{"1", "Jonathan", "Pilkington", "Doctor", "100000", "1"};
        allStaffData[1] = new String[]{"2", "Mary", "Lynch", "Doctor", "105000", "1"};
        allStaffData[2] = new String[]{"3", "Mary", "Bennett", "Nurse", "70000", "2"};

        for (int count = 0; count < allStaffData.length; count++) {
            // StaffID INT, FirstName VARCHAR, LastName VARCHAR, Occupation VARCHAR, Salary INT, HospitalID INT
            appDatabase.execSQL("INSERT INTO STAFF VALUES('" + Integer.parseInt(allStaffData[count][0]) + "', '" + allStaffData[count][1] + "', '" + allStaffData[count][2] + "', '" + allStaffData[count][3] + "', '" + Integer.parseInt(allStaffData[count][4]) + "', '" + Integer.parseInt(allStaffData[count][5]) + "');");
        }


        //------------------------------------------------------------------------------------------
        // Insert Hospitals Data
        allHeaderNames = new String[]{"Hospital ID", "Name", "Address", "Notes"};

        String[][] allHospitalData = new String[2][allHeaderNames.length];
        allHospitalData[0] = new String[]{"1", "The State Hospital", "Carstairs, West End, Lanark ML11 8RP, United Kingdom", "..."};
        allHospitalData[1] = new String[]{"2", "Leverndale Hospital", "510 Crookston Rd, Glasgow G53 7TU, United Kingdom", "..."};

        for (int count = 0; count < allHospitalData.length; count++) {
            // HospitalID INT, Name VARCHAR, Address VARCHAR, Notes VARCHAR
            appDatabase.execSQL("INSERT INTO HOSPITALS VALUES('" + Integer.parseInt(allHospitalData[count][0]) + "', '" + allHospitalData[count][1] + "', '" + allHospitalData[count][2] + "', '" + allHospitalData[count][3] + "');");
        }


        //------------------------------------------------------------------------------------------
        // Insert Prescription Data
        allHeaderNames = new String[]{"Prescription ID", "Description", "Medication ID"};

        String[][] allPrescriptionData = new String[4][allHeaderNames.length];
        allPrescriptionData[0] = new String[]{"0", "TBC", "0"};
        allPrescriptionData[1] = new String[]{"1", "Take 2 tablets a night", "2"};
        allPrescriptionData[2] = new String[]{"2", "Take 1 tablet in the morning and 1 at night", "1"};
        allPrescriptionData[3] = new String[]{"3", "Take 1 a day, after food", "3"};

        for (int count = 0; count < allPrescriptionData.length; count++) {
            // PrescriptionID INT, Description VARCHAR, MedicationID INT
            appDatabase.execSQL("INSERT INTO PRESCRIPTIONS VALUES('" + Integer.parseInt(allPrescriptionData[count][0]) + "', '" + allPrescriptionData[count][1] + "', '" + Integer.parseInt(allPrescriptionData[count][2]) + "');");
        }


        //------------------------------------------------------------------------------------------
        // Insert Medication Data
        allHeaderNames = new String[]{"Medication ID", "Name", "Volume", "Cost", "Description"};

        String[][] allMedicationData = new String[8][allHeaderNames.length];
        allMedicationData[0] = new String[]{"0", "TBC", "0", "0", "..."};
        allMedicationData[1] = new String[]{"1", "Panadol", "20", "10", "..."};
        allMedicationData[2] = new String[]{"2", "Ibuprofen", "30", "15", "..."};
        allMedicationData[3] = new String[]{"3", "Arsenaphine", "20", "40", "Anti-psychotic medication"};
        allMedicationData[4] = new String[]{"4", "Lithobid", "30", "35", "Mood stabilizer"};
        allMedicationData[5] = new String[]{"5", "Flouxetine", "20", "50", "Used for dissociate identity disorder"};
        allMedicationData[6] = new String[]{"6", "Sertraline", "15", "45", "Used for dissociate identity disorder"};
        allMedicationData[7] = new String[]{"7", "Duloxetine", "12", "30", "Used for dissociate identity disorder"};

        for (int count = 0; count < allMedicationData.length; count++) {
            appDatabase.execSQL("INSERT INTO MEDICATION VALUES('" + Integer.parseInt(allMedicationData[count][0]) + "', '" + allMedicationData[count][1] + "', '" + Integer.parseInt(allMedicationData[count][2]) + "', '" + Integer.parseInt(allMedicationData[count][3]) + "', '" + allMedicationData[count][4] + "');");
        }
    }

}
