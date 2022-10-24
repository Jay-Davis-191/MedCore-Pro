package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;

public class Page {
    private Activity s_activity;
    private String s_page;

    public Page(Activity activity, String page) {
        s_activity = activity;
        s_page = page;
    }

    public Intent changePage() {
        switch (s_page) {
            case "HomeAdmin":
                return new Intent(s_activity, HomePageAdmin.class);
            case "HomeStaff":
                return new Intent(s_activity, HomePageStaff.class);
            case "Patients":
                return new Intent(s_activity, PatientsPage.class);
            case "Appointments":
                return new Intent(s_activity, AppointmentsPage.class);
            case "Medication":
                return new Intent(s_activity, MedicationPage.class);
            case "Staff":
                return new Intent(s_activity, StaffPage.class);
            case "Reports":
                return new Intent(s_activity, ReportsPage.class);
            default:
                return new Intent();
        }
    }
}
