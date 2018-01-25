package apps.mohamed.customtoolbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Mohamed Elidrissi on 22/01/2018.
 */

public class NonClickableToolbar extends Toolbar {

    public NonClickableToolbar(Context context) {
        super(context);
    }

    public NonClickableToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NonClickableToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //We return false to disable touch events
        return false;
    }

}