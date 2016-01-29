package com.example.jcbsh.criminalintent;

import android.annotation.TargetApi;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by JCBSH on 22/01/2016.
 */
public class ImageFragment extends DialogFragment{
    public static final String EXTRA_IMAGE_PATH =
            "com.bignerdranch.android.criminalintent.image_path";
    public static final String EXTRA_IMAGE_LANDSCAPE_FLAG =
            "com.bignerdranch.android.criminalintent.image_rotation";
    private ImageView mImageView;

    public static ImageFragment newInstance(String imagePath, boolean landScapeFlag) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        bundle.putBoolean(EXTRA_IMAGE_LANDSCAPE_FLAG, landScapeFlag);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //FrameLayout frameLayout = new FrameLayout(getActivity());
        //frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mImageView = new ImageView(getActivity());

        String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);

        boolean landScapeFlag =  getArguments().getBoolean(EXTRA_IMAGE_LANDSCAPE_FLAG);

        if (!landScapeFlag) image = PictureUtils.getPortraitDrawable(getActivity(),image);

        mImageView.setImageDrawable(image);


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return mImageView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}
