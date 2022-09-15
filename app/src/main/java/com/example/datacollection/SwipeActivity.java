package com.example.datacollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.datacollection.databinding.ActivitySwipeBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwipeActivity extends AppCompatActivity {
    // data storage
    static final int DATA_COUNT = 50;
    static final int DATA_ENTRIES = 10000;
    String[] answers = new String[10];
    String[] actions = new String[DATA_ENTRIES];
    String[] directions = new String[DATA_ENTRIES];
    int[] swipeId = new int[DATA_ENTRIES];

    List<Long> duration = new ArrayList<>();
    List<Double> pressures = new ArrayList<>();
    List<Double> fingerSizes = new ArrayList<>();
    List<Long> timeStamp = new ArrayList<>();
    List<Integer> coordX = new ArrayList<>();
    List<Integer> coordY = new ArrayList<>();

    private TextView instruction, counter, arrowIns;
    private Button exportButton, nextDButton;

    // arrows
    private ImageView arrow;

    // variables
    private int swipeCount = 0;
    private int moveCount = 0;
    private int instructionCount = 0;

    // count how many swipes are collected for each direction
    private int directionCount = 0;
    // count how many directions have finished collecting
    private int directionCounter = 0;

    private final List<String> instructionList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        super.onCreate(savedInstanceState);

        com.example.datacollection.databinding.ActivitySwipeBinding binding = ActivitySwipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View mContent = binding.swipe;
        instruction = binding.instruction;
        counter = binding.counter;
        arrowIns = binding.arrowInstruction;
        exportButton = binding.exportButton;
        nextDButton = binding.nextDButton;

        arrow = binding.arrowImage;

        exportButton.setVisibility(View.GONE);

        addInstruction();

        // receive the answers array from QuestionActivity
        Bundle b = this.getIntent().getExtras();
        answers = b.getStringArray("answers");

        mContent.setOnTouchListener(new View.OnTouchListener() {
            // Times
            long startTime;
            long endTime;

            // Coords
            int startX = 0;
            int startY = 0;
            int endX = 0;
            int endY = 0;

            @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        // action check
                        view.performClick();
                        System.out.println("Down");
                        actions[moveCount] = "Down";

                        // swipe starting time
                        startTime = System.currentTimeMillis();

                        // swipe starting coords
                        startX = (int) motionEvent.getX();
                        startY = (int) motionEvent.getY();

                        swipeId[moveCount] = swipeCount;
                        moveCount++;
                        break;

                    case MotionEvent.ACTION_MOVE:

                        // Action check
                        System.out.println("Move");
                        actions[moveCount] = "Move";

                        // sampling data
                        sampling(motionEvent);

                        swipeId[moveCount] = swipeCount;
                        moveCount++;
                        break;

                    case MotionEvent.ACTION_UP:

                        // Action check
                        System.out.println("Up");
                        actions[moveCount] = "UP";

                        // swipe ending time
                        endTime = System.currentTimeMillis();
                        duration.add(endTime - startTime);

                        // swipe ending coords
                        endX = (int) motionEvent.getX();
                        endY = (int) motionEvent.getY();

                        Direction direction = getDirection(startX, startY, endX, endY);
                        directions[swipeCount] = direction.toString();

                        directionCount++;
                        counter.setText(String.valueOf(directionCount));

                        if (directionCount == DATA_COUNT) {
                            instruction.setText("Click on next to proceed");
                            nextDButton.setVisibility(View.VISIBLE);
                            arrow.setVisibility(View.GONE);
                        }

                        swipeId[moveCount] = swipeCount;
                        moveCount++;
                        swipeCount++;

                        break;

                }
                return true;
            }
        });

    }

    private void showArrow() {

        switch (directionCounter) {
            case 0: arrow.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.down);
                    arrow.setVisibility(View.VISIBLE);
                    break;

            case 1: arrow.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.downleft);
                    arrow.setVisibility(View.VISIBLE);
                    break;

            case 2: arrow.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.left);
                    arrow.setVisibility(View.VISIBLE);
                    break;

            case 3: arrow.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.upleft);
                    arrow.setVisibility(View.VISIBLE);
                    break;

            case 4: arrow.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.up);
                    arrow.setVisibility(View.VISIBLE);
                    break;

            case 5: arrow.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.upright);
                    arrow.setVisibility(View.VISIBLE);
                    break;

            case 6: arrow.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.right);
                    arrow.setVisibility(View.VISIBLE);
                    break;

            case 7: arrow.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.downright);
                    arrow.setVisibility(View.VISIBLE);
                    break;

            case 8: arrow.setVisibility(View.GONE);
                    break;
        }

    }

    // instructions for each direction with the same order
    private void addInstruction() {
        instructionList.add("Please swipe from top to bottom");
        instructionList.add("Now swipe from top right to bottom left");
        instructionList.add("Now swipe from right to left");
        instructionList.add("Now swipe from bottom right to top left");
        instructionList.add("Now swipe from bottom to top");
        instructionList.add("Now swipe from bottom left to top right");
        instructionList.add("Now swipe from left to right");
        instructionList.add("Now swipe from top left to bottom right");
    }

    @SuppressLint("SetTextI18n")
    public void next(View view) {
        arrowIns.setVisibility(View.VISIBLE);
        showArrow();
        directionCount = 0;
        counter.setText(String.valueOf(directionCount));
        nextDButton.setVisibility(View.GONE);

        if (instructionCount == 0) {
            moveCount = 0;
            swipeCount = 0;
            Arrays.fill(actions, null);
            Arrays.fill(directions, null);
            Arrays.fill(swipeId, 0);

            duration.clear();
            pressures.clear();
            fingerSizes.clear();
            timeStamp.clear();
            coordX.clear();
            coordY.clear();

            instruction.setText(instructionList.get(instructionCount));
        }
        else if (instructionCount < 8) {
            instruction.setText(instructionList.get(instructionCount));
        }
        else if (instructionCount == 8) {
            instruction.setText("You have finished the task\nPlease export the data");
            counter.setVisibility(View.GONE);
            exportButton.setVisibility(View.VISIBLE);
        }

        instructionCount++;
        directionCounter++;
    }

    // calculate swipe angles
    public Direction getDirection(float x1, float y1, float x2, float y2){
        double rad = Math.atan2(y1-y2,x2-x1) + Math.PI;
        double angle = (rad*180/Math.PI + 180)%360;
        return Direction.fromAngle(angle);
    }

    public enum Direction{
        top_bottom,
        topRight_bottomLeft,
        right_left,
        bottomRight_topLeft,
        bottom_top,
        bottomLeft_topRight,
        left_right,
        topLeft_bottomRight;

        public static Direction fromAngle(double angle){
            if (inRange(angle, 75, 105)){
                return Direction.bottom_top;
            }
            else if (inRange(angle, 15, 75)){
                return Direction.bottomLeft_topRight;
            }
            else if (inRange(angle, 0, 15) || inRange(angle, 345, 360)){
                return Direction.left_right;
            }
            else if (inRange(angle, 285, 345)){
                return Direction.topLeft_bottomRight;
            }
            else if (inRange(angle, 255, 285)){
                return Direction.top_bottom;
            }
            else if (inRange(angle, 195, 255)){
                return Direction.topRight_bottomLeft;
            }
            else if (inRange(angle, 165, 195)){
                return Direction.right_left;
            }
            else {
                return  Direction.bottomRight_topLeft;
            }
        }

        private static boolean inRange(double angle, float init, float end){
            return (angle >= init) && (angle < end);
        }
    }

    // access the batched historical event data points
    public void sampling(MotionEvent ev) {
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
                coordX.add((int) ev.getHistoricalX(p, h));
                coordY.add((int) ev.getHistoricalY(p, h));
            }
        }
        System.out.printf("At time %d:", ev.getEventTime());
        timeStamp.add(ev.getEventTime());
        for (int p = 0; p < pointerCount; p++) {
            System.out.printf("  pressure: (%f)|  ",
                    ev.getPressure(p));
            pressures.add((double) ev.getPressure(p));
            fingerSizes.add((double) ev.getSize(p));
            coordX.add((int) ev.getX(p));
            coordY.add((int) ev.getY(p));
        }
    }


    // write the data to files and share intent
    public void export(View view) {
        StringBuilder data1 = new StringBuilder();
        StringBuilder data2 = new StringBuilder();
        StringBuilder data3 = new StringBuilder();
        StringBuilder data4 = new StringBuilder();

        int count = 0;

        for (String answer : answers)
            if (answer != null)
                data4.append(answer).append(",");

        do {
            int temp = swipeId[count];
            data1.append(pressures.get(count)).append(",");
            data2.append(fingerSizes.get(count)).append(",");
            data3.append(coordX.get(count)).append(":").append(coordY.get(count)).append(",");
            count++;
            if (temp != swipeId[count]) {
                data1.append("\n");
                data2.append("\n");
                data3.append("\n");
                data4.append("\n").append(directions[temp]).append(",").append(duration.get(temp)).append(",");
            }
        } while (actions[count] != null);

        try {
            // saving data to file
            FileOutputStream out1 = openFileOutput("pressures.csv", Context.MODE_PRIVATE);
            out1.write(data1.toString().getBytes());
            out1.close();

            FileOutputStream out2 = openFileOutput("sizes.csv", Context.MODE_PRIVATE);
            out2.write(data2.toString().getBytes());
            out2.close();

            FileOutputStream out3 = openFileOutput("coords.csv", Context.MODE_PRIVATE);
            out3.write(data3.toString().getBytes());
            out3.close();

            FileOutputStream out4 = openFileOutput("swipes.csv", Context.MODE_PRIVATE);
            out4.write(data4.toString().getBytes());
            out4.close();

            // file provider and path
            Context context = getApplicationContext();
            File location1 = new File(getFilesDir(), "pressures.csv");
            Uri path1 = FileProvider.getUriForFile(context, "com.example.datacollection.fileProvider", location1);
            File location2 = new File(getFilesDir(), "sizes.csv");
            Uri path2 = FileProvider.getUriForFile(context, "com.example.datacollection.fileProvider", location2);
            File location3 = new File(getFilesDir(), "coords.csv");
            Uri path3 = FileProvider.getUriForFile(context, "com.example.datacollection.fileProvider", location3);
            File location4 = new File(getFilesDir(), "swipes.csv");
            Uri path4 = FileProvider.getUriForFile(context, "com.example.datacollection.fileProvider", location4);


            // output file list
            ArrayList<Uri> files = new ArrayList<>();
            files.add(path1);
            files.add(path2);
            files.add(path3);
            files.add(path4);

            // share intent
            Intent fileIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "data files");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

            Intent shareIntent = Intent.createChooser(fileIntent, null);
            startActivity(shareIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}