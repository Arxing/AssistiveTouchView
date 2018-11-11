package org.arxing.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.sql.Time;
import java.util.Date;

public class ClockView extends View {
    private float cx;
    private float cy;
    private float radius;
    private float r;
    private Paint pFrame;
    private Paint pCenter;
    private Paint pNumber;
    private Paint pLineSecond;
    private Paint pLineMinute;
    private Paint pLineHour;
    private Rect rectText = new Rect();
    private int hour;
    private int minute;
    private int second;

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    private void initAttr(AttributeSet attrs) {

    }

    private void init() {
        pFrame = new Paint();
        pFrame.setColor(Color.BLACK);
        pFrame.setStrokeWidth(10);
        pFrame.setAntiAlias(true);
        pFrame.setStyle(Paint.Style.STROKE);

        pCenter = new Paint(pFrame);
        pCenter.setStyle(Paint.Style.FILL);

        pNumber = new Paint();
        pNumber.setColor(Color.BLACK);
        pNumber.setTextSize(50);
        pNumber.setAntiAlias(true);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int newWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        cx = cy = width / 2f;
        radius = width / 2f - 10;
        r = radius * 0.85f;
        super.onMeasure(newWidthSpec, widthMeasureSpec);
    }

    @Override protected void onDraw(Canvas canvas) {
        drawFrame(canvas);
        drawCenter(canvas);
        drawNumber(canvas);
    }

    private void drawFrame(Canvas canvas) {
        canvas.drawCircle(cx, cy, radius, pFrame);
    }

    private void drawCenter(Canvas canvas) {
        canvas.drawCircle(cx, cy, 10, pCenter);
    }

    private void drawNumber(Canvas canvas) {
        for (int i = 0; i < 12; i++) {
            String text = numberToText(i);
            pNumber.getTextBounds(text, 0, text.length(), rectText);
            float degree = i * 30;
            PointF point = PointSpace.getPoint(r, degree);
            point.offset(cx, cy);
            point.offset(-rectText.width() / 2f, rectText.height() / 2f);
            canvas.drawText(text, point.x, point.y, pNumber);
        }
    }

    private String numberToText(int number) {
        return number == 0 ? "12" : String.valueOf(number);
    }

}
