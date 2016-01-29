package com.example.jcbsh.criminalintent;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeListFragment extends ListFragment {

    private static final int REQUEST_CRIME = 0;
    private ArrayList<Crime> mCrimes;
    private static final String TAG = "CrimeListFragment";
    public static final String LIFE_TAG = "life_ListFragment";

    private boolean mSubtitleVisible;

    public CrimeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LIFE_TAG, "onAttach() ");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LIFE_TAG, "onCreate() ");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mSubtitleVisible = false;

        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        ArrayAdapter<Crime> adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(LIFE_TAG, "onCreateContextMenu() ");
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
        Crime crime = adapter.getItem(position);
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(crime);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible && showSubtitle != null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }

    }
    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
                startActivityForResult(i, 0);
                return true;
            case R.id.menu_item_show_subtitle:
                if (((AppCompatActivity) getActivity()).getSupportActionBar().getSubtitle() == null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle);
                    mSubtitleVisible = true;
                    item.setTitle(R.string.hide_subtitle);
                } else {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
                    mSubtitleVisible= false;
                    item.setTitle(R.string.show_subtitle);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LIFE_TAG, "onCreateView() ");
        // Inflate the layout for this fragment
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (mSubtitleVisible) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.subtitle);
            }
        }

        FrameLayout frameLayout = new FrameLayout(getActivity());
        TextView textView = new TextView(getActivity());
        ListView listView = (ListView)v.findViewById(android.R.id.list);
        //textView.setId(android.R.id.empty);
        //textView.setBackgroundColor(Color.BLACK);
        textView.setText(getActivity().getString(R.string.empty_crimes_message));

        listView.setEmptyView(textView);
        //v.findViewById(android.R.id.list).setBackgroundColor(Color.BLACK);
        //TextView textView1 = (TextView) v.findViewById(android.R.id.empty);
        //textView1.setText(getActivity().getString(R.string.empty_crimes_message));
        //getListView().setEmptyView(textView);
        frameLayout.addView(v);
        frameLayout.addView(textView);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // Use floating context menus on Froyo and Gingerbread
            registerForContextMenu(listView);
        } else {
            // Use contextual action bar on Honeycomb and higher
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {

                    // Required, but not used in this implementation
                }
                // ActionMode.Callback methods
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.crime_list_item_context, menu);
                    return true;
                }
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                    // Required, but not used in this implementation
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    if (item.getItemId() == R.id.menu_item_delete_crime) {
                        CrimeAdapter adapter = (CrimeAdapter) getListView().getAdapter();
                        for (int i = 0; i < adapter.getCount(); i++) {
                            if (getListView().isItemChecked(i)) {
                                CrimeLab.get(getActivity()).deleteCrime(adapter.getItem(i));
                            }
                        }
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                    return false;

// BOOK WAY
//                    switch (item.getItemId()) {
//                        case R.id.menu_item_delete_crime:
//                            CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
//                            CrimeLab crimeLab = CrimeLab.get(getActivity());
//                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
//                                if (getListView().isItemChecked(i)) {
//                                    crimeLab.deleteCrime(adapter.getItem(i));
//                                }
//                            }
//                            mode.finish();
//                            adapter.notifyDataSetChanged();
//                            return true;
//                        default:
//                            return false;
//                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
        return frameLayout;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LIFE_TAG, "onActivityCreated() ");
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStart() {
        Log.d(LIFE_TAG, "onStart() ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(LIFE_TAG, "onResume() ");
        super.onResume();
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        Log.d(LIFE_TAG, "onPause() ");
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public void onStop() {
        Log.d(LIFE_TAG, "onStop() ");
        super.onStop();
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
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
        Log.d(TAG, c.getTitle() + " was clicked");

        // Start CrimePagerActivity
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivityForResult(i, REQUEST_CRIME);

        //((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME) {

        }
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_crime, null);
            }

            TextView title = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            title.setText(getItem(position).getTitle());

            TextView date = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
            date.setText(getItem(position).getFormattedDate2());

            CheckBox solved = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solved.setChecked(getItem(position).isSolved());



            return convertView;
        }
    }



}
