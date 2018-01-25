package apps.mohamed.customtoolbar;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, CustomToolbarView.CustomListener {

    private TextView tv_scale;
    private TextView tv_timeScale;
    private TextView tv_isNight;
    private TextView tv_scrollRange;
    private AppBarLayout appBar;
    private CustomToolbarView mCustomToolbarView;

    private float scrollRange = -1;
    private float timeScale = 0F;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        mSharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);

        mCustomToolbarView = findViewById(R.id.custom_toolbar);
        appBar = findViewById(R.id.app_bar);
        tv_scale = findViewById(R.id.tv_scale);
        tv_timeScale = findViewById(R.id.tv_timeScale);
        tv_isNight = findViewById(R.id.tv_isNight);
        tv_scrollRange = findViewById(R.id.tv_scrollRange);

        //Adding OnOffsetChangedListener to AppBarLayout
        appBar.addOnOffsetChangedListener(this);
        mCustomToolbarView.setCustomListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
         /*
         * Since the totalScrollRange is not going to change in runtime,
         * we getTotalScrollRange() only if the totalScrollRange equals -1
         * which is the initial value, if its not then it means that we already called it
         * and therefore we don't need to keep doing it.
         */
        if (scrollRange == -1) scrollRange = appBarLayout.getTotalScrollRange();

        /*
        * Simple Maths here, we divide verticalOffset we get from the listener
        * by the scrollRange and we add 1 to it to prevent the scale from being -1
        * when the AppBar is completely collapsed.
        */
        float scale = 1 + verticalOffset / scrollRange;

        //We add the elevation to the AppBarLayout only if its collapsed.
        if (scale <= 0.0F) appBar.setElevation(10);
        else appBar.setElevation(0);

        //We update the scale in our custom view
        mCustomToolbarView.setScale(scale);

        tv_scale.setText("Scale: " + scale);
        tv_scrollRange.setText("Scroll Range: " + scrollRange);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onValuesUpdated(boolean isNight, float timeScale) {
        tv_isNight.setText("isNight: " + String.valueOf(isNight));
        tv_timeScale.setText("timeScale: " + String.valueOf(timeScale));
    }
}