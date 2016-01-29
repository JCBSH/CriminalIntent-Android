package com.example.jcbsh.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by JCBSH on 22/01/2016.
 */
public class CrimeCameraActivity extends SingleFragmentActivity{



    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }



}
