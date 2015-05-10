package com.lkunic.apps.calisthenico.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.adapters.RoutineListAdapter;
import com.lkunic.apps.calisthenico.database.Routine;
import com.lkunic.apps.calisthenico.database.RoutineTable;
import com.lkunic.libs.apptoolbox.database.DbQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Luka Kunic 2015 / "RoutineBrowserActivity.java"
 * Created by lkunic on 08/05/2015.
 *
 * Activity that lists all saved routines.
 */
public class RoutineBrowserActivity extends AppCompatActivity
{
	// Reference to the list view that displays the routines
	private ListView mLvRoutines;

	// The routine list
	private List<Routine> mRoutines;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routine_browser);

		// Fetch the routines from the database and setup the list
		setupRoutineList();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		// This method will be called after returning from a RoutineViewerActivity. We need to reload the routine list
		// to pick up any changes that might have happened to the routine data (deleted routines, modified values...).
		mRoutines.clear();
		new RoutineLoader().execute();
	}

	private void setupRoutineList()
	{
		// Create the list that will hold routine data
		mRoutines = new ArrayList<>();

		// Configure the list view and connect it to the routine list
		mLvRoutines = (ListView) findViewById(R.id.lv_routines);
		mLvRoutines.setAdapter(new RoutineListAdapter(this, mRoutines));
		mLvRoutines.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// Start the RoutineViewerActivity for the selected routine
				Intent i = new Intent(getBaseContext(), RoutineViewerActivity.class);
				i.putExtra(RoutineViewerActivity.ARG_ROUTINE_ID, mRoutines.get(position).id);
				startActivityForResult(i, 0);
			}
		});

		// Start the routine loader that will populate the list
		new RoutineLoader().execute();
	}

	// region RoutineLoader

	private class RoutineLoader extends AsyncTask<Void, Void, Cursor>
	{
		@Override
		protected Cursor doInBackground(Void... params)
		{
			// Query the database for all routines and order them by name
			return DbQuery.create(getContentResolver(), Routine.URI)
					.withColumns(RoutineTable.ID, RoutineTable.TITLE, RoutineTable.EXERCISE_COUNT, RoutineTable.CYCLES)
					.orderBy(RoutineTable.TITLE + " ASC")
					.execute();
		}

		@Override
		protected void onPostExecute(Cursor cursor)
		{
			if (cursor == null)
			{
				// Nothing to process
				return;
			}

			Routine routine;

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
			{
				routine = new Routine();

				routine.id = cursor.getLong(cursor.getColumnIndex(RoutineTable.ID));
				routine.title = cursor.getString(cursor.getColumnIndex(RoutineTable.TITLE));
				routine.cycles = cursor.getInt(cursor.getColumnIndex(RoutineTable.CYCLES));
				routine.exerciseCount = cursor.getInt(cursor.getColumnIndex(RoutineTable.EXERCISE_COUNT));

				// Add the routine to the list
				mRoutines.add(routine);
			}

			// Notify the adapter that the routine list has changed so that the list view gets refreshed
			((RoutineListAdapter)mLvRoutines.getAdapter()).notifyDataSetChanged();
		}
	}

	// endregion
}
