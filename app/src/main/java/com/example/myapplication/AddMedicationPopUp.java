package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddMedicationPopUp extends AppCompatActivity {

    public static EditText newMedicationNameText, newMedicationCostText, newMedicationVolumeText, newMedicationDescriptionText;
    private static Button saveNewMedicationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication_pop_up);

        newMedicationNameText = findViewById(R.id.newMedicationNameText);
        newMedicationCostText = findViewById(R.id.newMedicationCostText);
        newMedicationVolumeText = findViewById(R.id.newMedicationVolumeText);
        newMedicationDescriptionText = findViewById(R.id.newMedicationDescriptionText);
        saveNewMedicationButton = findViewById(R.id.saveNewMedicationButton);

        //----------Initializes the objects used for creating the size of this activity. This is used for making this activity appear like a pop-up, instead of a new page.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  // Retrieves the width of the screen in pixels
        int height = dm.heightPixels;  // Retrieves the height of the screen in pixels
        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));  // Sets the screen size of the activity.

        saveNewMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateEntries()) {
                    SQLQueries.addMedication();
                    finish();
                }
            }
        });
    }


    private boolean validateEntries() {
        String newMedicationNameTextString = newMedicationNameText.getText().toString().trim();
        String newMedicationCostTextString = newMedicationCostText.getText().toString().trim();
        String newMedicationVolumeTextString = newMedicationVolumeText.getText().toString().trim();
        String newMedicationDescriptionTextString = newMedicationDescriptionText.getText().toString().trim();

        if (newMedicationNameTextString.equals("")) {
            Toast.makeText(this, "Please provide a name", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newMedicationCostTextString.equals("")) {
            Toast.makeText(this, "Please provide a cost", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newMedicationVolumeTextString.equals("")) {
            Toast.makeText(this, "Please provide a volume", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (newMedicationDescriptionTextString.equals("")) {
            Toast.makeText(this, "Please provide a description", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
}