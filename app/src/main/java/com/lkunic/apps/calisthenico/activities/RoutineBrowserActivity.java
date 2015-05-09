package com.lkunic.apps.calisthenico.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.adapters.RoutineListAdapter;
import com.lkunic.apps.calisthenico.database.Routine;
import com.lkunic.apps.calisthenico.database.RoutineTable;
import com.lkunic.libs.apptoolbox.database.DbQuery;

import java.util.ArrayList;
import java.util.List;

public class RoutineBrowserActivity extends AppCompatActivity
{
	private ListView mLvRoutines;
	private List<Routine> mRoutines;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routine_browser);

		setupRoutineList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_routine_browser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setupRoutineList()
	{
		mRoutines = new ArrayList<>();
		mLvRoutines = (ListView) findViewById(R.id.lv_routines);
		mLvRoutines.setAdapter(new RoutineListAdapter(this, mRoutines));
		mLvRoutines.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Toast.makeText(getBaseContext(), "Routine clicked - " + mRoutines.get(position).id, Toast.LENGTH_SHORT).show();
			}
		});

		new RoutineLoader().execute();
	}

	private class RoutineLoader extends AsyncTask<Void, Void, Cursor>
	{
		@Override
		protected Cursor doInBackground(Void... params)
		{
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

				mRoutines.add(routine);
			}

			((RoutineListAdapter)mLvRoutines.getAdapter()).notifyDataSetChanged();
		}
	}
}
