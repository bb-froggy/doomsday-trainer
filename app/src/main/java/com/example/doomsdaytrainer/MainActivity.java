package com.example.doomsdaytrainer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        // Generate a random date on start
        generateRandomDate();
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
     * Generate a random date between 1900 and 2099
     */
    private void generateRandomDate() {
        currentDate = Calendar.getInstance();
        
        // Generate random year between 1900 and 2099
        int year = 1900 + random.nextInt(200);
        
        // Generate random month (0-11 in Calendar)
        int month = random.nextInt(12);
        
        // Determine maximum day for the month
        currentDate.set(year, month, 1);
        int maxDay = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Generate random day (1-maxDay)
        int day = 1 + random.nextInt(maxDay);
        
        // Set the random date
        currentDate.set(year, month, day);
        
        // Format and display the date (without day of week)
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
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
        
        if (guessedDay == actualDay) {
            // Correct guess
            textViewResult.setText(getString(R.string.correct_guess, actualDayName));
            textViewResult.setTextColor(getResources().getColor(R.color.colorCorrect));
        } else {
            // Incorrect guess
            textViewResult.setText(getString(R.string.incorrect_guess, actualDayName));
            textViewResult.setTextColor(getResources().getColor(R.color.colorIncorrect));
        }
    }
}
