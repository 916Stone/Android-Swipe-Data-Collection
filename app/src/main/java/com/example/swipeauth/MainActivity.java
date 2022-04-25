package com.example.swipeauth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.TextView;

import com.example.swipeauth.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // Data storage
    static final int DATA_COUNT = 80;
    static final int DATA_ENTRIES = 10000;
    String[] actions = new String[DATA_ENTRIES];
    double[] velocityX = new double[DATA_ENTRIES];
    double[] velocityY = new double[DATA_ENTRIES];
    int[] touchIndices = new int[DATA_ENTRIES];

    List<Long> timeStamp = new ArrayList<>();
    List<Double> pressures = new ArrayList<>();
    List<Double> fingerSizes = new ArrayList<>();

    private TextView swipeText;
    private VelocityTracker mVelocityTracker = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.swipeauth.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View mContent = binding.main;
        swipeText = binding.swipe;

        mContent.setOnTouchListener(new View.OnTouchListener() {
            int dataCount = 0;
            int moveIndex = 0;
            int touchIndex = 0;

            // Times
            long start;
//            long end;

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Variables for velocity tracking
                int index = motionEvent.getActionIndex();
                int pointerId = motionEvent.getPointerId(index);

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(dataCount == DATA_COUNT) {
                            swipeText.setText("Finished collecting\nPlease export data");
                            break;
                        }

                        // Action check
                        view.performClick();
                        System.out.println("Down");
                        actions[moveIndex] = "Down";

                        // Start time
                        start = System.currentTimeMillis();

//                        // Finger size
//                        fingerSizes[moveIndex] = motionEvent.getSize();
//
//                        // Pressure
//                        pressures[moveIndex] = motionEvent.getPressure();

                        // Velocity tracking
                        if(mVelocityTracker == null) {
                            // Retrieve a new VelocityTracker object to watch the
                            // velocity of a motion.
                            mVelocityTracker = VelocityTracker.obtain();
                        }
                        else {
                            // Reset the velocity tracker back to its initial state.
                            mVelocityTracker.clear();
                        }
                        // Add a user's movement to the tracker.
                        mVelocityTracker.addMovement(motionEvent);

                        // Count
                        touchIndices[moveIndex] = touchIndex;
                        moveIndex++;

                        break;

                    // Move
                    case MotionEvent.ACTION_MOVE:
                        if(dataCount == DATA_COUNT) {
                            swipeText.setText("Finished collecting\nPlease export data");
                            break;
                        }
                        else
                            swipeText.setText("Swiping...");

                        samples(motionEvent);

                        // Action check
                        System.out.println("Move");
                        actions[moveIndex] = "Move";

                        // End time for each interval
//                        end = System.currentTimeMillis();
//                        durations[moveIndex] = end - start;
                        // New start time
//                        start = System.currentTimeMillis();

//                        // Finger size
//                        fingerSizes[moveIndex] = motionEvent.getSize();
//
//                        // Pressure
//                        pressures[moveIndex] = motionEvent.getPressure();

                        // Velocity tracking
                        mVelocityTracker.addMovement(motionEvent);
                        // When you want to determine the velocity, call
                        // computeCurrentVelocity(). Then call getXVelocity()
                        // and getYVelocity() to retrieve the velocity for each pointer ID.
                        mVelocityTracker.computeCurrentVelocity(1);
                        // Log velocity of pixels per second
                        // Best practice to use VelocityTrackerCompat where possible.

                        velocityX[moveIndex] = mVelocityTracker.getXVelocity(pointerId);
                        velocityY[moveIndex] = mVelocityTracker.getYVelocity(pointerId);

                        // Count
                        touchIndices[moveIndex] = touchIndex;
                        moveIndex++;

                        break;

                    case MotionEvent.ACTION_UP:
                        if(dataCount == DATA_COUNT) {
                            swipeText.setText("Finished collecting\nPlease export data");

                            break;
                        }
                        else {
                            dataCount++;
                            swipeText.setText("Swipes: " + dataCount);
                        }

                        // Action check
                        System.out.println("Up");
                        actions[moveIndex] = "UP";

                        // Time
//                        end = System.currentTimeMillis();
//                        durations[moveIndex] = end - start;


//                        // Finger size
//                        fingerSizes[moveIndex] = motionEvent.getSize();
//
//                        // Pressure
//                        pressures[moveIndex] = motionEvent.getPressure();

                        // Count
                        touchIndices[moveIndex] = touchIndex;
                        moveIndex++;
                        touchIndex++;

                        break;

                    case MotionEvent.ACTION_CANCEL:
                        // Return a VelocityTracker object back to be re-used by others.
                        mVelocityTracker.recycle();
                        break;

                }
                return true;
            }
        });
    }

    public void samples(MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        for (int h = 0; h < historySize; h++) {
            System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
            timeStamp.add(ev.getHistoricalEventTime(h));
            for (int p = 0; p < pointerCount; p++) {
                System.out.printf("  pressure: (%f)|  ",
                        ev.getHistoricalPressure(p, h));
                pressures.add((double) ev.getHistoricalPressure(p, h));
                fingerSizes.add((double) ev.getHistoricalSize(p, h));
            }
        }
        System.out.printf("At time %d:", ev.getEventTime());
        timeStamp.add(ev.getEventTime());
        for (int p = 0; p < pointerCount; p++) {
            System.out.printf("  pressure: (%f)|  ",
                    ev.getPressure(p));
            pressures.add((double) ev.getPressure(p));
            fingerSizes.add((double) ev.getSize(p));
        }
    }

    public void export(View view) {
        StringBuilder data = new StringBuilder();
        int count = 0;

//        data.append("TouchIndex, MoveIndex, TimeStamp, Pressure, FingerSize\n");

        do {
            int temp = touchIndices[count];

            data.append(pressures.get(count)).append(",").append(fingerSizes.get(count)).append(",");
            count++;
            if (temp != touchIndices[count]) {
                data.append("\n");
            }
        } while (actions[count] != null);

        try {
            //Saving data to file
            FileOutputStream out = openFileOutput("trainingData.csv", Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();

            //Exporting
            Context context = getApplicationContext();
            File fileLocation = new File(getFilesDir(), "trainingData.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.swipeauth.fileProvider", fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);

            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "trainingData");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);

            Intent shareIntent = Intent.createChooser(fileIntent, null);
            startActivity(shareIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}