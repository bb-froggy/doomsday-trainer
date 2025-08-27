package com.example.doomsdaytrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView textViewDate;
    private TextView textViewResult;
    private Calendar currentDate;
    private Random random = new Random();
    private SharedPreferences preferences;
    
    // Series mode variables
    private boolean inSeriesMode = false;
    private int seriesCount = 0;
    private int currentSeriesIndex = 0;
    private int correctGuessCount = 0;
    private long seriesStartTime = 0;
    private Button buttonStartSeries;
    
    // Define day of week constants to match Calendar class
    private static final int SUNDAY = 1;
    private static final int MONDAY = 2;
    private static final int TUESDAY = 3;
    private static final int WEDNESDAY = 4;
    private static final int THURSDAY = 5;
    private static final int FRIDAY = 6;
    private static final int SATURDAY = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize preferences
        preferences = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        
        textViewDate = findViewById(R.id.textViewDate);
        textViewResult = findViewById(R.id.textViewResult);

        // Set up day of week buttons
        setupDayButton(R.id.buttonMonday, MONDAY);
        setupDayButton(R.id.buttonTuesday, TUESDAY);
        setupDayButton(R.id.buttonWednesday, WEDNESDAY);
        setupDayButton(R.id.buttonThursday, THURSDAY);
        setupDayButton(R.id.buttonFriday, FRIDAY);
        setupDayButton(R.id.buttonSaturday, SATURDAY);
        setupDayButton(R.id.buttonSunday, SUNDAY);

        // Set up new date button
        Button buttonNewDate = findViewById(R.id.buttonNewDate);
        buttonNewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateRandomDate();
                textViewResult.setText("");
            }
        });
        
        // Set up series mode button
        buttonStartSeries = findViewById(R.id.buttonStartSeries);
        buttonStartSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSeriesMode();
            }
        });

        // Generate a random date on start
        generateRandomDate();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the date when returning from settings
        // (in case date format or range has changed)
        if (currentDate != null) {
            displayFormattedDate();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // Open settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a button for a day of the week
     * 
     * @param buttonId The resource ID for the button
     * @param dayOfWeek The Calendar constant for the day (e.g., Calendar.MONDAY)
     */
    private void setupDayButton(int buttonId, final int dayOfWeek) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess(dayOfWeek);
            }
        });
    }

    /**
     * Generate a random date using the date range from settings
     */
    private void generateRandomDate() {
        currentDate = Calendar.getInstance();
        
        // Get date range from preferences
        int startYear = preferences.getInt(SettingsActivity.KEY_START_YEAR, SettingsActivity.DEFAULT_START_YEAR);
        int endYear = preferences.getInt(SettingsActivity.KEY_END_YEAR, SettingsActivity.DEFAULT_END_YEAR);
        
        // Calculate the range
        int yearRange = endYear - startYear + 1;
        
        // Generate random year between startYear and endYear
        int year = startYear + random.nextInt(yearRange);
        
        // Generate random month (0-11 in Calendar)
        int month = random.nextInt(12);
        
        // Determine maximum day for the month
        currentDate.set(year, month, 1);
        int maxDay = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Generate random day (1-maxDay)
        int day = 1 + random.nextInt(maxDay);
        
        // Set the random date
        currentDate.set(year, month, day);
        
        // Display the date in the selected format
        displayFormattedDate();
    }
    
    /**
     * Format and display the date according to settings
     */
    private void displayFormattedDate() {
        // Get selected date format from preferences
        String formatPattern = preferences.getString(
                SettingsActivity.KEY_DATE_FORMAT, 
                SettingsActivity.DEFAULT_FORMAT
        );
        
        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatPattern, Locale.getDefault());
        String dateString = dateFormat.format(currentDate.getTime());
        textViewDate.setText(dateString);
    }

    /**
     * Check if the guessed day matches the actual day of the week
     * 
     * @param guessedDay The Calendar constant for the guessed day
     */
    private void checkGuess(int guessedDay) {
        int actualDay = currentDate.get(Calendar.DAY_OF_WEEK);
        
        // Get the name of the day for display
        String[] dayNames = new String[] {
                "", // Calendar days are 1-based
                getString(R.string.day_sunday),
                getString(R.string.day_monday),
                getString(R.string.day_tuesday),
                getString(R.string.day_wednesday),
                getString(R.string.day_thursday),
                getString(R.string.day_friday),
                getString(R.string.day_saturday)
        };
        
        String actualDayName = dayNames[actualDay];
        boolean isCorrect = (guessedDay == actualDay);
        
        if (isCorrect) {
            // Correct guess
            textViewResult.setText(getString(R.string.correct_guess, actualDayName));
            textViewResult.setTextColor(getResources().getColor(R.color.colorCorrect));
            if (inSeriesMode) correctGuessCount++;
        } else {
            // Incorrect guess
            textViewResult.setText(getString(R.string.incorrect_guess, actualDayName));
            textViewResult.setTextColor(getResources().getColor(R.color.colorIncorrect));
        }
        
        // Handle series mode progression
        if (inSeriesMode) {
            currentSeriesIndex++;
            
            if (currentSeriesIndex >= seriesCount) {
                // Series complete - show results
                finishSeriesMode();
            } else {
                // Continue with next date after a brief delay
                textViewResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        generateRandomDate();
                        textViewResult.setText("");
                        // Update progress
                        textViewDate.setText(textViewDate.getText() + 
                                "\n" + getString(R.string.series_progress, currentSeriesIndex + 1, seriesCount));
                    }
                }, 1500); // 1.5 second delay
            }
        }
    }
    
    /**
     * Start a new series session
     */
    private void startSeriesMode() {
        // Get series count from preferences
        seriesCount = preferences.getInt(SettingsActivity.KEY_SERIES_COUNT, SettingsActivity.DEFAULT_SERIES_COUNT);
        
        // Reset counters
        currentSeriesIndex = 0;
        correctGuessCount = 0;
        inSeriesMode = true;
        seriesStartTime = System.currentTimeMillis();
        
        // Generate first date
        generateRandomDate();
        textViewResult.setText("");
        
        // Update UI for series mode
        textViewDate.setText(textViewDate.getText() + 
                "\n" + getString(R.string.series_progress, currentSeriesIndex + 1, seriesCount));
        buttonStartSeries.setEnabled(false);
    }
    
    /**
     * Finish series mode and show results
     */
    private void finishSeriesMode() {
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - seriesStartTime;
        long averageTime = totalTime / seriesCount;
        
        // Reset mode
        inSeriesMode = false;
        buttonStartSeries.setEnabled(true);
        
        // Start results activity
        Intent intent = new Intent(this, SeriesResultsActivity.class);
        intent.putExtra(SeriesResultsActivity.EXTRA_TOTAL_TIME, totalTime);
        intent.putExtra(SeriesResultsActivity.EXTRA_AVERAGE_TIME, averageTime);
        intent.putExtra(SeriesResultsActivity.EXTRA_CORRECT_COUNT, correctGuessCount);
        intent.putExtra(SeriesResultsActivity.EXTRA_TOTAL_COUNT, seriesCount);
        startActivity(intent);
        
        // Generate a new date for regular mode
        generateRandomDate();
    }
}
