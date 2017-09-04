package lan.dong.labeltextview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import lan.dong.repository.LabelTextView;

/**
 * Created by 梁桂栋 on 2017/9/4 ： 22:06.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: lan.dong.labeltextview
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        final LabelTextView text1 = (LabelTextView) findViewById(lan.dong.repository.R.id.text1);
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text1.isLoading()){
                    text1.finishLoading("加载完毕");
                }else
                    text1.startLoading();
            }
        });
    }
}
