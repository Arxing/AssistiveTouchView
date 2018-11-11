package org.arxing.library;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class AssistiveGestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override public boolean onSingleTapUp(MotionEvent e) {
        Log.d("tag", String.format("onSingleTagUp()"));
        return super.onSingleTapUp(e);
    }

    @Override public void onLongPress(MotionEvent e) {
        Log.d("tag", String.format("onLongPress()"));
        super.onLongPress(e);
    }

    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("tag", String.format("onScroll()"));
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("tag", String.format("onFling()"));
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override public void onShowPress(MotionEvent e) {
        Log.d("tag", String.format("onShowPress()"));
        super.onShowPress(e);
    }

    @Override public boolean onDown(MotionEvent e) {
        Log.d("tag", String.format("onDown()"));
        return super.onDown(e);
    }

    @Override public boolean onDoubleTap(MotionEvent e) {
        Log.d("tag", String.format("onDoubleTap()"));
        return super.onDoubleTap(e);
    }

    @Override public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d("tag", String.format("onDoubleTapEvent()"));
        return super.onDoubleTapEvent(e);
    }

    @Override public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d("tag", String.format("onSingleTapConfirmed()"));
        return super.onSingleTapConfirmed(e);
    }
}
