package com.ft.imdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

/**
 * @author LDL
 * @date: 2021/11/17
 * @description:
 */
public class DragLayout extends FrameLayout {

    private static final String TAG = "DragLayout";

    private ViewDragHelper mDragHelper;
    private int mCurrentTop;
    private int mCurrentLeft;

    public DragLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public DragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
//                mDragOriLeft = child.getLeft();
//                mDragOriTop = child.getTop();
                Log.d(TAG, "tryCaptureView, left=" + child.getLeft() + "; top=" + child.getTop());
                return true;
            }
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                Log.d(TAG, "left=" + left + "; dx=" + dx);
                // 最小 x 坐标值不能小于 leftBound
                final int leftBound = getPaddingLeft();
                // 最大 x 坐标值不能大于 rightBound
                final int rightBound = getWidth() - child.getWidth() - getPaddingRight();
                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                mCurrentLeft = newLeft;
                return newLeft;
            }
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                Log.d(TAG, "top=" + top + "; dy=" + dy);
                // 最小 y 坐标值不能小于 topBound
                final int topBound = getPaddingTop();
                // 最大 y 坐标值不能大于 bottomBound
                final int bottomBound = getHeight() - child.getHeight() - getPaddingBottom();
                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
                mCurrentTop = newTop;
                return newTop;
            }
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                Log.d(TAG, "onViewReleased, xvel=" + xvel + "; yvel=" + yvel);
                int childWidth = releasedChild.getWidth();
                int parentWidth = getWidth();
                int leftBound = getPaddingLeft();// 左边缘
                int rightBound = getWidth() - releasedChild.getWidth() - getPaddingRight();// 右边缘
                // 方块的中点超过 ViewGroup 的中点时，滑动到左边缘，否则滑动到右边缘
                if ((childWidth / 2 + mCurrentLeft) < parentWidth / 2) {
                    mDragHelper.settleCapturedViewAt(leftBound, mCurrentTop);
                } else {
                    mDragHelper.settleCapturedViewAt(rightBound, mCurrentTop);
                }
                invalidate();
            }
        };
        mDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

}
