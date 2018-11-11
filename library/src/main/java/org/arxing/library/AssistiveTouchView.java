package org.arxing.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AssistiveTouchView extends FrameLayout implements View.OnTouchListener {
    private AssistiveTouchView self = this;
    private ImageView imageView;
    private GestureDetector gestureDetector;
    private ViewGroup parent;
    private ImageView child1;
    private ImageView child2;
    private ImageView child3;
    private AssistiveGestureListener assistiveGestureListener;

    private float child1ExpandDegree;
    private float child2ExpandDegree;
    private float child3ExpandDegree;
    private float r = 300;
    private boolean isCollapsed = true;
    private int childPadding = 15;
    private OnChildClickListener listener;

    public AssistiveTouchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    private void initAttr(AttributeSet attrs) {

    }

    private ImageView buildImageView(int padding) {
        ImageView imageView = new ImageView(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(padding, padding, padding, padding);
        return imageView;
    }

    private void init() {
        imageView = buildImageView(0);
        imageView.setImageResource(R.drawable.ic_main);

        child1 = buildImageView(childPadding);
        child2 = buildImageView(childPadding);
        child3 = buildImageView(childPadding);

        child1.setImageResource(R.drawable.ic_qq);
        child2.setImageResource(R.drawable.ic_service);
        child3.setImageResource(R.drawable.ic_wechat);

        addView(child1);
        addView(child2);
        addView(child3);
        addView(imageView);

        child1.setAlpha(0f);
        child2.setAlpha(0f);
        child3.setAlpha(0f);

        setOnTouchListener(this);
        setClickable(true);
        setLongClickable(true);
        setClipChildren(false);

        gestureDetector = new GestureDetector(getContext(), assistiveGestureListener = new AssistiveGestureListener());
    }

    @Override public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        parent = ((ViewGroup) getParent());
        parent.setClipChildren(false);
    }

    private void triggeEvent(View view, int index) {
        if (listener != null)
            listener.onClick(view, index);
    }

    private PointF getPoint(float x, float y, float r, float degree) {
        PointF point = PointSpace.getPoint(r, degree);
        point.offset(x, y);
        return point;
    }

    private void toggleChildCollapsed() {
        isCollapsed = !isCollapsed;
        setChildCollapsed(isCollapsed);
        assistiveGestureListener.canScroll = isCollapsed;
    }

    private void setChildCollapsed(boolean collapsed) {
        if (collapsed) {
            collapseChild(child1ExpandDegree, child1);
            collapseChild(child2ExpandDegree, child2);
            collapseChild(child3ExpandDegree, child3);
        } else {
            expandChild(child1ExpandDegree, child1);
            expandChild(child2ExpandDegree, child2);
            expandChild(child3ExpandDegree, child3);
        }
    }

    private void expandChild(float degree, View child) {
        PointF point = getPoint(0, 0, r, degree);
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("x", 0, point.x);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("y", 0, point.y);
        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("alpha", child.getAlpha(), 1);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(child, holderX, holderY, holderAlpha);
        animator.setDuration(250);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                child1.setOnClickListener(v -> triggeEvent(v, 1));
                child2.setOnClickListener(v -> triggeEvent(v, 2));
                child3.setOnClickListener(v -> triggeEvent(v, 3));
            }
        });
    }

    private void collapseChild(float degree, View child) {
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("x", child.getX(), 0);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("y", child.getY(), 0);
        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("alpha", child.getAlpha(), 0);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(child, holderX, holderY, holderAlpha);
        animator.setDuration(250);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                child1.setOnClickListener(null);
                child2.setOnClickListener(null);
                child3.setOnClickListener(null);
            }
        });
    }

    public void setOnChildClickListener(OnChildClickListener l) {
        this.listener = l;
    }

    class AssistiveGestureListener extends GestureDetector.SimpleOnGestureListener {
        private float preScrollX;
        private float preScrollY;
        private long backTime = 800;
        private long animDuration = 300;
        private Handler handler = new Handler();
        private boolean floatingLeft = true;
        private boolean floatingTop = true;
        private boolean canExpandChild = true;
        private boolean canScroll = true;

        AssistiveGestureListener() {
            updateChildDegrees();
        }

        private boolean isTouchSelf(float x, float y) {
            return x > getLeft() && x < getRight() && y > getTop() && y < getBottom();
        }

        private boolean canSetX(float x) {
            return x >= parent.getLeft() && x + getWidth() <= parent.getRight();
        }

        private boolean canSetY(float y) {
            return y >= parent.getTop() && y + getHeight() <= parent.getBottom();
        }

        private @Size(2) float[] operateBackPoint(float x, float y) {
            float resultX;
            float resultY;
            float centerX = x + self.getWidth() / 2f;
            float centerY = y + self.getHeight() / 2f;
            float centerParentX = parent.getWidth() / 2f;
            float centerParentY = parent.getHeight() / 2f;
            floatingLeft = centerX < centerParentX;
            floatingTop = centerY < centerParentY;
            resultX = floatingLeft ? 0 : parent.getRight() - self.getWidth();
            resultY = y;
            return new float[]{resultX, resultY};
        }

        @Override public boolean onSingleTapUp(MotionEvent e) {
            if (canExpandChild)
                toggleChildCollapsed();
            return true;
        }

        @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!canScroll)
                return false;
            distanceX = preScrollX == 0 ? 0 : e2.getRawX() - preScrollX;
            distanceY = preScrollY == 0 ? 0 : e2.getRawY() - preScrollY;
            float newX = getX() + distanceX;
            float newY = getY() + distanceY;
            if (canSetX(newX))
                setX(newX);
            if (canSetY(newY))
                setY(newY);
            preScrollX = e2.getRawX();
            preScrollY = e2.getRawY();
            handler.removeCallbacks(timeBack);
            handler.postDelayed(timeBack, backTime);
            canExpandChild = false;
            return true;
        }

        @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            handler.removeCallbacks(timeBack);
            back();
            updateChildDegrees();
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override public boolean onDown(MotionEvent e) {
            preScrollX = 0;
            preScrollY = 0;
            return true;
        }

        private void back() {
            float[] newXY = operateBackPoint(getX(), getY());
            PropertyValuesHolder xHolder = PropertyValuesHolder.ofFloat("x", getX(), newXY[0]);
            PropertyValuesHolder yHolder = PropertyValuesHolder.ofFloat("y", getY(), newXY[1]);
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(self, xHolder, yHolder);
            animator.setDuration(animDuration);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    canExpandChild = true;
                }

                @Override public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    canExpandChild = true;
                }
            });
        }

        private void updateChildDegrees() {
            float offset = 8;
            if (floatingLeft && floatingTop) {
                //左上
                child1ExpandDegree = 90 + offset;
                child2ExpandDegree = 135;
                child3ExpandDegree = 180 - offset;
            } else if (floatingLeft && !floatingTop) {
                //左下
                child1ExpandDegree = 0 + offset;
                child2ExpandDegree = 45;
                child3ExpandDegree = 90 - offset;
            } else if (!floatingLeft && floatingTop) {
                //右上
                child1ExpandDegree = 270 - offset;
                child2ExpandDegree = 225;
                child3ExpandDegree = 180 + offset;
            } else if (!floatingLeft && !floatingTop) {
                //右下
                child1ExpandDegree = 360 - offset;
                child2ExpandDegree = 315;
                child3ExpandDegree = 270 + offset;
            }
        }

        Runnable timeBack = () -> {
            back();
            updateChildDegrees();
        };
    }

    public interface OnChildClickListener {
        void onClick(View v, int index);
    }
}
