package de.hadizadeh.positioning.example;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.hadizadeh.positioning.controller.PositionListener;
import de.hadizadeh.positioning.controller.PositionManager;
import de.hadizadeh.positioning.controller.Technology;
import de.hadizadeh.positioning.exceptions.PositioningException;
import de.hadizadeh.positioning.exceptions.PositioningPersistenceException;
import de.hadizadeh.positioning.model.PositionInformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PositioningActivity extends Activity implements PositionListener {
    private PositionManager positionManager;
    private TextView currentPositionTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initializePositioning();
    }


    private void initializePositioning() {
        File file = new File(Environment.getExternalStorageDirectory(), "positioningPersistence.txt");
        try {
            positionManager = new PositionManager(file);
            Log.d("positionManager", "initialized");
        } catch (PositioningPersistenceException e) {
            e.printStackTrace();
        }

        Technology wifiTechnology = new WifiTechnology(this, "WIFI");
        CompassTechnology compassTechnology = new CompassTechnology(this, "compass", 80);

        try {
            positionManager.addTechnology(wifiTechnology);
            positionManager.addTechnology(compassTechnology);
        } catch (PositioningException e) {
            e.printStackTrace();
        }
        positionManager.registerPositionListener(this);

        final EditText mapName = (EditText) findViewById(R.id.mapname_et);
        Button mapBtn = (Button) findViewById(R.id.map_btn);
        Button startBtn = (Button) findViewById(R.id.start_btn);
        Button stopBtn = (Button) findViewById(R.id.stop_btn);
        currentPositionTv = (TextView) findViewById(R.id.current_position_tv);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionManager.map(mapName.getText().toString());
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionManager.startPositioning(100);
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionManager.stopPositioning();
            }
        });
    }

    @Override
    public void positionReceived(final PositionInformation positionInformation) {
        // Do nothing
    }

    @Override
    public void positionReceived(final List<PositionInformation> positionInformation) {
        currentPositionTv.post(new Runnable() {
            public void run() {
                String positioningText = "";
                for (int i = 0; i < positionInformation.size(); i++) {
                    positioningText += i + ".: " + positionInformation.get(i).getName() + System.getProperty("line.separator");
                }
                currentPositionTv.setText(positioningText);
            }
        });
    }
}
