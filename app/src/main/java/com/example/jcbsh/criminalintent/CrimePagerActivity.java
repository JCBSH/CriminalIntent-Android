package com.example.jcbsh.criminalintent;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;


public class CrimePagerActivity extends ActionBarActivity implements CrimeFragment.OnFragmentInteractionListener{

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<Crime> mCrimes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes = CrimeLab.get(this).getCrimes();


        mPagerAdapter = new CrimePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);


        Crime crime = (Crime) CrimeLab.get(this).
                getCrime((java.util.UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID));
        mViewPager.setCurrentItem(mCrimes.indexOf(crime));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Crime crime = mCrimes.get(position);
                if (crime.getTitle() != null) {
                    setTitle(crime.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class CrimePagerAdapter extends FragmentStatePagerAdapter {


        public CrimePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mCrimes.size();
        }
        @Override
        public Fragment getItem(int pos) {
            Crime crime = mCrimes.get(pos);
            return CrimeFragment.newInstance(crime.getId());
        }


    }


}
