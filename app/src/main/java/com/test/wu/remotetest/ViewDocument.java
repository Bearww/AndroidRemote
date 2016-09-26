package com.test.wu.remotetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewDocument extends Activity {

    private long rowID;
    private TextView nameTextView;
    private TextView linkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_document);

        // Get the EditTexts
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        linkTextView = (TextView) findViewById(R.id.linkTextView);

        // Get the selected document's row ID
        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(Constants.ROW_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create new LoadDocumentTask and execute it
        new LoadDocumentTask().execute(rowID);
    }

    // Performs database query outside GUI thread
    private class LoadDocumentTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector dbConnector = new DatabaseConnector(ViewDocument.this);

        // Perform the database access
        @Override
        protected Cursor doInBackground(Long... params) {

            dbConnector.open();

            // Get a cursor containing all data on given entry
            return dbConnector.getOneDocument(params[0]);
        }

        // Use the cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            cursor.moveToFirst();

            // Get the column index for each data item
            int nameIndex = cursor.getColumnIndex("name");
            int linkIndex = cursor.getColumnIndex("index");

            // Fill TextViews with the retrieved data
            nameTextView.setText(cursor.getString(nameIndex));
            linkTextView.setText(cursor.getString(linkIndex));

            cursor.close();
            dbConnector.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_document_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editItem:
                // Create an intent to launch the AddDocument Activity
                Intent addDocument = new Intent(this, AddDocument.class);

                // Pass the selected contact's data as extras with the intent
                addDocument.putExtra(Constants.ROW_ID, rowID);
                addDocument.putExtra(Constants.DOC_NAME, nameTextView.getText());
                addDocument.putExtra(Constants.DOC_LINK, linkTextView.getText());
                startActivity(addDocument);
                return true;
            case R.id.deleteItem:
                deleteDocument();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Delete a document
    private void deleteDocument() {
        // Create a new AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDocument.this);

        builder.setTitle(R.string.confirmTitle); // Title bar string
        builder.setMessage(R.string.confirmMessage); // Message to display

        // Provide an OK button that simply dismissed the dialog
        builder.setPositiveButton(R.string.button_delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseConnector dbConnector = new DatabaseConnector(ViewDocument.this);

                        // Create an AsyncTask that deletes the document in another thread,
                        // then calls finish after the deletion
                        AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
                            @Override
                            protected Object doInBackground(Long... params) {
                                dbConnector.deleteDocument(params[0]);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                finish();
                            }
                        };

                        // Execute the AsyncTask to delete document at rowID
                        deleteTask.execute(new Long[] { rowID });
                    }
                });

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
    }
}
