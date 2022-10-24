package com.example.myapplication;

// Parts of this are from https://stackoverflow.com/questions/34772248/how-to-create-dynamic-tablelayout-in-runtime-with-custom-horizontal-and-vertica. Created by Arthez, 2016

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.view.GestureDetectorCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

public class SimpleTextTableWithBorders extends TableLayout {

    private Context mContext;
    private String mactivity;
    private String[][] mTableContent, mneededData;
    private int mTextColor, mBorderColor, mBackgroundColor, mSelectedRowColor;
    private int mTextViewBorderWidth, mTableBorderWidth;
    private TableLayout table_layout, mTableLayout;
    private GestureDetectorCompat gestureDetector;
    private Boolean doubleClick = false;
    private long time, doubleClickLastTime;
    private Handler doubleHandler;
    private String[] mneededHeaders, mheaderNames;
    private int[] headerLocations;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public SimpleTextTableWithBorders(Context context, String[][] tableContent, TableLayout tableLayout, String activity, String[] neededHeaders) {
        super(context);
        mContext = context;
        mTableContent = tableContent;
        mBackgroundColor = R.color.white;
        mSelectedRowColor = R.color.light_grey;
        mTextViewBorderWidth = 0;
        mTableBorderWidth = mTextViewBorderWidth * 2;
        mTableLayout = tableLayout;
        mactivity = activity;
        mneededHeaders = neededHeaders;
        mheaderNames = tableContent[0];
        getAppropriateData();
        setupTable();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupTable() {
        TableRow tableRow, headerRow;
        TextView textView;
        headerLocations = new int[mneededHeaders.length];

        int horizontalPadding = 0;

        switch(mactivity) {
            case "Patients":
                horizontalPadding = 20;
                break;
            case "Medication":
                horizontalPadding = 20;
                break;
            case "PatientsProfile":
                horizontalPadding = 40;
                break;
            case "Staff":
                horizontalPadding = 20;
                break;
            case "Appointments":
                horizontalPadding = 30;
                break;
            default:
                horizontalPadding = 5;
                break;
        }

        setStretchAllColumns(true);
        setBackground(borderDrawable(mTableBorderWidth * 10));
        setPadding(mTableBorderWidth, mTableBorderWidth, mTableBorderWidth, mTableBorderWidth);

        ////
        headerRow = new TableRow(mContext);

        int notesIndex = Arrays.asList(mneededData[0]).indexOf("Notes");
        int patientNameIndex = Arrays.asList(mneededData[0]).indexOf("Patient Name");
        int IDIndex = Arrays.asList(mneededData[0]).indexOf("ID");

        for (int currentColumn = 0; currentColumn < mneededData[0].length; currentColumn++) {
            textView = new TextView(mContext);
            textView.setText(mneededData[0][currentColumn]);
            textView.setTextSize(22);
            textView.setBackground(borderDrawable(2));
            textView.setTextColor(Color.WHITE);
            headerRow.setBackgroundColor(Color.BLUE);
            textView.setGravity(Gravity.CENTER);
            textView.setMaxWidth(200);
            textView.setMaxLines(1);

            if (currentColumn == 0 && mactivity.equals("PatientsProfile")) {
                textView.setMaxWidth(10);
            }


            textView.setPadding(horizontalPadding, 3, horizontalPadding, 3);
            headerRow.addView(textView);
        }
        headerRow.setClickable(false);
        table_layout = mTableLayout;
        table_layout.setStretchAllColumns(true);
        table_layout.addView(headerRow);


        for (int currentRow = 1; currentRow < mneededData.length; currentRow++) {
            tableRow = new TableRow(mContext);

            for (int currentColumn = 0; currentColumn < mneededData[0].length; currentColumn++) {
                textView = new TextView(mContext);
                textView.setText(mneededData[currentRow][currentColumn]);
                textView.setTextSize(22);
                textView.setBackground(borderDrawable(2));
                textView.setTextColor(Color.BLACK);

                if (currentColumn == 1) {
                    if (mactivity.equals("Appointments") && currentColumn == 1) {
                        SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yy");
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");

                        String[] selectedDateSplit = mneededData[currentRow][currentColumn].split(" ");
                        Date currentDate = null;
                        Date selecteDate = null;
                        try {
                            currentDate = sdformat.parse(LocalDate.now().format(dtf));
                            selecteDate = sdformat.parse(selectedDateSplit[0]);

                            if (currentDate.compareTo(selecteDate) <= 0) {
                                tableRow.setBackgroundColor(Color.WHITE);
                            } else {
                                tableRow.setBackgroundColor(Color.LTGRAY);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (!mactivity.equals("Appointments")) {
                    tableRow.setBackgroundColor(Color.WHITE);
                }

                textView.setMaxLines(1);
                if (currentColumn == notesIndex) {
                    textView.setGravity(Gravity.LEFT);
                    textView.setSingleLine(true);
                    textView.setMaxWidth(1200);
                }
                else {
                    textView.setGravity(Gravity.CENTER);
                    if (currentColumn == patientNameIndex) {
                        textView.setMaxWidth(250);
                    }
                }

                textView.setPadding(horizontalPadding, 3, horizontalPadding, 3);
                tableRow.addView(textView);

            }
            tableRow.setClickable(true);
            tableRow.setOnClickListener(tablerowOnClickListener);

            table_layout.addView(tableRow);
        }

    }

    private GradientDrawable borderDrawable(int borderWidth) {
        GradientDrawable shapeDrawable = new GradientDrawable();
        shapeDrawable.setStroke(borderWidth, mBackgroundColor);
        return shapeDrawable;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAppropriateData () {
        if (mactivity.equals("Patients")) {
            mneededData = new String[mTableContent.length][mTableContent[0].length];
            mneededData[0] = mTableContent[0];
            for (int currentRow = 1; currentRow < mTableContent.length; currentRow++) {

                mneededData[currentRow][0] = mTableContent[currentRow][0];
                mneededData[currentRow][1] = mTableContent[currentRow][1] + " " + mTableContent[currentRow][2];

                String age = retrieveAge(mTableContent[currentRow][3]);
                mneededData[currentRow][2] = age;
                mneededData[currentRow][3] = mTableContent[currentRow][4];
                mneededData[currentRow][4] = mTableContent[currentRow][5];
            }
        }

        else if (mactivity.equals("Medication")) {
            mneededData = new String[mTableContent.length][mTableContent[0].length];
            mneededData[0] = mTableContent[0];
            for (int currentRow = 1; currentRow < mTableContent.length; currentRow++) {
                mneededData[currentRow][0] = mTableContent[currentRow][0];
                mneededData[currentRow][1] = mTableContent[currentRow][1];
                mneededData[currentRow][2] = mTableContent[currentRow][2] + " tablets";
                mneededData[currentRow][3] = "$" + mTableContent[currentRow][3];
                mneededData[currentRow][4] = mTableContent[currentRow][4];
            }
        }

        else if (mactivity.equals("PatientsProfile")) {
            mneededData = new String[mTableContent.length][mTableContent[0].length];
            mneededData[0] = mTableContent[0];
            for (int currentRow = 1; currentRow < mTableContent.length; currentRow++) {
                mneededData[currentRow][0] = mTableContent[currentRow][0];
                mneededData[currentRow][1] = mTableContent[currentRow][1];
                mneededData[currentRow][2] = mTableContent[currentRow][2];
                mneededData[currentRow][3] = mTableContent[currentRow][3];
            }
        }

        else if (mactivity.equals("Staff")) {
            mneededData = new String[mTableContent.length][mTableContent[0].length];
            mneededData[0] = mTableContent[0];
            for (int currentRow = 1; currentRow < mTableContent.length; currentRow++) {
                mneededData[currentRow][0] = mTableContent[currentRow][0];
                mneededData[currentRow][1] = mTableContent[currentRow][1] + " " + mTableContent[currentRow][2];
                mneededData[currentRow][2] = mTableContent[currentRow][3];
                mneededData[currentRow][3] = mTableContent[currentRow][4];
                mneededData[currentRow][4] = mTableContent[currentRow][5];
            }
        }

        else if (mactivity.equals("Appointments")) {
            mneededData = new String[mTableContent.length][mTableContent[0].length];
            mneededData[0] = mTableContent[0];
            for (int currentRow = 1; currentRow < mTableContent.length; currentRow++) {
                mneededData[currentRow][0] = mTableContent[currentRow][0];
                String[] dateSplit = mTableContent[currentRow][2].split(":");
                String timePeriod = "";

                mneededData[currentRow][1] = mTableContent[currentRow][1] + " " + dateSplit[0] + ":" + dateSplit[1] + timePeriod;
                mneededData[currentRow][2] = mTableContent[currentRow][3] + " " + mTableContent[currentRow][4];
                mneededData[currentRow][3] = mTableContent[currentRow][5] + " " + mTableContent[currentRow][6];
                mneededData[currentRow][4] = mTableContent[currentRow][7];
                mneededData[currentRow][5] = mTableContent[currentRow][8];
            }
        }
    }


    // Retrieves the age of each patient name for the 'Age' column
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String retrieveAge(String DOB) {
        LocalDate currentDate = LocalDate.now();
        String[] Age = DOB.split("/");  // Splits date into three parts
        LocalDate AgeDifference = LocalDate.of(Integer.parseInt(Age[2]), Integer.parseInt(Age[1]), Integer.parseInt(Age[0]));  // Formats reminderDate entry into the same format as the DOB
        Period period = Period.between(currentDate, AgeDifference);  // Calculates the difference between the due date and the current date in days, weeks, and years - if appropriate.
        return String.valueOf(Math.abs(period.getYears()));
    }


    // Runs when one of the entry rows is clicked on
    private OnClickListener tablerowOnClickListener = new OnClickListener() {
        public void onClick(View view) {
            for (int i = 0; i < table_layout.getChildCount(); i++) {
                TextView idTextView = (TextView) ((TableRow) view).getChildAt(0);
                View row = table_layout.getChildAt(i);
                String id;

                if (row == view) {
                    if (System.currentTimeMillis() - doubleClickLastTime < 200) {
                        row.setBackgroundColor(getResources().getColor(R.color.light_grey));
                        Intent intent = new Intent();
                        id = idTextView.getText().toString().trim();

                        if (mactivity.equals("Patients")) {
                            intent = new Intent(getContext(), SelectedPatientPage.class);
                        } else if (mactivity.equals("PatientsProfile")) {
                            intent = new Intent(getContext(), SelectedAppointmentPage.class);
                        } else if (mactivity.equals("Medication")) {
                            intent = new Intent(getContext(), SelectedMedicationPage.class);
                        } else if (mactivity.equals("Appointments")) {
                            intent = new Intent(getContext(), SelectedAppointmentPage.class);
                        } else if (mactivity.equals("Staff")) {
                            intent = new Intent(getContext(), SelectedStaffPage.class);
                        }

                        if (!id.equals("")) {
                            intent.putExtra("ID", Integer.parseInt(id));
                            getContext().startActivity(intent);
                        }
                    }
                    else {
                        row.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        doubleClickLastTime = System.currentTimeMillis();
                    }
                }
                else
                {
                    if (!mactivity.equals("Appointments")) {
                        row.setBackgroundColor(getResources().getColor(android.R.color.white));
                    }
                }
                table_layout.getChildAt(0).setBackgroundColor(Color.BLUE);
            }

        }
    };




}