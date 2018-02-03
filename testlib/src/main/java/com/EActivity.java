package com;

import android.app.Activity;
import android.os.Bundle;
import com.tencent.tauth.Tencent;

/**
 * Created by wangfei on 2018/2/3.
 */

public class EActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tencent tencent =  Tencent.createInstance("123",this);
    }
}
