package com.example.mainuser.car_finder;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Fragment;
import android.widget.TextView;

import java.util.Locale;

import java.lang.Long;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoScreen extends Activity implements SensorEventListener {
    public TextView textAccelView, textMagView, textTrueXYZView, textPosView;

    public SensorManager mSensorManager;
    public Sensor mAccelerometer, mMagnometer;

    private float[] oldAccelVal = new float[3];
    private float[] mAccelSenVal = new float[3];
    private float[] mGeomagneticXYZ = new float[3];
    private float[] Rot = new float[9];
    private float[] MatI = new float[9];
    private float[] EarthAccel = new float[3];
    private float[] linear_acceleration = new float[3];
    private long  oldT, newT;
    private float delT;

    boolean success;
    float kFilterFactor = 0.1f;

    public PositionList posList = new PositionList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_info_screen);
        textAccelView = (TextView)findViewById(R.id.AccelView);
        textMagView = (TextView)findViewById(R.id.MagView);
        textTrueXYZView = (TextView)findViewById(R.id.TrueXYZView);
        textPosView = (TextView)findViewById(R.id.PositionView);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        posList.processEvent(event);

        PointF_3D pt;

        /** Note: to get positions, initialize a PositionList object
         * initialize an a sensor handler for both the geomagnetic and accelerometer
         * pass the events they collect to PositionList object by the processEvent method
         *
         * To retrieve relative positions, PositionList.getLastPoint() or PositionList.getPointAtPos(int n)
         * both return a PointF_3D object
         */

        pt = posList.getTotalDisplacement();

        textPosView.setText(String.format(
                Locale.ENGLISH,
                "Total Displacement\n\tX Component : %f \n\tY Component : %f \n\tZ Component : %f \n",
                pt.getX(), pt.getY(), pt.getZ()
                )
        );

        // Display Accelerometer result.
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            oldT = newT;
            newT = event.timestamp;
            delT = (Long.valueOf(newT - oldT)).floatValue()/1000000000f;

            mAccelSenVal = event.values;

            oldAccelVal[0] = kFilterFactor * event.values[0] + (1 - kFilterFactor) * oldAccelVal[0];
            oldAccelVal[1] = kFilterFactor * event.values[1] + (1 - kFilterFactor) * oldAccelVal[1];
            oldAccelVal[2] = kFilterFactor * event.values[2] + (1 - kFilterFactor) * oldAccelVal[2];

            linear_acceleration[0] = event.values[0] - oldAccelVal[0];
            linear_acceleration[1] = event.values[1] - oldAccelVal[1];
            linear_acceleration[2] = event.values[2] - oldAccelVal[2];

            textAccelView.setText(
                    String.format(
                            Locale.ENGLISH,
                            "Accelerometer X Direction: %f \nAccelerometer Y Direction: %f \nAccelerometer Z Direction: %f",
                            mAccelSenVal[0], mAccelSenVal[1], mAccelSenVal[2]
                    )
            );
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagneticXYZ = event.values;

            textMagView.setText(
                    String.format(
                            Locale.ENGLISH,
                            "Magnometer X Direction: %.2f \nMagnometer Y Direction: %.2f \nMagnometer Z Direction: %.2f",
                            mGeomagneticXYZ[0], mGeomagneticXYZ[1], mGeomagneticXYZ[2]
                    )
            );
        }

        success = SensorManager.getRotationMatrix(Rot, MatI, mAccelSenVal, mGeomagneticXYZ);
        if (success) {

            EarthAccel[0] = Rot[0]*linear_acceleration[0] + Rot[1]*linear_acceleration[1] + Rot[2]*linear_acceleration[2];
            EarthAccel[1] = Rot[3]*linear_acceleration[0] + Rot[4]*linear_acceleration[1] + Rot[5]*linear_acceleration[2];
            EarthAccel[2] = Rot[6]*linear_acceleration[0] + Rot[7]*linear_acceleration[1] + Rot[8]*linear_acceleration[2];

            textTrueXYZView.setText(
                    String.format(
                            Locale.ENGLISH,
                            "True Accel X: %.2f \nTrue Accel Y: %.2f \nTrue Accel Z: %.2f",
                            EarthAccel[0], EarthAccel[1], EarthAccel[2]
                    )
            );
        }
    }
}
