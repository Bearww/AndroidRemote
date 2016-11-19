package com.test.wu.remotetest;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MeetingActivity extends AppCompatActivity {

    Fragment currentFragment = null;

    Map<String, String> meetingLink = new HashMap<>();
    Map<String, String> meetingForm = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android version >= 18 -> set orientation fullUser
        if (Build.VERSION.SDK_INT >= 18)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);

            // Android version < 18 -> set orientation fullSensor
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        setContentView(R.layout.activity_meeting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("議程"));
        tabLayout.addTab(tabLayout.newTab().setText("成員"));
        tabLayout.addTab(tabLayout.newTab().setText("文件"));
        tabLayout.addTab(tabLayout.newTab().setText("測試"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                currentFragment = adapter.getItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        new LinkCloudTask(LinkCloud.MEETING).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_remote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        Log.i("[MA]Fragment", "onBackPressed");
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            Log.i("[MA]Fragment", "nothing on backstack");
            super.onBackPressed();
            //additional code
        } else {
            Log.i("[MA]Fragment", "popping backstack");
            getFragmentManager().popBackStack();
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(currentFragment instanceof IOnFocusListenable) {
            ((IOnFocusListenable) currentFragment).onWindowFocusChanged(hasFocus);
        }
    }

    /**
     * Use an AsyncTask to fetch the cloud data on a background thread, and update
     * the meeting text field with results on the main UI thread.
     */
    class LinkCloudTask extends AsyncTask<Void, Void, Boolean> {

        private String mUrl;

        public LinkCloudTask(String url) {
            mUrl = url;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            JSONObject content = null;
            try {
                content = LinkCloud.request(mUrl);
                meetingLink = LinkCloud.getLink(content);
                meetingForm = LinkCloud.getForm(content);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return content != null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if(success) {
                Log.i("[MA]Load", "Meeting Links");
                for(String link : meetingLink.keySet())
                    Log.i("[MA]Link", meetingLink.get(link));

                Log.i("[MA]Load", "Meeting Forms");
                for(String form : meetingLink.keySet())
                    Log.i("[MA]Link", meetingLink.get(form));
            }
        }
    }
}
