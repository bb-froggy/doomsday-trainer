package froggy.game.doomsdaytrainer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SeriesResultsActivity extends AppCompatActivity {

    public static final String EXTRA_TOTAL_TIME = "total_time";
    public static final String EXTRA_AVERAGE_TIME = "average_time";
    public static final String EXTRA_CORRECT_COUNT = "correct_count";
    public static final String EXTRA_TOTAL_COUNT = "total_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_results);

        // Get data from intent
        long totalTimeMillis = getIntent().getLongExtra(EXTRA_TOTAL_TIME, 0);
        long averageTimeMillis = getIntent().getLongExtra(EXTRA_AVERAGE_TIME, 0);
        int correctCount = getIntent().getIntExtra(EXTRA_CORRECT_COUNT, 0);
        int totalCount = getIntent().getIntExtra(EXTRA_TOTAL_COUNT, 0);

        // Format times
        String formattedTotalTime = formatTime(totalTimeMillis);
        String formattedAverageTime = formatTime(averageTimeMillis);
        
        // Calculate accuracy percentage
        float accuracyPercentage = (totalCount > 0) ? (float) correctCount * 100 / totalCount : 0;

        // Set text views
        TextView textViewTotalTime = findViewById(R.id.textViewTotalTime);
        TextView textViewAverageTime = findViewById(R.id.textViewAverageTime);
        TextView textViewAccuracy = findViewById(R.id.textViewAccuracy);

        textViewTotalTime.setText(getString(R.string.series_time_total, formattedTotalTime));
        textViewAverageTime.setText(getString(R.string.series_time_average, formattedAverageTime));
        textViewAccuracy.setText(getString(R.string.series_accuracy, correctCount, totalCount, accuracyPercentage));

        // Set finish button
        Button buttonFinish = findViewById(R.id.buttonFinish);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Format time in a human-readable format
     * @param timeMillis Time in milliseconds
     * @return Formatted time string
     */
    private String formatTime(long timeMillis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis) - TimeUnit.MINUTES.toSeconds(minutes);
        long milliseconds = timeMillis - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds);
        
        if (minutes > 0) {
            return String.format(Locale.getDefault(), "%d:%02d.%01ds", minutes, seconds, milliseconds / 100);
        } else {
            return String.format(Locale.getDefault(), "%d.%01ds", seconds, milliseconds / 100);
        }
    }
}
