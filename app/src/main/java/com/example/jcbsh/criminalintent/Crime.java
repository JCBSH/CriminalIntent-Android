package com.example.jcbsh.criminalintent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by JCBSH on 19/01/2016.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Photo mPhoto = null;
    private String mSuspect;
    private ArrayList<String> mPhoneNumbers;

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";
    private static final String JSON_PHONE_NUMBER = "number";
    private static final String JSON_PHONE_NUMBERS = "numbers";

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
        mPhoneNumbers = new ArrayList<String>();
    }

    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mPhoneNumbers = new ArrayList<String>();
        if (json.has(JSON_TITLE)) {
            mTitle = json.getString(JSON_TITLE);
        }

        if (json.has(JSON_SUSPECT)) {
            mSuspect =  json.getString(JSON_SUSPECT);
        }
        if (json.has(JSON_PHOTO)) {
            mPhoto = new Photo (json.getJSONObject(JSON_PHOTO));
        }
        if (json.has(JSON_PHONE_NUMBERS)) {
            JSONArray array = json.getJSONArray(JSON_PHONE_NUMBERS);
            for (int i = 0; i < array.length(); i++) {
                mPhoneNumbers.add(array.getJSONObject(i).getString(JSON_PHONE_NUMBER));
            }
        }
        mDate = new Date();
        mDate.setTime(json.getLong(JSON_DATE));
        mSolved = json.getBoolean(JSON_SOLVED);
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getFormattedDate2() {
        String date = (String) android.text.format.DateFormat.format("EEEE, dd/MM/yy", getDate());
        return date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public UUID getId() {
        return mId;
    }
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public ArrayList<String> getPhoneNumbers() {
        return mPhoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<String> phoneNumbers) {
        mPhoneNumbers = phoneNumbers;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject object = new JSONObject();
        object.put(JSON_ID, mId.toString());
        object.put(JSON_TITLE, mTitle);
        object.put(JSON_SUSPECT, mSuspect);
        object.put(JSON_SOLVED, mSolved);
        object.put(JSON_DATE, mDate.getTime());
        if (mPhoneNumbers.size() > 0) {
            JSONArray array = new JSONArray();
            for (String number : mPhoneNumbers) {
                JSONObject numberObject = new JSONObject();
                numberObject.put(JSON_PHONE_NUMBER, number);
                array.put(numberObject);
            }
            object.put(JSON_PHONE_NUMBERS, array);
        }
        if (mPhoto != null) {
            object.put(JSON_PHOTO, mPhoto.toJSON());
        }
        return object;

    }
}
