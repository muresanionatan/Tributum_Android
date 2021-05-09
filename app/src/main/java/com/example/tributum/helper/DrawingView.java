package com.example.tributum.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import com.example.tributum.listener.SignatureListener;

public class DrawingView extends View {

    private SignatureListener signatureListener;

    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint bitmapPaint;
    private Paint paint;

    public DrawingView(Context context, SignatureListener signatureListener) {
        super(context);
        this.signatureListener = signatureListener;

        path = new Path();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);

        canvas.drawPath(path, paint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;

        if (signatureListener != null)
            signatureListener.onDrawingStarted();
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        path.lineTo(mX, mY);
        // commit the path to our offscreen
        canvas.drawPath(path, paint);
        // kill this so we don't double draw
        path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void clear() {
        bitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
        System.gc();
    }
}