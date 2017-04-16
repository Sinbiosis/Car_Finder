package com.example.mainuser.car_finder;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import java.lang.Long;
import java.util.Locale;

/**
 * Created by mainuser on 4/15/17.
 */

public class PositionList extends CircularDoublyLinkedList {
    private CircularDoublyLinkedList<PointF_3D> posList;
    private CircularDoublyLinkedList<PointF_3D> AccelList;

    private PointF_3D linear_acceleration;
    private PointF_3D totalPosition;

    private float[] mGeomagneticXYZ;
    private float[] EarthAccel;

    private float[] Rot;
    private float[] MatI;

    private long  oldT, newT;
    private float delT;

    private static final int sizeAccelList = 10;

    private static final int size = 2;

    boolean success;
    float kFilterFactor = 0.1f;

    public PositionList(){
        posList = new CircularDoublyLinkedList<>();
        AccelList = new CircularDoublyLinkedList<>();

        linear_acceleration = new PointF_3D();
        totalPosition = new PointF_3D();

        EarthAccel = new float[3];
        mGeomagneticXYZ = new float[3];

        MatI = new float[9];
        Rot = new float[9];

    }

    public void processEvent (SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            PointF_3D mAccelSenVal = new PointF_3D(event.values[0], event.values[1], event.values[2]);

            if(oldT == 0) {
                oldT = event.timestamp;
                newT = event.timestamp;
            } else {
                oldT = newT;
                newT = event.timestamp;
            }

            delT = Long.valueOf(newT - oldT).floatValue()/1000000000f;

            if(AccelList.getSize() < sizeAccelList) {
                AccelList.insertAtEnd(mAccelSenVal);
            } else {
                AccelList.overwriteNode(mAccelSenVal);
            }

            linear_acceleration = filter(AccelList);

        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagneticXYZ = event.values;
        }

        success = SensorManager.getRotationMatrix(Rot, MatI, AccelList.getEndNode().getData().getXYZ(), mGeomagneticXYZ);
        if (success) {
            PointF_3D ptr = new PointF_3D();

            EarthAccel[0] = Rot[0]*linear_acceleration.getX() + Rot[1]*linear_acceleration.getY() + Rot[2]*linear_acceleration.getZ();
            EarthAccel[1] = Rot[3]*linear_acceleration.getX() + Rot[4]*linear_acceleration.getY() + Rot[5]*linear_acceleration.getZ();
            EarthAccel[2] = Rot[6]*linear_acceleration.getX() + Rot[7]*linear_acceleration.getY() + Rot[8]*linear_acceleration.getZ();

            ptr.setXYZ(0.5f*EarthAccel[0]*delT*delT, 0.5f*EarthAccel[1]*delT*delT, 0.5f*EarthAccel[2]*delT*delT);

            Log.i("PositionList_Process",String.format(
                    Locale.ENGLISH,
                    "Position Value: \n\tX : %f \n\tY : %f \n\tZ : %f \n",
                    ptr.getX(), ptr.getY(), ptr.getZ()
                    )
            );

            totalPosition.setX(totalPosition.getX() + ptr.getX());
            totalPosition.setY(totalPosition.getY() + ptr.getY());
            totalPosition.setZ(totalPosition.getZ() + ptr.getZ());

            Log.i("PositionList_Process",String.format(
                    Locale.ENGLISH,
                    "Total Position Value: \n\tX : %f \n\tY : %f \n\tZ : %f \n",
                    totalPosition.getX(), totalPosition.getY(), totalPosition.getZ()
                    )
            );

            if(posList.getSize() < size){
                posList.insertAtEnd(ptr);
            } else {
                posList.overwriteNode(ptr);
            }
        }
    }

    private PointF_3D filter(CircularDoublyLinkedList<PointF_3D> vals) {
        PointF_3D filteredVal = new PointF_3D();

        PointF_3D filter = new PointF_3D(
                kFilterFactor * vals.getEndNode().getData().getX() + (1 - kFilterFactor) * vals.getEndNode().getLinkPrev().getData().getX(),
                kFilterFactor * vals.getEndNode().getData().getY() + (1 - kFilterFactor) * vals.getEndNode().getLinkPrev().getData().getY(),
                kFilterFactor * vals.getEndNode().getData().getZ() + (1 - kFilterFactor) * vals.getEndNode().getLinkPrev().getData().getZ()
        );

        filteredVal.setX(vals.getEndNode().getData().getX() - filter.getX());
        filteredVal.setY(vals.getEndNode().getData().getY() - filter.getY());
        filteredVal.setZ(vals.getEndNode().getData().getZ() - filter.getZ());

        return filteredVal;
    }

    public PointF_3D getPointAtPos(int pos) {
        Node<PointF_3D> ptr = posList.getNodeAtPos(pos);
        return ptr.getData();
    }

    public PointF_3D getLastPoint() {
        return posList.getEndNode().getData();
    }

    public boolean isEmpty() {
        return posList.isEmpty();
    }

    public int getSize() {
        return posList.getSize();
    }
}