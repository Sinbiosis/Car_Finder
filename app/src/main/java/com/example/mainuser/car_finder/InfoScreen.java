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

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoScreen extends Activity implements SensorEventListener {
    public TextView textAccelView, textGyroView, textMagView, textOrientView, textTrueXYZView;

    public SensorManager mSensorManager;
    public Sensor mAccelerometer, mGyroscope, mMagnometer;

    private float[] mAccelXYZ = new float[3];
    private float[] mGeomagneticXYZ = new float[3];
    private float[] mGyroXYZ = new float[3];
    float Rot[] = new float[9];
    float MatI[] = new float[9];
    private float Orientation[] = new float[3];
    private float TrueAccel[] = new float[3];

    boolean success;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_info_screen);
        textAccelView = (TextView)findViewById(R.id.AccelView);
        textGyroView = (TextView)findViewById(R.id.GyroView);
        textMagView = (TextView)findViewById(R.id.MagView);
        textOrientView = (TextView)findViewById(R.id.OrientView);
        textTrueXYZView = (TextView)findViewById(R.id.TrueXYZView);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {

        // Display Accelerometer results
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelXYZ = event.values;
            textAccelView.setText(
                    String.format(
                            Locale.ENGLISH,
                            "Accelerometer X Direction: %.2f \nAccelerometer Y Direction: %.2f \nAccelerometer Z Direction: %.2f",
                            mAccelXYZ[0], mAccelXYZ[1], mAccelXYZ[2]
                    )
            );
        }

        // Display Gyroscope results
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mGyroXYZ = event.values;
            textGyroView.setText(
                    String.format(
                            Locale.ENGLISH,
                            "Gyroscope X Direction: %.2f \nGyroscope Y Direction: %.2f \nGyroscope Z Direction: %.2f",
                            mGyroXYZ[0], mGyroXYZ[1], mGyroXYZ[2]
                    )
            );
        }

        // Display Magnometer results
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

        // Display Orientation and "True" accelerometer results
        success = SensorManager.getRotationMatrix(Rot, MatI, mAccelXYZ, mGeomagneticXYZ);
        if (success) {
            SensorManager.getOrientation(Rot, Orientation);

            textOrientView.setText(
                    String.format(
                            Locale.ENGLISH,
                            "Azimuth: %.2f \nPitch: %.2f \nRoll: %.2f",
                            Orientation[0], Orientation[1], Orientation[2]
                    )
            );

            TrueAccel[0] = Rot[0]*mAccelXYZ[0] + Rot[1]*mAccelXYZ[1] + Rot[2]*mAccelXYZ[2];
            TrueAccel[1] = Rot[3]*mAccelXYZ[0] + Rot[4]*mAccelXYZ[1] + Rot[5]*mAccelXYZ[2];
            TrueAccel[2] = Rot[6]*mAccelXYZ[0] + Rot[7]*mAccelXYZ[1] + Rot[8]*mAccelXYZ[2];

            textTrueXYZView.setText(
                    String.format(
                            Locale.ENGLISH,
                            "True Accel X: %.2f \nTrue Accel Y: %.2f \nTrue Accel Z: %.2f",
                            TrueAccel[0], TrueAccel[1], TrueAccel[2]
                    )
            );
        }
    }

}
