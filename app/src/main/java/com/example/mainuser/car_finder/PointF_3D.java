package com.example.mainuser.car_finder;

/**
 * Created by mainuser on 4/15/17.
 */

public class PointF_3D {
    private float[] xyz;

    public PointF_3D() {
        xyz = new float[3];
        xyz[0] = 0;
        xyz[1] = 0;
        xyz[2] = 0;
    }

    public PointF_3D(float valX, float valY, float valZ) {
        xyz = new float[3];
        xyz[0] = valX;
        xyz[1] = valY;
        xyz[2] = valZ;
    }

    public PointF_3D(PointF_3D pt) {
        xyz = pt.getXYZ();
    }

    public PointF_3D(float[] vals) {
        xyz = vals;
    }

    public float[] getXYZ() {
        return xyz;
    }

    public float getX() {
        return xyz[0];
    }

    public float getY() {
        return xyz[1];
    }

    public float getZ() {
        return xyz[2];
    }

    public void setXYZ(float[] vals) {
        xyz = vals;
    }

    public void setXYZ(float valX, float valY, float valZ) {
        xyz[0] = valX;
        xyz[1] = valY;
        xyz[2] = valZ;
    }

    public void setX(float val) {
        xyz[0] = val;
    }

    public void setY(float val) {
        xyz[1] = val;
    }

    public void setZ(float val) {
        xyz[2] = val;
    }
}