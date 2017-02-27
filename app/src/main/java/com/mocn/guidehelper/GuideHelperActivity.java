package com.mocn.guidehelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GuideHelperActivity extends AppCompatActivity {
    private TextView tv_first;
    private Button btn_first;
    private Button btn_second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_helper);
        tv_first = (TextView) findViewById(R.id.tv_first);
        btn_first = (Button) findViewById(R.id.btn_first);
        btn_second = (Button) findViewById(R.id.btn_second);

        btn_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<GuideHelper.PageData> pageDatas = new ArrayList<GuideHelper.PageData>();
                pageDatas.add(new GuideHelper.PageData(btn_first, R.mipmap.tip1));
                pageDatas.add(new GuideHelper.PageData(tv_first, R.mipmap.tip1));
                pageDatas.add(new GuideHelper.PageData(btn_second, R.mipmap.tip1));

                GuideHelper guideHelper = new GuideHelper(GuideHelperActivity.this, pageDatas);
                guideHelper.show();
            }
        });
    }
}
