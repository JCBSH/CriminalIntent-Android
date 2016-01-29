package com.example.jcbsh.criminalintent;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.OnFragmentInteractionListener,
        CrimeFragment.OnFragmentInteractionListener{



    @Override
    protected Fragment createFragment() {
        Fragment fragment =  new CrimeListFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment fragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        fragment.updateUI();
    }

    @Override
    public void onCrimeSelected(Crime c) {

        if (findViewById(R.id.detailFragmentContainer) == null) {
            Intent i = new Intent(this, CrimePagerActivity.class);
            i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
            startActivity(i);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = CrimeFragment.newInstance(c.getId());
            if (oldDetail != null) {
                ft.remove(oldDetail);
            }
            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.commit();
        }



    }
}
