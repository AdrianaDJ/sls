/**
 * This file is part of Simple Last.fm Scrobbler.
 * 
 *     https://github.com/tgwizard/sls
 * 
 * Copyright 2011 Simple Last.fm Scrobbler Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

package com.adam.aslfms;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.adam.aslfms.service.NetApp;
import com.adam.aslfms.util.AppSettings;
import com.adam.aslfms.util.ScrobblesDatabase;
import com.adam.aslfms.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StatusActivity extends AppCompatActivity {

	private static final int MENU_SCROBBLE_NOW_ID = 0;
	private static final int MENU_VIEW_CACHE_ID = 1;
	private static final int MENU_RESET_STATS_ID = 2;

    private AppSettings settings;

    private ScrobblesDatabase mDb;

    private static final String TAG = "StatusActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_activity);

        settings = new AppSettings(this);

        mDb = new ScrobblesDatabase(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//TODO: Make menu options work again
		menu.add(0, MENU_SCROBBLE_NOW_ID, 0, R.string.scrobble_now).setIcon(
                android.R.drawable.ic_menu_upload);
		menu.add(0, MENU_RESET_STATS_ID, 0, R.string.reset_stats).setIcon(
                android.R.drawable.ic_menu_close_clear_cancel);
		menu.add(0, MENU_VIEW_CACHE_ID, 0, R.string.view_sc).setIcon(
                android.R.drawable.ic_menu_view);

		return super.onCreateOptionsMenu(menu);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case MENU_SCROBBLE_NOW_ID:
                int numInCache = mDb.queryNumberOfTracks();
                Util.scrobbleAllIfPossible(this, numInCache);
                Log.e(TAG,"Scrobble attempt.");
                return true;
            case MENU_RESET_STATS_ID:
                for (NetApp napp : NetApp.values()){
                    settings.clearSubmissionStats(napp);
                }
                return true;
            case MENU_VIEW_CACHE_ID:
                Intent i = new Intent(this, ViewScrobbleCacheActivity.class);
                i.putExtra("viewall", true);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());

        for (NetApp napp : NetApp.values()) {
            adapter.addFragment(StatusFragment.newInstance(napp.getValue()), napp.getName());
        }
        viewPager.setAdapter(adapter);
    }

    static class TabAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
