package com.example.jcbsh.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private static final String LIFE_TAG = "life_CrimeFragment";

    public static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";

    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_SUSPECT = 2;

    private OnFragmentInteractionListener mListener;

    private Crime mCrime;

    private EditText mCrimeTitleEditText;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private UUID mId;
    private android.view.ActionMode mActionMode;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;


    public CrimeFragment() {
        // Required empty public constructor
    }

    public static CrimeFragment newInstance(UUID id) {
        Bundle bundle = new Bundle();
        CrimeFragment fragment = new CrimeFragment();

        bundle.putSerializable(EXTRA_CRIME_ID, id);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LIFE_TAG, "onCreate() ");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime =  CrimeLab.get(getActivity()).getCrime(mId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    //cause parent activity to be destroyed
                    NavUtils.navigateUpFromSameTask(getActivity());
                    //don't cause parent activity to be destroyed
                    //getActivity().finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getActivity().getMenuInflater().inflate(R.menu.fragment_crime_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_photo:
                deletePhoto();
                showPhoto();
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(LIFE_TAG, "onAttach() ");
        super.onAttach(context);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        Log.d(LIFE_TAG, "onCreateView() ");
        View view = inflater.inflate(R.layout.fragment_crime, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

        }

        mCrimeTitleEditText = (EditText) view.findViewById(R.id.crime_editText);
        mCrimeTitleEditText.setText(mCrime.getTitle());
        mCrimeTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button)view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mPhotoButton = (ImageButton) view.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
                //intent.putExtra()
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });

        // If camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD &&
                        Camera.getNumberOfCameras() > 0);
        if (!hasACamera) {
            mPhotoButton.setEnabled(false);
        }

        mPhotoView = (ImageView)view.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null)
                    return;
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                String path = getActivity()
                        .getFileStreamPath(p.getFilename()).getAbsolutePath();

                ImageFragment.newInstance(path, p.isLandScape())
                        .show(fm, DIALOG_IMAGE);
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(mPhotoView);
        } else {
            mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
                // Called when the user long-clicks on someView
                public boolean onLongClick(View view) {
                    if (mActionMode != null) {
                        return false;
                    }

                    // Start the CAB using the ActionMode.Callback defined above
                    mActionMode = getActivity().startActionMode(new ActionMode.Callback() {
                        // Called when the action mode is created; startActionMode() was called
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            // Inflate a menu resource providing context menu items
                            MenuInflater inflater = mode.getMenuInflater();
                            inflater.inflate(R.menu.fragment_crime_context, menu);
                            return true;
                        }

                        // Called each time the action mode is shown. Always called after onCreateActionMode, but
                        // may be called multiple times if the mode is invalidated.
                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            return false; // Return false if nothing is done
                        }

                        // Called when the user selects a contextual menu item

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_item_delete_photo:
                                    deletePhoto();
                                    showPhoto();
                                    mode.finish(); // Action picked, so close the CAB
                                    return true;
                                default:
                                    return false;
                            }
                        }

                        // Called when the user exits the action mode
                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            mActionMode = null;
                        }
                    });
                    view.setSelected(true);
                    return true;
                }
            });
        }

        mReportButton = (Button)view.findViewById(R.id.crime_reportButton);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));

                //check is there is app that will response to the implicit intent
                //before starting it
                PackageManager pm = getActivity().getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe) {
                    startActivity(i);
                }

            }
        });

        mSuspectButton = (Button) view.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

                //check is there is app that will response to the implicit intent
                //before starting it
                PackageManager pm = getActivity().getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe) {
                    startActivityForResult(i, REQUEST_SUSPECT);
                }

            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallSuspectButton = (Button)view.findViewById(R.id.crime_callSuspectButton);
        if (mCrime.getPhoneNumbers().size() > 0) {
            mCallSuspectButton.setVisibility(View.VISIBLE);
        } else {
            mCallSuspectButton.setVisibility(View.INVISIBLE);
        }
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCrime.getPhoneNumbers().size() > 0) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:" + mCrime.getPhoneNumbers().get(0)));
                    startActivity(i);
                }

            }
        });

        return view;
    }

    private void showPhoto() {
// (Re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
            if (!p.isLandScape()) b = PictureUtils.getPortraitDrawable(getActivity(), b);
        }
        mPhotoView.setImageDrawable(b);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LIFE_TAG, "onActivityResult() ");
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
            case REQUEST_PHOTO:
                // Create a new Photo object and attach it to the crime
                String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
                int orientation = data.getIntExtra(CrimeCameraFragment.EXTRA_PHOTO_ORIENTATION, 0);
                if (filename != null) {
                    Log.i(TAG, "filename: " + filename);
                }

                deletePhoto();
                mCrime.setPhoto(new Photo(filename, orientation));
                showPhoto();
                //Log.i(TAG, "Crime: " + mCrime.getTitle() + " has a photo");
                break;
            case REQUEST_SUSPECT:
                Uri contactUri = data.getData();

                String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
                Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null);

                if(cursor.getCount() == 0){
                    cursor.close();
                    return;
                }

                cursor.moveToFirst();
                int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String contractId = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                mCrime.setSuspect(name);

                Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contractId,null,null);

                ArrayList<String> numbers = new ArrayList<String>();
                while(phones.moveToNext()){
                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    numbers.add(number);

//                    int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//                    switch (type){
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                            break;
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                            break;
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                            break;
//                    }
                }
                mCrime.setPhoneNumbers(numbers);

                phones.close();
                cursor.close();

                mSuspectButton.setText(mCrime.getSuspect());
                if(mCrime.getPhoneNumbers().size() > 0){
                    mCallSuspectButton.setVisibility(View.VISIBLE);
                } else {
                    mCallSuspectButton.setVisibility(View.INVISIBLE);
                }
                break;
        }

    }

//    private void deletePhoto() {
//        if (mCrime.getPhoto() != null) {
//            String oldPhotoPath = getActivity()
//                    .getFileStreamPath(mCrime.getPhoto().getFilename()).getAbsolutePath();
//            File file = new File(oldPhotoPath);
//            if(file.delete()) {
//                PictureUtils.cleanImageView(mPhotoView);
//                mCrime.setPhoto(null);
//            }
//        }
//    }

    private void deletePhoto() {
        if (mCrime.getPhoto() != null) {
            if(getActivity().deleteFile(mCrime.getPhoto().getFilename())) {
                PictureUtils.cleanImageView(mPhotoView);
                mCrime.setPhoto(null);
            }
        }
    }


    private void updateDate() {
        mDateButton.setText(mCrime.getFormattedDate2());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }


    @Override
    public void onStart() {
        Log.d(LIFE_TAG, "onStart() ");
        super.onStart();
        //showPhoto();
    }

    @Override
    public void onResume() {
        Log.d(LIFE_TAG, "onResume() ");
        //showPhoto();
        super.onResume();
        showPhoto();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onPause() {
        Log.d(LIFE_TAG, "onPause() ");
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
        PictureUtils.cleanImageView(mPhotoView);
        if (mActionMode != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mActionMode.finish();
            }

        }

    }

    @Override
    public void onStop() {
        Log.d(LIFE_TAG, "onStop() ");
        super.onStop();
        //PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public void onDestroyView() {
        Log.d(LIFE_TAG, "onDestroyView() ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(LIFE_TAG, "onDestroy() ");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(LIFE_TAG, "onDetach() ");
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
