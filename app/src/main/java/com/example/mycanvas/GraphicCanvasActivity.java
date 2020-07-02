package com.example.mycanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GraphicCanvasActivity extends View {
    int startX = -1;
    int startY = -1;
    int stopX = -1;
    int stopY = -1;
    int shapeType = 0;
    int shapeColor = 0;
    String toolState = "pencil";
    Path path;
    Paint paint;
    Paint eraserpaint;
    Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint;
    List<Shape> shapes = new ArrayList<>();
    boolean isFinished = false;

    public GraphicCanvasActivity(Context context) {
        super(context);
        path = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public GraphicCanvasActivity(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public GraphicCanvasActivity(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GraphicCanvasActivity(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void createNewCanvas() {
        path = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mBitmap = Bitmap.createBitmap(3000, 6000, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private void touch_start(float x, float y) {
        path.reset();
        path.moveTo(x,y);
        mX = x;
        mY = y;
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
        if (shapeType == 5) {
            mCanvas.drawPath(path, eraserpaint);
        } else {
            mCanvas.drawPath(path, paint);
        }
        // kill this so we don't double draw
        path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Shape touchedShape = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isFinished = false;
                startX = (int) event.getX();
                startY = (int) event.getY();

                switch (toolState) {
                    case "pencil":
                        shapeType = 1;
                        touch_start(startX, startY);
                        break;
                    case "line":
                        touchedShape = obtainTouchedShape(startX, startY);
                        shapeType = 2;
                        break;
                    case "circle":
                        touchedShape = obtainTouchedShape(startX, startY);
                        shapeType = 3;
                        break;
                    case "rect":
                        touchedShape = obtainTouchedShape(startX, startY);
                        shapeType = 4;
                        Log.d("MyTag", "select rect");
                        break;
                    case "eraser":
                        shapeType = 5;
                        touch_start(startX, startY);
                        Log.d("MyTag", "지우개 선택");
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                stopX = (int) event.getX();
                stopY = (int) event.getY();
                if (shapeType == 1 || shapeType == 5) {
                    touch_move(stopX, stopY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touchedShape == null) {
                    Shape shape = new Shape();

                    shape.startX = startX;
                    shape.startY = startY;
                    shape.stopX = stopX;
                    shape.stopY = stopY;
                    shape.shapeType = shapeType;
                    shape.color = shapeColor;

                    if (shapeType == 5) shape.color = 4;

                    shapes.add(shape);
                    if (shapeType == 1 || shapeType == 5) {
                        touch_up();
                    }
                    isFinished = true;

                    this.invalidate();
                    break;
                } else {
                    touchedShape.startX = startX;
                    touchedShape.startY = startY;
                    touchedShape.stopX = stopX;
                    touchedShape.stopY = stopY;
                    touchedShape.shapeType = shapeType;
                    touchedShape.color = shapeColor;

                    shapes.add(touchedShape);
                }

        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint = new Paint();
        eraserpaint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        eraserpaint.setAntiAlias(true);
        eraserpaint.setStrokeWidth(70);
        eraserpaint.setStyle(Paint.Style.STROKE);
        eraserpaint.setColor(Color.WHITE);

        switch (shapeColor) {
            case 0:
                paint.setColor(Color.BLACK);
                break;
            case 1:
                paint.setColor(Color.RED);
                break;
            case 2:
                paint.setColor(Color.BLUE);
                break;
            case 3:
                paint.setColor(Color.GREEN);
                break;
            case 4:
                paint.setColor(Color.WHITE);
                break;
        }

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            switch (shape.shapeType) {
                case 1:
                    canvas.drawColor(shape.color);
                    canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
                    canvas.drawPath(path, paint);
                    break;
                case 2:
                    canvas.drawColor(shape.color);
                    canvas.drawLine(shape.startX, shape.startY,
                            shape.stopX, shape.stopY, paint);
                    break;
                case 3:
                    canvas.drawColor(shape.color);
                    int radius = (int) Math.sqrt(Math.pow(shape.stopX - shape.startX, 2)
                            + Math.pow(shape.stopY - shape.startY, 2));
                    canvas.drawCircle(shape.startX, shape.startY, radius, paint);
                    break;
                case 4:
                    canvas.drawColor(shape.color);
                    Rect rect = new Rect(shape.startX, shape.startY, shape.stopX,
                            shape.stopY);
                    canvas.drawRect(rect, paint);
                    Log.d("MyTag", String.valueOf(shape.color));
                    Log.d("MyTag", "사각형을 생성함");
                    break;
                case 5:
                    canvas.drawColor(shape.color);
                    canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
                    canvas.drawPath(path, eraserpaint);
                    Log.d("MyTag", String.valueOf(shape.color));
                    Log.d("MyTag", "지우개");
                    break;
            }

//            if (isFinished == false) {
//                paint.setColor(Color.RED);
//
//                switch (shapeType) {
//                    case 1:
//                        canvas.drawColor(shape.color);
//                        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
//                        canvas.drawPath(path, paint);
//                        break;
//                    case 2:
//                        canvas.drawColor(shape.color);
//                        canvas.drawLine(startX, startY, stopX, stopY, paint);
//                        break;
//                    case 3:
//                        canvas.drawColor(shape.color);
//                        int radius = (int) Math.sqrt(Math.pow(stopX - startX, 2)
//                                + Math.pow(stopY - startY, 2));
//                        canvas.drawCircle(startX, startY, radius, paint);
//                        break;
//                    case 4:
//                        canvas.drawColor(shape.color);
//                        Rect rect = new Rect(startX, startY, stopX, stopY);
//                        canvas.drawRect(rect, paint);
//                        Log.d("MyTag", String.valueOf(shape.color));
//                        break;
//                    case 5:
//                        paint.setColor(Color.WHITE);
//                        paint.setStrokeWidth(60);
//                        paint.setAntiAlias(true);
//                        canvas.drawLine(shape.startX, shape.startY,
//                                shape.stopX, shape.stopY, paint);
//                }
//            }
        }
    }

    private Shape obtainTouchedShape(int startX, int startY) {
        Shape touchedShape = getTouchedShape(startX, startY);

        if (null == touchedShape) {
            touchedShape = new Shape();

            //shapes.add(touchedShape);
        }

        return touchedShape;
    }

    private Shape getTouchedShape(final int xTouch, final int yTouch) {
        Shape touched = null;

        for (Shape shape : shapes) {
            if (xTouch >= shape.startX-70 && xTouch <= shape.startX +70 && yTouch >= shape.startY-70 && yTouch <= shape.startY+70) {
                touched = shape;
                shapes.remove(shape);
                Log.d("MyTag", "touch touch");
                break;
            }
        }

        return touched;
    }

    class Shape {

        int shapeType;
        int startX, startY, stopX, stopY;
        int color;
    }
}
