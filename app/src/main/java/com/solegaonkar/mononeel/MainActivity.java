package com.solegaonkar.mononeel;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String analysisDisplayFormat = "Nozzles: %d x %d\nTotal: %d\n\n" +
            "Layout:\nOffset from edges: %.1f, %.1f\n" +
            "Distances: %.1f, %.1f\n\n" +
            "Maximum Gap: %.2f;\n";

    private double x=0, y=0, r =0, s =0;
    private boolean square;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        addListeners();
        refreshAnalysis();
    }

    private void addListeners() {
        Log.i(this.getLocalClassName(), "Add Listeners Called");
        EditText etFieldSizeX = findViewById(R.id.etFieldSizeX);
        EditText etFieldSizeY = findViewById(R.id.etFieldSizeY);
        EditText etRadius = findViewById(R.id.etRadius);
        SeekBar sbGap = findViewById(R.id.sbSeparation);
        RadioGroup shape = findViewById(R.id.radioGroup);
        shape.setOnCheckedChangeListener(new InputFieldWatcher());
        etFieldSizeX.addTextChangedListener(new InputFieldWatcher());
        etFieldSizeY.addTextChangedListener(new InputFieldWatcher());
        etRadius.addTextChangedListener(new InputFieldWatcher());
        sbGap.setOnSeekBarChangeListener(new InputFieldWatcher());
    }

    private void readValues() {
        EditText etFieldSizeX = findViewById(R.id.etFieldSizeX);
        EditText etFieldSizeY = findViewById(R.id.etFieldSizeY);
        EditText etRadius = findViewById(R.id.etRadius);
        SeekBar sbGap = findViewById(R.id.sbSeparation);
        RadioGroup shape = findViewById(R.id.radioGroup);

        x = readNumber(etFieldSizeX);
        y = readNumber(etFieldSizeY);
        r = readNumber(etRadius);

        square = shape.getCheckedRadioButtonId() == R.id.radioSquare;
        s = (sbGap.getProgress() - 4.0) / 4;
    }

    private int readNumber(EditText etFieldSizeX) {
        int number = 0;
        String s = etFieldSizeX.getText().toString();
        if (s.length() > 0)
            number = Integer.parseInt(s);
        return number;
    }

    private void refreshAnalysis() {
        int nx, ny;
        double edgeXOffset, edgeYOffset, distanceX, distanceY, maxGap;
        readValues();

        try {
            if (!square) {
                double sx = (r + s) * Math.sqrt(3);
                double sy = 3 * (r + s) / 2;
                nx = (int) (x / sx) + 1;
                ny =  2 * (int) (y / (2*sy)) + 1;

                edgeXOffset = Math.floor(10 * (x - (nx - 1) * sx) / 2) / 10;
                edgeYOffset = Math.floor(10 * (y - (ny - 1) * sy) / 2) / 10;
                distanceX = Math.floor(10 * (x - edgeXOffset) / (nx - 1))/10;
                distanceY = Math.floor(10 * (y - edgeYOffset) / (ny - 1))/10;

                double theta = Math.atan(distanceX / (2 * distanceY)) * 2;
                maxGap = distanceX / (2 * Math.sin(theta)) - r;
            } else {
                double sx = (r + s) * Math.sqrt(2);
                double sy = (r + s) * Math.sqrt(2);
                nx = (int) (x / sx) + 1;
                ny = (int) (y / sy) + 1;

                edgeXOffset = Math.floor(10 * (x - (nx - 1) * sx) / 2) / 10;
                edgeYOffset = Math.floor(10 * (y - (ny - 1) * sy) / 2) / 10;
                distanceX = Math.floor(10 * (x - edgeXOffset) / (nx - 1)) / 10;
                distanceY = Math.floor(10 * (y - edgeYOffset) / (ny - 1)) / 10;

                maxGap = Math.floor(100 * (Math.sqrt(distanceX * distanceX + distanceY * distanceY) - 2 * r)) / 100;
            }
            String display = String.format(analysisDisplayFormat, nx, ny, nx*ny, edgeXOffset, edgeYOffset, distanceX, distanceY, maxGap);
            TextView analysisView = findViewById(R.id.tvAnalysis);
            analysisView.setText(display);
        } catch (Exception e) {
        }
    }

    private class InputFieldWatcher implements TextWatcher, SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {
        int number = 0;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            refreshAnalysis();
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            refreshAnalysis();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            refreshAnalysis();
        }
    }
}
