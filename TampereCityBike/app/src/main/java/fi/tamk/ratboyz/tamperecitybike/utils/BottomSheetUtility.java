package fi.tamk.ratboyz.tamperecitybike.utils;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import fi.tamk.ratboyz.tamperecitybike.R;


public class BottomSheetUtility extends BottomSheetBehavior.BottomSheetCallback {
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_STATIONARY = 0;
    public static final int DIRECTION_DOWN = -1;

    private float mLastOffset;
    private int mState;
    private int mMovement;
    private BottomSheetBehavior behavior;
    private AppCompatActivity activity;
    private int mPeekHeight;
    private ValueAnimator mAnimator;
    private int mDuration;
    private ActionBar appBar;
    private View bottomSheetContainer;
    private AnimationState mAnimState = AnimationState.HIDDEN;

    public BottomSheetUtility(BottomSheetBehavior behavior, AppCompatActivity activity) {
        this.behavior = behavior;
        this.activity = activity;
        this.appBar = activity.getSupportActionBar();
        this.mPeekHeight = (int) activity.getResources().getDimension(R.dimen.bottom_sheet_peek_height);
        this.bottomSheetContainer = activity.findViewById(R.id.bottomSheetContainer);
        // TODO reference android constants
        mDuration = 150;
        mAnimator = ValueAnimator.ofInt(0, mPeekHeight);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(mDuration);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                BottomSheetUtility.this.behavior.setPeekHeight(value);
            }
        });
    }


    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        this.mState = newState;

        switch (newState) {
            case BottomSheetBehavior.STATE_DRAGGING:
                //Log.d("Tag", "dragging");
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
                //Log.d("Tag", "collapsed");
                break;
            case BottomSheetBehavior.STATE_EXPANDED:
                //Log.d("Tag", "expanded");
                break;
            case BottomSheetBehavior.STATE_HIDDEN:
                //Log.d("Tag", "hidden");
                break;
            case BottomSheetBehavior.STATE_SETTLING:
                //Log.d("Tag", "settling");
                break;
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        //Log.d("Tag", "offset: " + slideOffset);
        if (mState == BottomSheetBehavior.STATE_COLLAPSED
                || mState == BottomSheetBehavior.STATE_EXPANDED
                || mState == BottomSheetBehavior.STATE_HIDDEN) {
            mMovement = DIRECTION_STATIONARY;
        } else {
            mMovement = (slideOffset > mLastOffset)
                    ? DIRECTION_UP
                    : DIRECTION_DOWN;
        }

        mLastOffset = slideOffset;
        appBar.hide();
    }


    public boolean shouldConsumeBackPressedEvent() {
        Log.d("TAG", behavior.getState() + "");
        // TODO Bottomsheet animation restarts on rapid back presses.
        switch (behavior.getState()) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                if (behavior.getPeekHeight() == 0) {
                    //Log.d("TAG", "collapsed false");
                    return false;
                } else {
                    hideBottomSheet();
                    return true;
                }
            case BottomSheetBehavior.STATE_EXPANDED:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                //Log.d("TAG", "expanded true");
                return true;
            case BottomSheetBehavior.STATE_DRAGGING:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                //Log.d("TAG", "dragging true");
                return true;
            case BottomSheetBehavior.STATE_SETTLING:
                if (movementDirection() == BottomSheetUtility.DIRECTION_UP) {
                    appBar.hide();
                } else {
                    hideBottomSheet();
                }
                return true;
            default:
                return false;
        }

    }

    public int movementDirection() {
        return mMovement;
    }

    public void showBottomSheet() {
        if (mAnimState != AnimationState.SHOWN){
            mAnimState = AnimationState.SHOWN;
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mAnimator.setIntValues(0, mPeekHeight);
            mAnimator.start();
        }
    }

    public void hideBottomSheet() {
        if(mAnimState != AnimationState.HIDDEN) {
            mAnimState = AnimationState.HIDDEN;
            mAnimator.setIntValues(mPeekHeight, 0);
            mAnimator.start();
        }
    }

    private enum AnimationState {
        SHOWN,
        HIDDEN,
    }
}
