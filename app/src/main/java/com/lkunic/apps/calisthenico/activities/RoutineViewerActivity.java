package com.lkunic.apps.calisthenico.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.adapters.ExerciseListAdapter;
import com.lkunic.apps.calisthenico.database.Exercise;
import com.lkunic.apps.calisthenico.database.Routine;
import com.lkunic.apps.calisthenico.database.RoutineTable;
import com.lkunic.libs.apptoolbox.database.DbQuery;
import com.melnykov.fab.FloatingActionButton;

public class RoutineViewerActivity extends AppCompatActivity
{
	public static final String ARG_ROUTINE_ID = "routine_id";

	private long mRoutineId;
	private Routine mRoutine;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routine_viewer);

		Intent i = getIntent();

		if (i != null && i.hasExtra(ARG_ROUTINE_ID))
		{
			mRoutineId = i.getLongExtra(ARG_ROUTINE_ID, -1);
		}

		mRoutine = new Routine();
		mRoutine.id = mRoutineId;

		new RoutineLoader().execute(mRoutine);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_routine_viewer, menu);
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

	private void setupContent()
	{
		setTitle(mRoutine.title);

		TextView txtCycles = (TextView) findViewById(R.id.txt_cycles);
		TextView txtRestCycles = (TextView) findViewById(R.id.txt_rest_cycles);
		TextView txtRestExercises = (TextView) findViewById(R.id.txt_rest_exercises);

		txtCycles.setText(String.valueOf(mRoutine.cycles));
		txtRestCycles.setText(String.format("%ds", mRoutine.restBetweenCycles));
		txtRestExercises.setText(String.format("%ds", mRoutine.restBetweenExercises));

		ListView lvExercises = (ListView) findViewById(R.id.lv_exercises);
		lvExercises.setAdapter(new ExerciseListAdapter(getBaseContext(), mRoutine.exercises));

		FloatingActionButton fabStartRoutine = (FloatingActionButton) findViewById(R.id.btn_start_routine);
		fabStartRoutine.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getBaseContext(), "Starting routine!", Toast.LENGTH_SHORT).show();
			}
		});
	}


	private class RoutineLoader extends AsyncTask<Routine, Void, Cursor>
	{
		@Override
		protected Cursor doInBackground(Routine... params)
		{
			return DbQuery.create(getContentResolver(), params[0].getItemUri())
					.withColumns(RoutineTable.TITLE, RoutineTable.CYCLES, RoutineTable.EXERCISES,
							RoutineTable.REST_BETWEEN_CYCLES, RoutineTable.REST_BETWEEN_EXERCISES)
					.execute();
		}

		@Override
		protected void onPostExecute(Cursor cursor)
		{
			if (cursor == null || cursor.getCount() == 0)
			{
				// The cursor is empty
				return;
			}

			cursor.moveToFirst();

			mRoutine.title = cursor.getString(cursor.getColumnIndex(RoutineTable.TITLE));
			mRoutine.cycles = cursor.getInt(cursor.getColumnIndex(RoutineTable.CYCLES));
			mRoutine.restBetweenCycles = cursor.getInt(cursor.getColumnIndex(RoutineTable.REST_BETWEEN_CYCLES));
			mRoutine.restBetweenExercises = cursor.getInt(cursor.getColumnIndex(RoutineTable.REST_BETWEEN_EXERCISES));

			String[] exerciseStrings = cursor.getString(cursor.getColumnIndex(RoutineTable.EXERCISES)).split("\\|");
			mRoutine.exercises = new Exercise[exerciseStrings.length];

			for (int i = 0, n = exerciseStrings.length; i < n; i++)
			{
				mRoutine.exercises[i] = new Exercise(exerciseStrings[i]);
			}

			setupContent();
		}
	}
}
