package com.test.wu.remotetest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MeetingJoinTask mJoinTask = null;

    private EditText meetingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        meetingID = (EditText) findViewById(R.id.editText);

        Button meetingCreateButton = (Button) findViewById(R.id.createButton);
        meetingCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreate();
            }
        });

        Button meetingJoinButton = (Button) findViewById(R.id.joinButton);
        meetingJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptJoin();
            }
        });

        // Template
        // Action of toolbar at bottom

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meeting_join) {
            // Handle the meeting create/join action
        } else if (id == R.id.nav_meeting_list) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void attemptCreate() {
        if(mJoinTask != null) {
            return;
        }

        // Get free id from the server.
        String meeting = "1";

        boolean cancel = false;
        View focusView = null;

/*
        // Check for a valid meeting id.
        if (TextUtils.isEmpty(meeting)) {
            meetingID.setError(getString(R.string.error_field_required));
            focusView = meetingID;
            cancel = true;
        }
*/

        if (cancel) {
            // There was an error; don't attempt join and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user join attempt.
            //showProgress(true);
            mJoinTask = new MeetingJoinTask(meeting);
            mJoinTask.execute((Void) null);
        }
    }

    private void attemptJoin() {
        if(mJoinTask != null) {
            return;
        }

        // Reset errors.
        meetingID.setError(null);

        // Store values at the time of the login attempt.
        String meeting = meetingID.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid meeting id.
        if (TextUtils.isEmpty(meeting)) {
            meetingID.setError(getString(R.string.error_field_required));
            focusView = meetingID;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt join and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user join attempt.
            //showProgress(true);
            mJoinTask = new MeetingJoinTask(meeting);
            mJoinTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous join task
     * the user.
     */
    public class MeetingJoinTask extends AsyncTask<Void, Void, Boolean> {

        private final int mMeetingID;

        MeetingJoinTask(String meetingID) {
            mMeetingID = Integer.parseInt(meetingID);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            // Check meeting id is online

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mJoinTask = null;
            //showProgress(false);

            if (success) {
                finish();
                Intent intent = new Intent(MenuActivity.this, MeetingActivity.class);
                startActivity(intent);
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mJoinTask = null;
            //showProgress(false);
        }
    }
}
