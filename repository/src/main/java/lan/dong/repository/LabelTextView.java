package lan.dong.repository;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by 梁桂栋 on 16-10-31 ： 下午11:42.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class LabelTextView extends android.support.v7.widget.AppCompatTextView {

    private int shadowColor;
    private int strokeWidth;
    private int bgColor;
    private float roundRadius = 0;
    private boolean clickAnimation = true;
    private Paint bgPaint;
    private Paint loadingPaint;
    private float shadowRadius = 0;
    private int shadowDx;
    private int shadowDy;
    private RectF bgRect;
    private RectF loadingRect;
    private boolean needInnerCircle = false;
    private int startColor;
    private int centerColor;
    private int endColor;
    private int startAngle = 0;
    private int sweepAngle = 20;
    private int loadingPadding = 10;
    private int innerR = 1;
    private boolean flag = true;
    private int sweepStroke = 8;
    private LinearGradient gradient;
    private int textColor;
    private String gradientType = "";
    private int scaleStep = 10;
    private static final int STATE_NONE = 0;
    private static final int STATE_LOADING = 2;
    private static final int STATE_PREPARE = 1;
    private static final int STATE_BEFORE_FINISH = 3;
    private int state;
    private int finishCount = 0;

    private boolean refresh = false;

    public LabelTextView(Context context) {
        this(context, null);
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bgColor = 0xFF4CAF50;
        shadowColor = 0XFF777777;
        strokeWidth = 0;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LabelTextView);
            bgColor = ta.getColor(R.styleable.LabelTextView_bg_color, bgColor);
            roundRadius = ta.getDimension(R.styleable.LabelTextView_radius, roundRadius);
            clickAnimation = ta.getBoolean(R.styleable.LabelTextView_clickAnimation, clickAnimation);
            shadowRadius = ta.getDimension(R.styleable.LabelTextView_shadowRadius, shadowRadius);
            shadowColor = ta.getColor(R.styleable.LabelTextView_shadowColor, shadowColor);
            shadowDx = (int) ta.getDimension(R.styleable.LabelTextView_shadowDx,0);
            shadowDy = (int) ta.getDimension(R.styleable.LabelTextView_shadowDy,0);
            strokeWidth = (int) ta.getDimension(R.styleable.LabelTextView_strokeWidth, 0);
            needInnerCircle = ta.getBoolean(R.styleable.LabelTextView_innerCircle, false);
            sweepStroke = ta.getDimensionPixelSize(R.styleable.LabelTextView_sweepStroke, 8);
            loadingPadding = ta.getDimensionPixelSize(R.styleable.LabelTextView_sweepPadding, 10);
            gradientType = ta.getString(R.styleable.LabelTextView_gradientType);
            if (!TextUtils.isEmpty(gradientType)) {
                startColor = ta.getColor(R.styleable.LabelTextView_gradientStartColor, bgColor);
                centerColor = ta.getColor(R.styleable.LabelTextView_gradientCenterColor, bgColor);
                endColor = ta.getColor(R.styleable.LabelTextView_gradientEndColor, bgColor);
            }
            ta.recycle();
        }
        bgPaint = new Paint();
        if (strokeWidth > 0) {
            bgPaint.setStyle(Paint.Style.STROKE);
            bgPaint.setStrokeWidth(strokeWidth);
            bgPaint.setStrokeCap(Paint.Cap.ROUND);
            bgPaint.setStrokeJoin(Paint.Join.ROUND);
        }
        loadingPaint = new Paint();
        loadingPaint.setColor(bgColor);
        loadingPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);
        bgPaint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        bgPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
    }


    public float getRoundRadius() {
        return roundRadius;
    }

    public void setRoundRadius(float roundRadius) {
        this.roundRadius = roundRadius;
        invalidate();
    }

    public boolean isClickAnimation() {
        return clickAnimation;
    }

    public void setClickAnimation(boolean clickAnimation) {
        this.clickAnimation = clickAnimation;
    }


    @Override
    public void invalidate() {
        refresh = true;
        super.invalidate();
    }

    public boolean isLoading() {
        return state == STATE_LOADING;
    }

    @Override
    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        bgPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
        invalidate();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        bgPaint.setStrokeWidth(strokeWidth);
        invalidate();
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        bgPaint.setColor(bgColor);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        float r = shadowRadius + strokeWidth;
        int width = (int) (getWidth());
        int height = (int) (getHeight());
        //init LinearGradient when a gradient type was setted.
        if ("0".equals(gradientType) && gradient == null) {
            gradient = new LinearGradient(0, getHeight() / 2, getWidth(), getHeight() / 2,
                    new int[]{startColor, centerColor, endColor},
                    new float[]{0, 0.5f, 1},
                    Shader.TileMode.CLAMP);
            bgPaint.setShader(gradient);
        }
        //get the progress circle radius
        int radius;
        if (getWidth() < getHeight()) {
            radius = (int) ((width - sweepStroke - shadowRadius * 2) / 2);
        } else {
            radius = (int) ((height - sweepStroke - shadowRadius * 2) / 2);
        }
        int cx = (int) (width / 2 + shadowRadius);
        int cy = (int) (height / 2 + shadowRadius);
        if (state == STATE_LOADING) {
            drawLoading(canvas, cx, cy, radius);
        } else if (state == STATE_PREPARE) {
            bgRect.set(r + scaleStep, r, width - shadowRadius - r - scaleStep, height - shadowRadius - r);
            canvas.drawRoundRect(bgRect, roundRadius, roundRadius, bgPaint);
            if (r + scaleStep >= cx - radius) {
                state = STATE_LOADING;
                scaleStep = 10;
                invalidate();
            } else {
                scaleStep += 20;
                invalidate();
            }
        } else if (state == STATE_BEFORE_FINISH) {
            if (finishCount > 6) {
                finishCount = 0;
                state = STATE_NONE;
                setTextColor(textColor); // reset the text color.
                invalidate();
            } else {
                finishCount++;
                drawLoading(canvas, cx, cy, radius);
                postInvalidateDelayed(150);
            }
        } else { // normal state is drawing a roundRect
            if (bgRect == null) {
                bgRect = new RectF(r, r, width - shadowRadius - r, height - shadowRadius - r);
            } else if (refresh) {
                bgRect.set(r, r, width - shadowRadius - r, height - shadowRadius - r);
                refresh = false;
            }
            canvas.drawRoundRect(bgRect, roundRadius, roundRadius, bgPaint);
        }
        super.onDraw(canvas);
    }

    private void drawLoading(Canvas canvas, int cx, int cy, int radius) {
        // draw a fill circle
        loadingPaint.setColor(bgColor);
        loadingPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, radius, loadingPaint);
        if (needInnerCircle) {
            if (flag) {
                innerR += 5;
                if (innerR >= radius / 3)
                    flag = false;
            } else {
                innerR -= 5;
                if (innerR < 1)
                    flag = true;
            }
            loadingPaint.setColor(textColor);
            canvas.drawCircle(cx, cy, innerR, loadingPaint);
        }
        //draw a circle progress
        startAngle += 30;
        if (startAngle > 360)
            startAngle = 0;
        sweepAngle += 10;
        if (sweepAngle > 270)
            sweepAngle = 10;
        if (loadingRect == null) {
            loadingRect = new RectF(cx - radius + loadingPadding, cy - radius + loadingPadding, cx + radius - loadingPadding, cy + radius - loadingPadding);
        } else {
            loadingRect.set(cx - radius + loadingPadding, cy - radius + loadingPadding, cx + radius - loadingPadding, cy + radius - loadingPadding);
        }
        loadingPaint.setStrokeWidth(sweepStroke);
        loadingPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(loadingRect, startAngle, sweepAngle, false, loadingPaint);
        postInvalidateDelayed(150);
    }


    public void startLoading() {
        if (state < STATE_PREPARE) {
            state = STATE_PREPARE;
            textColor = getTextColors().getDefaultColor();
            setTextColor(0x00000000); // make the text is invisible
        }
    }

    public void finishLoading(String text) {
        state = STATE_BEFORE_FINISH;
        if (TextUtils.isEmpty(text)) {
            invalidate();
        } else {
            setText(text);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (clickAnimation && event.getAction() == MotionEvent.ACTION_DOWN) {
            ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.9f, 1.0f).setDuration(200).start();
            ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.9f, 1.0f).setDuration(200).start();
            ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.5f, 1.0f).setDuration(200).start();
        }
        return super.onTouchEvent(event);
    }
}
