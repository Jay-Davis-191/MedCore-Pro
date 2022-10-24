package com.example.myapplication;

// Samples of BarChart Code taken from https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/main/java/com/xxmassdeveloper/mpchartexample/BarChartActivity.java

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationBarMenu;

import java.util.ArrayList;

public class ReportsPage extends AppCompatActivity {

    private static TextView reportTotalPatients, reportTotalSectionedPatients, reportTotalHomeCaredPatients, reportPrescribedDrugs, reportDrugCosts, reportDrugTotalCosts;
    private static Spinner reportMonthSpinner, reportYearSpinner;
    private static String[] reportPatientData;
    public static String selectedMonth, selectedYear;
    private static Cursor reportCursor;
    private static ArrayList barArrayList;
    private static BarChart reportBarChart;
    private static BarDataSet barDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_page);

        reportTotalPatients = findViewById(R.id.reportTotalPatients);
        reportTotalSectionedPatients = findViewById(R.id.reportTotalSectionedPatients);
        reportTotalHomeCaredPatients = findViewById(R.id.reportTotalHomeCaredPatients);
        reportPrescribedDrugs = findViewById(R.id.reportPrescribedDrugs);
        reportDrugCosts = findViewById(R.id.reportDrugCosts);
        reportDrugTotalCosts = findViewById(R.id.reportDrugTotalCosts);
        reportMonthSpinner = findViewById(R.id.reportMonthSpinner);
        reportYearSpinner = findViewById(R.id.reportYearSpinner);
        reportBarChart = findViewById(R.id.reportBarChart);

        String[] monthValues = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> sectionedValues;
        sectionedValues = new ArrayAdapter<>(this, R.layout.sectioned_spinner_design, monthValues);
        sectionedValues.setDropDownViewResource(R.layout.sectioned_spinner_items_design);
        reportMonthSpinner.setAdapter(sectionedValues);
        reportMonthSpinner.setSelection(0);

        String[] yearValues = SQLQueries.retrieveAllYears();
        sectionedValues = new ArrayAdapter<>(this, R.layout.sectioned_spinner_design, yearValues);
        sectionedValues.setDropDownViewResource(R.layout.sectioned_spinner_items_design);
        reportYearSpinner.setAdapter(sectionedValues);
        reportYearSpinner.setSelection(0);

        reportMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = adapterView.getSelectedItem().toString();
                convertMonth();
                retrieveDrugs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        reportYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = adapterView.getSelectedItem().toString();
                retrieveDrugs();
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        reportPatientData = SQLQueries.retrievePatientDataForReportPage();

        reportTotalPatients.setText(reportPatientData[0]);
        reportTotalSectionedPatients.setText(reportPatientData[1]);
        reportTotalHomeCaredPatients.setText(reportPatientData[2]);

        selectedMonth = reportMonthSpinner.getSelectedItem().toString();
        if (!reportYearSpinner.getSelectedItem().toString().equals("N/A")) {
            selectedYear = reportYearSpinner.getSelectedItem().toString();
        }
        else {
            Toast.makeText(this, "Please mark some appointments as completed to see results", Toast.LENGTH_LONG).show();
        }

        convertMonth();
        retrieveDrugs();
        getData();


    }


    private static void retrieveDrugs() {
        reportCursor = SQLQueries.retrieveAllDrugsAndCostsForReportsPage(selectedMonth, selectedYear);
        String prescribedDrugs = "";
        String drugCosts = "";
        String drugTotalCosts = "";

        if (!(reportCursor == null)) {
            reportCursor.moveToFirst();
            int count = 1;
            while (!reportCursor.isAfterLast()) {
                prescribedDrugs += count + ". " + reportCursor.getString(0) + "\n";
                drugCosts += count + ". $" + reportCursor.getString(1) + "\n";
                drugTotalCosts += count + ". $" + reportCursor.getString(2) + "\n";
                count++;
                reportCursor.moveToNext();
            }
        }
        if (prescribedDrugs.equals("")) {
            prescribedDrugs = "No Medication Taken";
        }
        reportPrescribedDrugs.setText(prescribedDrugs);
        reportDrugCosts.setText(drugCosts);
        reportDrugTotalCosts.setText(drugTotalCosts);
    }


    private static void convertMonth() {
        switch (selectedMonth) {
            case "January":
                selectedMonth = "1";
                break;
            case "February":
                selectedMonth = "2";
                break;
            case "March":
                selectedMonth = "3";
                break;
            case "April":
                selectedMonth = "4";
                break;
            case "May":
                selectedMonth = "5";
                break;
            case "June":
                selectedMonth = "6";
                break;
            case "July":
                selectedMonth = "7";
                break;
            case "August":
                selectedMonth = "8";
                break;
            case "September":
                selectedMonth = "9";
                break;
            case "October":
                selectedMonth = "10";
                break;
            case "November":
                selectedMonth = "11";
                break;
            case "December":
                selectedMonth = "12";
                break;
            default:
                selectedMonth = "1";
                break;
        }
    }

    private void getData() {
        int[] graphData = new int[12];
        for (int i = 0; i < graphData.length; i++) {
            graphData[i] = SQLQueries.retrieveMonthDataForGraph(String.valueOf(i + 1), selectedYear);
        }

        barArrayList = new ArrayList<BarEntry>();

        barArrayList.add(new BarEntry(0f, graphData[0]));
        barArrayList.add(new BarEntry(1f, graphData[1]));
        barArrayList.add(new BarEntry(2f, graphData[2]));
        barArrayList.add(new BarEntry(3f, graphData[3]));
        barArrayList.add(new BarEntry(4f, graphData[4]));
        barArrayList.add(new BarEntry(5f, graphData[5]));
        barArrayList.add(new BarEntry(6f, graphData[6]));
        barArrayList.add(new BarEntry(7f, graphData[7]));
        barArrayList.add(new BarEntry(8f, graphData[8]));
        barArrayList.add(new BarEntry(9f, graphData[9]));
        barArrayList.add(new BarEntry(10f, graphData[10]));
        barArrayList.add(new BarEntry(11f, graphData[11]));

        barDataSet = new BarDataSet(barArrayList, "Months");
        //barDataSet = new BarDataSet(barArrayList, "Number of completed appointments for each hospital for selected year");

        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        reportBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));
        reportBarChart.getXAxis().setTextSize(15f);

        BarData barData = new BarData(barDataSet);
        reportBarChart.setData(barData);
        reportBarChart.setDrawBarShadow(true);
        reportBarChart.setDrawValueAboveBar(true);
        reportBarChart.setPinchZoom(false);
        reportBarChart.setDrawGridBackground(false);

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(15f);
        reportBarChart.getDescription().setEnabled(false);
        reportBarChart.setBackgroundColor(Color.WHITE);
    }
}