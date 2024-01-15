package com.ch.fishinglocation.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.amap.api.maps.Projection;
import com.amap.api.maps.model.LatLng;
import com.ch.fishinglocation.R;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private Paint paint;
    private Path path;
//    private List<LatLng> screenPoints = new ArrayList<>(); // 保存屏幕上的点
    private List<LatLng> points = new ArrayList<>(); // 保存屏幕上的点
    private Projection projection; // 高德地图投影对象
    private static final float TOUCH_TOLERANCE = 10; // 定义移动阈值
    private float mX, mY; // 记录上一次的坐标位置

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.black)); // 设置你的线条颜色
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }

    private void touchStart(float x, float y) {
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            if (projection != null) {
                LatLng latLng = projection.fromScreenLocation(new Point((int) x, (int) y));
                points.add(latLng); // 保存地图上的点
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(pointX, pointY);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                touchMove(pointX, pointY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // 当用户停止绘制时，可以在这里处理路径转换为坐标点的逻辑
//                convertToLatLngPoints(); // 用户完成绘制，将屏幕坐标转换为地图坐标
                break;
            default:
                return false;
        }

        // 通知view重绘
        postInvalidate();
        return true;
    }

    // 设置投影对象
    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    // 转换屏幕坐标到地图坐标
//    private void convertToLatLngPoints() {
//        if (projection != null) {
//            for (LatLng point : screenPoints) {
//                LatLng latLng = projection.fromScreenLocation(new android.graphics.Point((int) point.latitude, (int) point.longitude));
//                points.add(latLng);
//            }
//        }
//        screenPoints.clear(); // 清空屏幕坐标列表
//    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void clear() {
        path.reset();
        points.clear();
        invalidate();
    }
}

