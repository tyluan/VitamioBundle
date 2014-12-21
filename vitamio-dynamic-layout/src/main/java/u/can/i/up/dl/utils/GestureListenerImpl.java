package u.can.i.up.dl.utils;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by lczgywzyy on 2014/12/19.
 */
public class GestureListenerImpl  extends GestureDetector.SimpleOnGestureListener{

    public GestureListenerImpl(){

    }

    /**
     * double click
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.i("GestureListenerImpl", "onDoubleTap");
        return true;
    }

    /**
     * single click
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        ViewUtils.getInstance().SingleTapConfirmed();
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        ViewUtils.getInstance().Fling(e1, e2, velocityX, velocityY);
        return true;
    }

    /**
     * move
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        ViewUtils.getInstance().ScrollScreen(e1, e2, distanceX, distanceY);
        return true;
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(boolean isUp, float percent) {
        ViewUtils.getInstance().VolumeSlide(isUp, percent);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(boolean isUp, float percent) {
        ViewUtils.getInstance().BrightnessSlide(isUp, percent);
    }

}
