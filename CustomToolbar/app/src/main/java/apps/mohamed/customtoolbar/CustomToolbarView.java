package apps.mohamed.customtoolbar;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by Mohamed Elidrissi on 22/01/2018.
 */

public class CustomToolbarView extends View {

    private final float extendOverBoundary = 180F;
    private final float arcSize = 90F;
    private float cloud1_offset_x;
    private float cloud1_offset_y;
    private float cloud2_offset_x;
    private float cloud2_offset_y;
    private float cloud3_offset_x;
    private float cloud3_offset_y;
    private float sun_moon_offset_y;
    private float scale = 1.0F;
    private float timeScale = 0.0F;

    private float width = 0F;
    private float height = 0F;

    private boolean isNight = false;

    private Bitmap mSunMoonBitmap;
    private Bitmap mStarsBitmap;
    private Bitmap mCloud1, mCloud2, mCloud3;

    private ValueAnimator mValueAnimator = new ValueAnimator();
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    private Paint mBackgroundPaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint mOvalPaint = new Paint(ANTI_ALIAS_FLAG);
    private LinearGradient mGradient;

    private CustomListener mCustomListener;


    //Optional interface to show the values on the TextViews
    public interface CustomListener {
        void onValuesUpdated(boolean isNight, float timeScale);
    }

    //Since we are going to set this view in xml only we don't need other constructors.
    public CustomToolbarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initBitmaps();
        animateSky();
    }

    private void init(Context context) {
        mGradient = new LinearGradient(0, 0, 0, 0,
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorAccent),
                Shader.TileMode.CLAMP);
        mBackgroundPaint.setShader(mGradient);

        mOvalPaint.setStyle(Paint.Style.FILL);
        mOvalPaint.setColor(ContextCompat.getColor(context, R.color.colorWindowBackground));

        isNight();
    }

    private void initBitmaps() {
        Resources res = getResources();
        mCloud1 = BitmapFactory.decodeResource(res, R.drawable.bg_cloud_01);
        mCloud2 = BitmapFactory.decodeResource(res, R.drawable.bg_cloud_02);
        mCloud3 = BitmapFactory.decodeResource(res, R.drawable.bg_cloud_03);
        if (isNight) {
            mSunMoonBitmap = BitmapFactory.decodeResource(res, R.drawable.bg_moon);
            mStarsBitmap = BitmapFactory.decodeResource(res, R.drawable.bg_stars);
        } else {
            mSunMoonBitmap = BitmapFactory.decodeResource(res, R.drawable.bg_sun);
            mStarsBitmap = null;
        }

        //Init bitmaps
        cloud1_offset_x = res.getDimensionPixelSize(R.dimen.cloud1_offset_x);
        cloud1_offset_y = res.getDimensionPixelSize(R.dimen.cloud1_offset_y);
        cloud2_offset_x = res.getDimensionPixelSize(R.dimen.cloud2_offset_x);
        cloud2_offset_y = res.getDimensionPixelSize(R.dimen.cloud2_offset_y);
        cloud3_offset_x = res.getDimensionPixelSize(R.dimen.cloud3_offset_x);
        cloud3_offset_y = res.getDimensionPixelSize(R.dimen.cloud3_offset_y);
        sun_moon_offset_y = res.getDimensionPixelSize(R.dimen.sun_moon_offset_y);
    }

    private void animateSky() {
        mValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mValueAnimator.setFloatValues(0.0F, timeScale);
        mValueAnimator.setDuration(3000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                timeScale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateGradient();

        //Draws a rectangle for the background gradient.
        canvas.drawRect(0, 0, width, height, mBackgroundPaint);

        //if mStarsBitmap is null its day time so no need to draw stars.
        if (mStarsBitmap != null) {
            canvas.drawBitmap(mStarsBitmap,
                    (width / 2) - (mStarsBitmap.getWidth() / 2),
                    mStarsBitmap.getHeight() / 2 * scale,
                    null);
        }

        canvas.drawBitmap(mSunMoonBitmap,
                width * timeScale - mSunMoonBitmap.getWidth() / 2,
                (height / 2) - mSunMoonBitmap.getHeight() / 2,
                null);

        canvas.drawBitmap(mCloud1,
                (width * 0.10F) + cloud1_offset_x * (1 - scale),
                (height * 0.60F) + cloud1_offset_y * (1 - scale),
                null);

        canvas.drawBitmap(mCloud2,
                (width / 2 - mCloud2.getWidth() / 2) + cloud2_offset_x * (1 - scale),
                (height * 0.50F) + cloud2_offset_y * (1 - scale),
                null);

        canvas.drawBitmap(mCloud3,
                (width * 0.90F - mCloud3.getWidth() / 2) + cloud3_offset_x * (1 - scale),
                (height * 0.60F) + cloud3_offset_y * (1 - scale),
                null);

        //Draws an oval on top of the view to simulate an Arc
        canvas.drawOval((-extendOverBoundary),
                height - arcSize * scale,
                width + extendOverBoundary,
                height + arcSize * scale,
                mOvalPaint);

        mCustomListener.onValuesUpdated(isNight, timeScale);
    }

    //This method updates the scale and refreshes the view.
    public void setScale(float scale) {
        this.scale = scale;
        invalidate();
    }

    public void setCustomListener(CustomListener listener) {
        mCustomListener = listener;
    }

    private void updateGradient() {
        if (scale > 0.0F) {
            mGradient = new LinearGradient(0, 0,
                    width * scale,
                    height * scale,
                    calculateColor1(),
                    calculateColor2(),
                    Shader.TileMode.CLAMP);
            mBackgroundPaint.setShader(mGradient);
        }
    }

    //This method checks whether its night or not.
    private void isNight() {
        Date date = new Date(System.currentTimeMillis());
        //We use 'kk' pattern to get hours in 24 hour format
        SimpleDateFormat dateFormat = new SimpleDateFormat("kk", Locale.US);
        int currentHour = Integer.parseInt(dateFormat.format(date));

        //if the current hour is greater than 6 and less than 20, it means that its day time.
        isNight = !(currentHour >= 6 && currentHour <= 20);
        calculateTimeScale(isNight, currentHour);
    }

    /**
     * This method calculates the time scale which is a value between 0.0 & 1.0,
     * which basically decides where the sun or moon will be depending on the time.
     **/
    private void calculateTimeScale(boolean isNight, int currentHour) {
        float totalDayHours = 14F;
        float totalNightHours = 10F;

        if (isNight) {
            if (currentHour < 20) {
                timeScale = (float) (currentHour + 4) / totalNightHours;
            } else {
                timeScale = (float) (currentHour - 20) / totalNightHours;
            }
        } else {
            timeScale = (float) (currentHour - 6) / totalDayHours;
        }
    }


    /**
     * The following methods use ArgbEvaluator to create smooth transitions between the colors,
     **/

    private int calculateColor1() {
        return (int) mArgbEvaluator.evaluate(scale,
                ContextCompat.getColor(getContext(), R.color.color_gradient_noon_1),
                calculateColor1Base());
    }

    private int calculateColor2() {
        return (int) mArgbEvaluator.evaluate(scale,
                ContextCompat.getColor(getContext(), R.color.color_gradient_noon_2),
                calculateColor2Base());
    }

    private int calculateColor1Base() {
        if (isNight) {
            if (timeScale <= 0.25F) {
                return (int) mArgbEvaluator.evaluate(timeScale * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_evening_1),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_1));
            } else if (timeScale > 0.25F && timeScale < 0.75F) {
                return (int) mArgbEvaluator.evaluate((timeScale - 0.25F) * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_1),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_1));
            } else {
                return (int) mArgbEvaluator.evaluate((timeScale - 0.75F) * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_1),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_morning_1));
            }
        } else {
            if (timeScale <= 0.5F) {
                return (int) mArgbEvaluator.evaluate(timeScale * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_morning_1),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_noon_1));
            } else if (timeScale > 0.5F && timeScale <= 0.75) {
                return (int) mArgbEvaluator.evaluate((timeScale - 0.5F) * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_noon_1),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_evening_1));
            } else {
                return (int) mArgbEvaluator.evaluate((timeScale - 0.75F) * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_evening_1),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_1));
            }
        }
    }

    private int calculateColor2Base() {
        if (isNight) {
            if (timeScale <= 0.25F) {
                return (int) mArgbEvaluator.evaluate(timeScale * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_evening_2),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_2));
            } else if (timeScale > 0.25F && timeScale < 0.75F) {
                return (int) mArgbEvaluator.evaluate((timeScale - 0.25F) * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_2),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_2));
            } else {
                return (int) mArgbEvaluator.evaluate((timeScale - 0.75F) * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_2),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_morning_2));
            }
        } else {
            if (timeScale <= 0.5F) {
                return (int) mArgbEvaluator.evaluate(timeScale * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_morning_2),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_noon_2));
            } else if (timeScale > 0.5F && timeScale <= 0.75) {
                return (int) mArgbEvaluator.evaluate((timeScale - 0.5F) * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_noon_2),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_evening_2));
            } else {
                return (int) mArgbEvaluator.evaluate((timeScale - 0.75F) * 2,
                        ContextCompat.getColor(getContext(), R.color.color_gradient_evening_2),
                        ContextCompat.getColor(getContext(), R.color.color_gradient_night_2));
            }
        }
    }

}