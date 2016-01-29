package com.example.jcbsh.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JCBSH on 22/01/2016.
 */
public class Photo {
    private static final String JSON_FILENAME = "filename";
    private static final String JSON_ROTATION = "rotation";

    public static final int ORIENTATION_PORTRAIT_NORMAL_ROTATION =  90;
    public static final int ORIENTATION_PORTRAIT_INVERTED_ROTATION =  270;
    public static final int ORIENTATION_LANDSCAPE_NORMAL_ROTATION =  0;
    public static final int ORIENTATION_LANDSCAPE_INVERTED_ROTATION =  180;
    private String mFilename;
    private int mRotation;
    private boolean mLandScapeFlag;

    /** Create a Photo representing an existing file on disk */
    public Photo(String filename, int orientation) {
        mFilename = filename;

        //Log.d("AAAAA", String.valueOf(orientation));

        if (orientation >= 315 || orientation < 45) {
            mRotation = Photo.ORIENTATION_PORTRAIT_NORMAL_ROTATION;
            mLandScapeFlag = false;
        }
        else if (orientation < 315 && orientation >= 225) {
            mRotation = Photo.ORIENTATION_LANDSCAPE_NORMAL_ROTATION;
            mLandScapeFlag = true;
        }
        else if (orientation < 225 && orientation >= 135) {
            mRotation = Photo.ORIENTATION_PORTRAIT_INVERTED_ROTATION;
            mLandScapeFlag = false;

        }
        else { // orientation <135 && orientation > 45
            mRotation = Photo.ORIENTATION_LANDSCAPE_INVERTED_ROTATION;
            mLandScapeFlag = true;
        }

    }

    public Photo(JSONObject object) throws JSONException {
        mFilename = object.getString(JSON_FILENAME);
        mRotation = object.getInt(JSON_ROTATION);
    }

    public String getFilename() {
        return mFilename;
    }

    public int getRotation() {
        return mRotation;
    }

    public JSONObject toJSON () throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_FILENAME, mFilename);
        jsonObject.put(JSON_ROTATION, mRotation);
        return jsonObject;
    }


    public boolean isLandScape() {
        return mLandScapeFlag;
    }
}
