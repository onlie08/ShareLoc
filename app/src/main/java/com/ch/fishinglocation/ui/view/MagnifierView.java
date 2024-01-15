package com.ch.fishinglocation.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

public class MagnifierView extends View {
    private Bitmap bitmap; // 地图的截图
    private Matrix matrix; // 用于放大
    private int magnifierDiameter; // 放大镜直径
    private float scaleFactor; // 放大倍数

    public MagnifierView(Context context, int magnifierDiameter, float scaleFactor) {
        super(context);
        this.magnifierDiameter = magnifierDiameter;
        this.scaleFactor = scaleFactor;
        matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            // 绘制放大镜的内容
            Bitmap scaledBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, magnifierDiameter, magnifierDiameter, matrix, true);
            canvas.drawBitmap(scaledBitmap, 0, 0, null);
            scaledBitmap.recycle();
        }
    }
}

