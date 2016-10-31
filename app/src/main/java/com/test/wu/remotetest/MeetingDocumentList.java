package com.test.wu.remotetest;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

public class MeetingDocumentList extends ListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME};

    // This is the select criteria
    static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_meeting_document, container, false);
/*
        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) view.findViewById(android.R.id.content);
        root.addView(progressBar);
*/
        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = { "name" };
        int[] toViews = { R.id.docTextView }; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.simple_list_document, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Create new GetDocumentsTask and execute it
        new GetDocumentsTask().execute((Object[]) null);
    }

    @Override
    public void onStop() {
        Cursor cursor = mAdapter.getCursor();

        if(cursor != null)
            cursor.deactivate();

        mAdapter.changeCursor(null);
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.document_menu, menu);

        // Place an action bar item for searching.
        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(getActivity());
        sv.setOnQueryTextListener(this);
        item.setActionView(sv);
    }

    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        new GetDocumentsTask().execute((Object[]) null);
        return null;
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        // Create an intent to launch the ViewDocument activity
        Intent viewDoc = new Intent(getActivity(), ViewDocument.class);

        // Pass the selected contact's row ID as an extra with the intent
        viewDoc.putExtra(Constants.ROW_ID, id);
        startActivity(viewDoc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Create a new intent to launch the AddDocument activity
        Intent addNewDocument = new Intent(getActivity(), AddDocument.class);
        startActivity(addNewDocument);
        return super.onOptionsItemSelected(item);
    }

    // Performs database query outside GUI thread
    private class GetDocumentsTask extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector dbConnector = new DatabaseConnector(getActivity());

        // Perform the database access
        @Override
        protected Cursor doInBackground(Object... params) {
            dbConnector.open();

            // TODO Remove manual doc list
            //dbConnector.insertDocument("Example1", "www.csie.nuk.edu.tw/~wuch/course/csb051/csb051-python.pdf");
            //dbConnector.insertDocument("Example2", "123");
            //dbConnector.insertDocument("Example3", "456");

            // Get a cursor containing call documents
            return dbConnector.getAllDocuments();
        }

        // Use the cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor cursor) {
            mAdapter.changeCursor(cursor);
            dbConnector.close();
        }
    }
}
