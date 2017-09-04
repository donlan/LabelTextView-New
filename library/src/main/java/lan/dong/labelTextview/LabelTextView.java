package lan.dong.labelTextview;

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
    private RectF bgRect;
    private RectF loadingRect;
    private boolean needInnerCircle = false;
    private int startColor;
    private int centerColor;
    private int endColor;

    private String gradientType = "";

    private static final int STATE_NONE = 0;
    private static final int STATE_LOADING = 1;

    private int state;

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
            strokeWidth = (int) ta.getDimension(R.styleable.LabelTextView_strokeWidth, 0);
            needInnerCircle = ta.getBoolean(R.styleable.LabelTextView_innerCircle, false);
            sweepStroke = ta.getDimensionPixelSize(R.styleable.LabelTextView_sweepStroke,8);
            loadingPadding = ta.getDimensionPixelSize(R.styleable.LabelTextView_sweepPadding,10);
            gradientType = ta.getString(R.styleable.LabelTextView_gradientType);
            if(!TextUtils.isEmpty(gradientType)){
                startColor = ta.getColor(R.styleable.LabelTextView_gradientStartColor,bgColor);
                centerColor = ta.getColor(R.styleable.LabelTextView_gradientCenterColor,bgColor);
                endColor = ta.getColor(R.styleable.LabelTextView_gradientEndColor,bgColor);
            }
            System.out.println(gradientType);
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
        bgPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }


    private int startAngle = 0;
    private int sweepAngle = 20;
    private int loadingPadding = 10;
    private int innerR = 1;
    private boolean flag = true;
    private int sweepStroke = 8;
    private LinearGradient gradient;
    @Override
    protected void onDraw(Canvas canvas) {
        float r = shadowRadius / 2 + strokeWidth;
        if("linear".equals(gradientType) && gradient == null){
            gradient = new LinearGradient(0,getHeight()/2,getWidth(),getHeight()/2,
                    new int[]{startColor,centerColor,endColor},
                    new float[]{0,0.5f,1},
                    Shader.TileMode.CLAMP);
            bgPaint.setShader(gradient);
        }
        if (state == STATE_LOADING) {
            int radius;
            if (getWidth() < getHeight()) {
                radius = (getWidth() - sweepStroke) / 2;
            } else {
                radius = (getHeight() - sweepStroke) / 2;
            }

            int l = getWidth() / 2;
            int rr = getHeight() / 2;
            loadingPaint.setColor(bgColor);
            loadingPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(l, rr, radius, loadingPaint);
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
                canvas.drawCircle(l, rr, innerR, loadingPaint);
            }
            startAngle += 30;
            if (startAngle > 360)
                startAngle = 0;
            sweepAngle += 10;
            if (sweepAngle > 270)
                sweepAngle = 10;
            if (loadingRect == null) {
                loadingRect = new RectF(l - radius + loadingPadding, loadingPadding, l + radius - loadingPadding, (radius) * 2 + sweepStroke/2 - loadingPadding);
            } else {
                loadingRect.set(l - radius + loadingPadding, loadingPadding, l + radius - loadingPadding, (radius) * 2 + sweepStroke/2 - loadingPadding);
            }
            loadingPaint.setStrokeWidth(sweepStroke);
            loadingPaint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(loadingRect, startAngle, sweepAngle, false, loadingPaint);
            postInvalidateDelayed(150);
        } else {
            if (bgRect == null) {
                bgRect = new RectF(r, r, getWidth() - r, getHeight() - r);
            } else if (refresh) {
                bgRect.set(r, r, getWidth() - r, getHeight() - r);
                refresh = false;
            }
            canvas.drawRoundRect(bgRect, roundRadius, roundRadius, bgPaint);
        }
        super.onDraw(canvas);
    }

    private String mText;
    private int textColor;

    public void startLoading() {
        if (state != STATE_LOADING) {
            mText = getText().toString();
            state = STATE_LOADING;
            textColor = getTextColors().getDefaultColor();
            setTextColor(0x00000000);
        }
    }

    public void finishLoading(String text) {
        state = STATE_NONE;
        if(TextUtils.isEmpty(text)){
            invalidate();
        }else {
            setTextColor(textColor);
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
