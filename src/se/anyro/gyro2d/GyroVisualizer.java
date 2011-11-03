/*
 * Copyright (C) 2011 Adam Nybäck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.anyro.gyro2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GyroVisualizer extends View {

    private static final float RADIANS_TO_DEGREES = (float) (180d / Math.PI);
    private static final float RADIUS = 150;

    private Paint mGyroPaint = new Paint();
    private Paint mMagPaintNorth = new Paint();
    private Paint mMagPaintSouth = new Paint();
    private Paint mAccPaint = new Paint();
    private Paint mTouchPaint = new Paint();

    private float mGyroRotationX, mGyroRotationY, mGyroRotationZ;
    private float mAccX, mAccY;
    private float mMagX, mMagY;
    private float mFirstX, mFirstY;
    private float mSecondX, mSecondY;
    private boolean mMultiTouch = false;

    public GyroVisualizer(Context context) {
        this(context, null);
    }

    public GyroVisualizer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GyroVisualizer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mGyroPaint.setColor(0x77ffffff);
        mGyroPaint.setStyle(Style.STROKE);
        mGyroPaint.setStrokeWidth(5);
        mGyroPaint.setAntiAlias(true);

        mMagPaintNorth.setColor(0xffff0000);
        mMagPaintNorth.setStrokeWidth(5);
        mMagPaintNorth.setAntiAlias(true);
        mMagPaintSouth.setColor(0xffffffff);
        mMagPaintSouth.setStrokeWidth(5);
        mMagPaintSouth.setAntiAlias(true);

        mAccPaint.setColor(0xff33bb33);
        mAccPaint.setStrokeWidth(5);
        mAccPaint.setAntiAlias(true);

        mTouchPaint.setColor(0xff4444cc);
        mTouchPaint.setStrokeWidth(5);
        mTouchPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // MultiTouch
        if (mMultiTouch) {
            float mMidTouchX = (mFirstX + mSecondX) / 2;
            float mMidTouchY = (mFirstY + mSecondY) / 2;
            canvas.drawLine(mFirstX, mFirstY, mSecondX, mSecondY, mTouchPaint);
            canvas.drawLine(mMidTouchX - mMidTouchY + mSecondY, mMidTouchY - mSecondX + mMidTouchX, mMidTouchX
                    + mMidTouchY - mSecondY, mMidTouchY + mSecondX - mMidTouchX, mTouchPaint);
        }

        float midX = getWidth() / 2f;
        float midY = getHeight() / 2f;

        // Accelerometer
        float accX = midX + mAccX * 22f;
        float accY = midY + mAccY * 22f;
        canvas.drawLine(midX, midY, accX, accY, mAccPaint);
        canvas.drawCircle(accX, accY, 5, mAccPaint);

        // Compass
        canvas.drawLine(midX, midY, midX + mMagX * 4f, midY + mMagY * 4f, mMagPaintNorth);
        canvas.drawLine(midX, midY, midX - mMagX * 4f, midY - mMagY * 4f, mMagPaintSouth);

        // Gyroscope
        canvas.save();
        canvas.rotate(mGyroRotationZ * RADIANS_TO_DEGREES, midX, midY);
        canvas.drawLine(midX, midY - RADIUS, midX, midY + RADIUS, mGyroPaint);
        canvas.drawLine(midX - RADIUS, midY, midX + RADIUS, midY, mGyroPaint);
        canvas.drawCircle(midX, midY, RADIUS, mGyroPaint);
        canvas.restore();

        canvas.drawCircle(midX + mGyroRotationY * 350, midY + mGyroRotationX * 350, 10, mGyroPaint);
        invalidate();
    }

    public void setGyroRotation(float x, float y, float z) {
        mGyroRotationX = x;
        mGyroRotationY = y;
        mGyroRotationZ = z;
    }

    public void setAcceleration(float x, float y) {
        mAccX = x;
        mAccY = y;
    }

    public void setMagneticField(float x, float y) {
        mMagX = x;
        mMagY = y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            return true;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            mMultiTouch = false;
            return true;
        }

        int count = event.getPointerCount();
        if (count < 2) {
            mMultiTouch = false;
        } else {
            mMultiTouch = true;
            mFirstX = event.getX(0);
            mFirstY = event.getY(0);
            mSecondX = event.getX(1);
            mSecondY = event.getY(1);
        }
        return true;
    }
}
