package com.example.doomsdaytrainer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    // Constants for SharedPreferences
    public static final String PREFS_NAME = "DoomsdayTrainerPrefs";
    public static final String KEY_START_YEAR = "start_year";
    public static final String KEY_END_YEAR = "end_year";
    public static final String KEY_DATE_FORMAT = "date_format";
    public static final String KEY_SERIES_COUNT = "series_count";
    
    // Constants for date formats
    public static final String FORMAT_MDY = "MM/dd/yyyy";
    public static final String FORMAT_DMY = "dd.MM.yyyy";
    public static final String FORMAT_YMD = "yyyy-MM-dd";
    public static final String FORMAT_LONG = "MMMM d, yyyy";
    
    // Default values
    public static final int DEFAULT_START_YEAR = 1900;
    public static final int DEFAULT_END_YEAR = 2099;
    public static final String DEFAULT_FORMAT = FORMAT_LONG;
    public static final int DEFAULT_SERIES_COUNT = 10;
    public static final int MAX_SERIES_COUNT = 100;

    private EditText editTextStartYear;
    private EditText editTextEndYear;
    private EditText editTextSeriesCount;
    private RadioGroup radioGroupDateFormats;
    private Button buttonSave;
    
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Enable the back button in the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        // Initialize views
        editTextStartYear = findViewById(R.id.editTextStartYear);
        editTextEndYear = findViewById(R.id.editTextEndYear);
        editTextSeriesCount = findViewById(R.id.editTextSeriesCount);
        radioGroupDateFormats = findViewById(R.id.radioGroupDateFormats);
        buttonSave = findViewById(R.id.buttonSaveSettings);
        
        // Load preferences
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Load current settings
        loadSettings();
        
        // Set up save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    /**
     * Load settings from SharedPreferences
     */
    private void loadSettings() {
        // Load year range
        int startYear = preferences.getInt(KEY_START_YEAR, DEFAULT_START_YEAR);
        int endYear = preferences.getInt(KEY_END_YEAR, DEFAULT_END_YEAR);
        int seriesCount = preferences.getInt(KEY_SERIES_COUNT, DEFAULT_SERIES_COUNT);
        
        editTextStartYear.setText(String.valueOf(startYear));
        editTextEndYear.setText(String.valueOf(endYear));
        editTextSeriesCount.setText(String.valueOf(seriesCount));
        
        // Load date format preference
        String dateFormat = preferences.getString(KEY_DATE_FORMAT, DEFAULT_FORMAT);
        
        // Select the appropriate radio button
        int radioId;
        switch (dateFormat) {
            case FORMAT_MDY:
                radioId = R.id.radioMDY;
                break;
            case FORMAT_DMY:
                radioId = R.id.radioDMY;
                break;
            case FORMAT_YMD:
                radioId = R.id.radioYMD;
                break;
            case FORMAT_LONG:
            default:
                radioId = R.id.radioLong;
                break;
        }
        
        RadioButton radioButton = findViewById(radioId);
        radioButton.setChecked(true);
    }

    /**
     * Save settings to SharedPreferences
     */
    private void saveSettings() {
        try {
            // Get values from inputs
            int startYear = Integer.parseInt(editTextStartYear.getText().toString());
            int endYear = Integer.parseInt(editTextEndYear.getText().toString());
            int seriesCount = Integer.parseInt(editTextSeriesCount.getText().toString());
            
            // Validate year range
            if (endYear <= startYear) {
                Toast.makeText(this, R.string.invalid_year_range, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validate series count
            if (seriesCount < 1 || seriesCount > MAX_SERIES_COUNT) {
                Toast.makeText(this, R.string.invalid_series_count, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get selected date format
            String dateFormat = getSelectedDateFormat();
            
            // Save to preferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(KEY_START_YEAR, startYear);
            editor.putInt(KEY_END_YEAR, endYear);
            editor.putString(KEY_DATE_FORMAT, dateFormat);
            editor.putInt(KEY_SERIES_COUNT, seriesCount);
            editor.apply();
            
            // Close the activity
            finish();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get the selected date format from radio buttons
     */
    private String getSelectedDateFormat() {
        int selectedId = radioGroupDateFormats.getCheckedRadioButtonId();
        
        if (selectedId == R.id.radioMDY) {
            return FORMAT_MDY;
        } else if (selectedId == R.id.radioDMY) {
            return FORMAT_DMY;
        } else if (selectedId == R.id.radioYMD) {
            return FORMAT_YMD;
        } else {
            return FORMAT_LONG;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
