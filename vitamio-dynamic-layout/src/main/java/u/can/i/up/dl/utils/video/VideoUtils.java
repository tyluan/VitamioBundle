package u.can.i.up.dl.utils.video;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import u.can.i.up.dl.R;
import u.can.i.up.dl.utils.Variables;

/**
 * Created by lczgywzyy on 2014/12/18.
 */
public class VideoUtils {

    private static final VideoUtils mViewUtils = new VideoUtils();

    private VideoView mVideoView = null;
    private int mPlayType = -1;
    private Activity mActivity = null;
    private RelativeLayout mLayout = null;
    private ImageView mControlView;
    private ProgressBar mProgressBar;
    private ImageView mArrayView;
    private TextView mTextView;
    private AudioManager mAudioManager;
    private int mMaxVolume;//最大声音
    private int mVolume = -1;//当前声音
    private float mBrightness = -1f;//当前亮度

    private boolean mScrollScreenFlag = false;

    private int currMoveMode = Variables.MODE_NONE;

    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("mDismissHandler", "hide view");
            currMoveMode = Variables.MODE_NONE;
            if (mVideoView.isPlaying()) {
                mControlView.setVisibility(View.GONE);
            } else {
                mControlView.setImageResource(R.drawable.video_play_1);
            }
            mProgressBar.setVisibility(View.GONE);
            mArrayView.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
        }
    };

    private VideoUtils() {
    }

    /**
     * @param
     * @return mViewUtils的实例
     */
    public static VideoUtils getInstance() {
        return mViewUtils;
    }

    /**
     * @param
     * @return mVideoView实例
     */
    public VideoView getVideoView() {
        return getInstance().mVideoView;
    }

    /**
     * @param
     * @return mControlView
     * 返回ImageView类型的mControlView
     */
    public ImageView getControlView() {
        return getInstance().mControlView;
    }

    /**
     * @return mScrollScreenFlag
     * 判断是不是划屏操作；
     * 中间变量！
     */
    public boolean isScrollScreen() {
        return mScrollScreenFlag;
    }

    private void setScrollScreenFlag(boolean flag) {
        mScrollScreenFlag = flag;
    }

    /**
     * @param mLayout 在指定LinearLayout界面中生成动态布局
     * @deprecated
     */
    private void createVideoView(LinearLayout mLayout) {
        if (!LibsChecker.checkVitamioLibs(mActivity))
            return;
        mLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        CenterLayout.LayoutParams clp = new CenterLayout.LayoutParams(vlp);
        mVideoView = new VideoView(mActivity);
        mVideoView.setLayoutParams(vlp);
        mLayout.addView(mVideoView);
//        mVideoView.setMediaController(new MediaController(mActivity));
        mVideoView.requestFocus();
//        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
                updateVideoView();
            }
        });
        mVideoView.setOnTouchListener(new VideoViewTouchListener(mActivity));
//        mGestureDetector = new GestureDetector(mActivity, new GestureListenerImpl());
    }

    /**
     * @param mLayout 在指定RelativeLayout界面中生成动态布局
     */
    private void createVideoView(RelativeLayout mLayout) {
        if (!LibsChecker.checkVitamioLibs(mActivity))
            return;
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        CenterLayout.LayoutParams clp = new CenterLayout.LayoutParams(vlp);
        mVideoView = new VideoView(mActivity);
        mVideoView.setLayoutParams(vlp);
        mLayout.addView(mVideoView);
//        mVideoView.setMediaController(new MediaController(mActivity));
        mVideoView.requestFocus();
//        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
                updateVideoView();
            }
        });
        baseInit();
    }

    /*相关变量的初始化
    * */
    private void baseInit() {
        mVideoView.setOnTouchListener(new VideoViewTouchListener(mActivity));
        mControlView = createControlView();
        mProgressBar = createProcessBar();
        mArrayView = createArrowView();
        mTextView = createTextView();
    }

    private ImageView createControlView() {
        ImageView controlView = new ImageView(mActivity);
        RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        viewLayoutParams.height = 200;
        viewLayoutParams.width = 200;
        controlView.setLayoutParams(viewLayoutParams);
//        controlView.setId(childViewIdTag++);
        mLayout.addView(controlView);
        controlView.setVisibility(View.GONE);
        Log.i("createControlView", "add image view : " + controlView.getId());
        return controlView;
    }

    private ProgressBar createProcessBar() {
        /** init progress bar */
        ProgressBar progressBar = new ProgressBar(mActivity, null, android.R.attr.progressBarStyleHorizontal);
        RelativeLayout.LayoutParams barLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        barLayoutParams.addRule(RelativeLayout.BELOW, mControlView.getId());
        barLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //     barLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        //     barLayoutParams.height = 200;
        barLayoutParams.width = 200;
        progressBar.setLayoutParams(barLayoutParams);
//        progressBar.setId(childViewIdTag++);
        mLayout.addView(progressBar);
        //       videoLayout.bringChildToFront(imageView);
        progressBar.setVisibility(View.GONE);
        Log.i("createProcessBar", "add progress bar : " + progressBar.getId());
        return progressBar;
    }

    private ImageView createArrowView() {
        /** init control view */
        ImageView arrowView = new ImageView(mActivity);
        RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        viewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        viewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        // viewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        viewLayoutParams.height = 50;
        viewLayoutParams.width = 50;
        viewLayoutParams.bottomMargin = 50;
        arrowView.setLayoutParams(viewLayoutParams);
//        arrowView.setId(childViewIdTag++);
        mLayout.addView(arrowView);
        arrowView.setVisibility(View.GONE);
        Log.i("createArrowView", "add arrow view : " + arrowView.getId());
        return arrowView;
    }

    private TextView createTextView() {
        /** init text view*/
        TextView textView = new TextView(mActivity);
        RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // viewLayoutParams.addRule(RelativeLayout.BELOW, arrowView.getId());
        //    viewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        viewLayoutParams.addRule(RelativeLayout.RIGHT_OF, mArrayView.getId());
        viewLayoutParams.addRule(RelativeLayout.ALIGN_TOP, mArrayView.getId());
        //    viewLayoutParams.bottomMargin = 50;
        viewLayoutParams.height = 50;
        textView.setTextColor(0xff000000);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.0f);
        textView.setLayoutParams(viewLayoutParams);
//        textView.setId(childViewIdTag++);
        mLayout.addView(textView);
        textView.setVisibility(View.GONE);
        Log.i("createTextView", "add text view : " + textView.getId());
        return textView;
    }

    private void updateVideoView() {
        int videoHight = mVideoView.getVideoHeight();
        int videoWidth = mVideoView.getVideoWidth();
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenHeight = metric.heightPixels;   // 屏幕高度（像素）
        int screenWidth = metric.widthPixels;     // 屏幕宽度（像素）

        switch (mPlayType) {
            case Variables.VIDEO_NAIVE_WITH_CONTROLLER:
                mVideoView.setMediaController(new MediaController(mActivity));
            case Variables.VIDEO_NAIVE_WITHOUT_CONTROLLER:
                break;
            case Variables.VIDEO_CENTER_WITH_CONTROLLER:
                mVideoView.setMediaController(new MediaController(mActivity));
            case Variables.VIDEO_CENTER_WITHOUT_CONTROLLER:
//                Log.i("=====VideoHeight======", "" + videoHight);
//                Log.i("=====VideoWidth======", "" + videoWidth);
//                Log.i("=====ScreenHeight======", "" + screenHeight);
//                Log.i("=====ScreenWidth======", "" + screenWidth);
                if (videoHight < screenHeight * videoWidth / screenWidth)
                    mVideoView.setY((screenHeight - videoHight) / 2);
//                if (videoWidth < screenWidth * videoHight / screenHeight)
//                    mVideoView.setX((screenWidth - videoWidth)/2);
                break;
            default:
                break;
        }
    }

    /**
     * @param path 视频的本地路径
     * @see Variables;
     */
    private void playVideo(String path) {
        mVideoView.setVideoPath(path);
    }

    /**
     * @param tmpActivity 指定播放视频所在的界面
     * @param mLayout     在指定界面中生成动态布局，播放时默认布局
     * @param path        视频的本地路径
     *                    <p/>
     *                    在指定页面中生成唯一动态布局，并且在该布局中播放视频
     */
    public void playVideoInVideoView(Activity tmpActivity, LinearLayout mLayout, String path) {
        playVideoInVideoView(tmpActivity, mLayout, path, Variables.VIDEO_NAIVE_WITH_CONTROLLER);
    }

    /**
     * @param tmpActivity 指定播放视频所在的界面
     * @param ll          在指定界面中生成动态布局
     * @param path        视频的本地路径
     * @param playType    播放的形式，默认布局还是居中布局等等。
     * @see Variables
     * <p/>
     * 在指定页面中生成唯一动态布局，并且在该布局中播放视频
     */
    public void playVideoInVideoView(Activity tmpActivity, LinearLayout ll, String path, int playType) {
        mActivity = tmpActivity;
        mPlayType = playType;
        createVideoView(mLayout);
        playVideo(path);
    }

    /**
     * @param tmpActivity 指定播放视频所在的界面
     * @param ml          在指定界面中生成动态布局
     * @param path        视频的本地路径
     * @param playType    播放的形式，默认布局还是居中布局等等。
     * @see Variables
     * <p/>
     * 在指定页面中生成唯一动态布局，并且在该布局中播放视频
     */
    public void playVideoInVideoView(Activity tmpActivity, RelativeLayout ml, String path, int playType) {
        mActivity = tmpActivity;
        mPlayType = playType;
        mLayout = ml;
        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        createVideoView(mLayout);
        playVideo(path);
    }

    /**
     * GestureListenerImpl实例的单击处理函数；
     */
    public void SingleTapConfirmed() {
        setScrollScreenFlag(false);
        mControlView.setImageResource(R.drawable.video_play_1);
        if (mVideoView.isPlaying()) {
            Log.i("item id ", "" + mControlView.getId());
            mVideoView.pause();
            mControlView.setVisibility(View.VISIBLE);
        } else {
            mVideoView.start();
            mControlView.setVisibility(View.GONE);
        }
        Log.i("GestureListenerImpl", "onSingleTapConfirmed");
    }

    /**
     * GestureListenerImpl实例的滑动处理函数；
     */
    public void Fling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        setScrollScreenFlag(true);
        if (currMoveMode == Variables.MODE_PROGRESS) {
            long addTime = 15000;
            long deleteTime = 15000;
            int mOldX = (int) e1.getX(), mOldY = (int) e1.getY();
            int mNewX = (int) e2.getX(), mNewY = (int) e2.getY();
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                int windowWidth = mVideoView.getWidth();
                //     if (mOldX < windowWidth * 3.5 / 5 && mOldX > windowWidth * 1.5 / 5) {
                if (mNewX - mOldX > 0) {
                    mArrayView.setImageResource(R.drawable.arrow_1);
                    mArrayView.setVisibility(View.VISIBLE);
                    mTextView.setText((addTime / 1000) + "s");
                    mTextView.setVisibility(View.VISIBLE);
                    Log.i("Fling", "seek request +");
                    mVideoView.seekTo(mVideoView.getCurrentPosition() + addTime);
                } else {
                    mArrayView.setImageResource(R.drawable.arrow_2);
                    mArrayView.setVisibility(View.VISIBLE);
                    mTextView.setText((deleteTime / 1000) + "s");
                    mTextView.setVisibility(View.VISIBLE);
                    Log.i("Fling", "seek request -");
                    mVideoView.seekTo(mVideoView.getCurrentPosition() - deleteTime);
                }
            }

            //}
            currMoveMode = Variables.MODE_NONE;
        }
        Log.i("Fling", "action : fling");
    }

    /**
     * GestureListenerImpl实例的消息处理函数；
     */
    public void endGesture() {
        Log.i("endGesture", "end gesture");
        mVolume = -1;
        mBrightness = -1f;

        // hide view
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 1000);
        //   seekVedioRequest = 0;
        setScrollScreenFlag(false);
    }

    /**
     * GestureListenerImpl实例的划屏处理函数；
     */
    public void ScrollScreen(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        setScrollScreenFlag(true);
        float mOldX = e1.getX(), mOldY = e1.getY();
        int mNewX = (int) e2.getX(), mNewY = (int) e2.getY();
        //   float rawDistanceX = mOldX - mNewX;
        float rawDistanceY = mOldY - mNewY;
        int windowHeight = mVideoView.getHeight();
        if (currMoveMode == Variables.MODE_VOLUME) {
            //if (mOldX > windowWidth * 4.0 / 5) {// move at right
            //onVolumeSlide((distanceY) / screenHeight);
            VolumeSlide(distanceY > 0, (rawDistanceY) / windowHeight);
        } else if (currMoveMode == Variables.MODE_BRIGHTNESS) {
            //else if (mOldX < windowWidth / 5.0) {// move at left
            BrightnessSlide(distanceY > 0, (rawDistanceY) / windowHeight);
        }
    }

    /**
     * GestureListenerImpl实例的声音处理函数；
     */
    public void VolumeSlide(boolean isUp, float percent) {
        if ((isUp && percent > 0) || (!isUp && percent < 0)) {
            if (percent > 0) {
                Log.i("onVolumeSlide", "volume + " + percent);
                mControlView.setImageResource(R.drawable.volume_up_1);
            } else {
                Log.i("onVolumeSlide", "volume - " + percent);
                mControlView.setImageResource(R.drawable.volume_down_1);
            }
            mControlView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            if (mVolume < 0) {
                mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (mVolume < 0)
                    mVolume = 0;
            }
            float index = (percent * mMaxVolume) + mVolume;
            if (index > mMaxVolume) {
                index = mMaxVolume;
            } else if (index < 0) {
                index = 0;
            }
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
            mProgressBar.setProgress((int) ((index / mMaxVolume) * 100));
        } else {
            Log.i("onVolumeSlide", "invalid scroll : " + percent);
        }
    }

    /**
     * GestureListenerImpl实例的亮度处理函数；
     *
     * @param isUp
     * @param percent
     */
    public void BrightnessSlide(boolean isUp, float percent) {
        if ((isUp && percent > 0) || (!isUp && percent < 0)) {
            mControlView.setImageResource(R.drawable.brightness_1);
            mControlView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            if (mBrightness < 0) {
                mBrightness = mActivity.getWindow().getAttributes().screenBrightness;
                if (mBrightness <= 0.00f)
                    mBrightness = 0.50f;
                if (mBrightness < 0.01f)
                    mBrightness = 0.01f;
            }
            WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
            lpa.screenBrightness = mBrightness + percent;
            if (lpa.screenBrightness > 1.0f)
                lpa.screenBrightness = 1.0f;
            else if (lpa.screenBrightness < 0.01f)
                lpa.screenBrightness = 0.01f;
            mActivity.getWindow().setAttributes(lpa);
            mProgressBar.setProgress((int) (lpa.screenBrightness * 100));
        }
    }

    public void setCurrMoveMode(int newMoveMode) {
        this.currMoveMode = newMoveMode;
    }

    public int getCurrMoveMode() {
        return this.currMoveMode;
    }

    public float getVideoViewWidth() {
        return this.mVideoView.getWidth();
    }
}
