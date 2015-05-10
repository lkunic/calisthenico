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
import com.lkunic.libs.apptoolbox.database.DbUtil;
import com.lkunic.libs.apptoolbox.dialogs.BaseDialog;
import com.lkunic.libs.apptoolbox.dialogs.DeleteConfirmationDialog;
import com.lkunic.libs.apptoolbox.dialogs.OnDialogResultListener;
import com.melnykov.fab.FloatingActionButton;

/**
 * Copyright (c) Luka Kunic 2015 / "RoutineViewerActivity.java"
 * Created by lkunic on 08/05/2015.
 *
 * Activity is displays detailed information about the routine and allows starting the routine execution.
 */
public class RoutineViewerActivity extends AppCompatActivity
{
	// Argument key for the routine id
	public static final String ARG_ROUTINE_ID = "routine_id";

	// The routine being displayed
	private Routine mRoutine;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routine_viewer);

		// Get the routine id from the intent extras
		mRoutine = new Routine();
		mRoutine.id = getIntent().getLongExtra(ARG_ROUTINE_ID, -1);

		// Start the loader that will fetch the routine from the database
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

		if (id == R.id.action_delete)
		{
			// Display a confirmation dialog before deleting the routine
			DeleteConfirmationDialog dialog = DeleteConfirmationDialog.newInstance(mRoutine.title);
			dialog.setDialogResultListener(deleteRoutineDialogListener);
			dialog.display(getSupportFragmentManager());

			return true;
		}

		if (id == R.id.action_edit)
		{
			// Start the RoutineEditorActivity with the routine id as an intent extra
			Intent i = new Intent(getBaseContext(), RoutineEditorActivity.class);
			i.putExtra(RoutineEditorActivity.ARG_ROUTINE_ID, mRoutine.id);
			startActivityForResult(i, 0);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		// Called when returning from a RoutineEditorActivity
		// Load the routine data again in case something changed
		new RoutineLoader().execute(mRoutine);
	}

	/**
	 * Get references to the views and set up the view behavior.
	 */
	private void setupContent()
	{
		// Set the activity title to the routine title
		setTitle(mRoutine.title);

		// Get view references for the routine parameters
		TextView txtCycles = (TextView) findViewById(R.id.txt_cycles);
		TextView txtRestCycles = (TextView) findViewById(R.id.txt_rest_cycles);
		TextView txtRestExercises = (TextView) findViewById(R.id.txt_rest_exercises);

		// Set the routine parameter values
		txtCycles.setText(String.valueOf(mRoutine.cycles));
		txtRestCycles.setText(String.format("%ds", mRoutine.restBetweenCycles));
		txtRestExercises.setText(String.format("%ds", mRoutine.restBetweenExercises));

		// Setup the exercise list
		ListView lvExercises = (ListView) findViewById(R.id.lv_exercises);
		lvExercises.setAdapter(new ExerciseListAdapter(getBaseContext(), R.layout.row_view_exercise, mRoutine.exercises));

		// Setup the floating action button that is used for starting routine execution
		FloatingActionButton fabStartRoutine = (FloatingActionButton) findViewById(R.id.btn_start_routine);
		fabStartRoutine.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO implement routine execution
				Toast.makeText(getBaseContext(), "Starting routine!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	// region Dialog listener

	/**
	 * Listener that handles the confirmation dialog for deleting a routine.
	 */
	private OnDialogResultListener deleteRoutineDialogListener = new OnDialogResultListener()
	{
		@Override
		public void onDialogCancelled(BaseDialog<?> dialog)
		{
			// The dialog was cancelled
			dialog.dismiss();
		}

		@Override
		public void onDialogResultPositive(BaseDialog<?> dialog)
		{
			// Not used
		}

		@Override
		public void onDialogResultNegative(BaseDialog<?> dialog)
		{
			// Delete the routine from the database
			DbUtil.delete(getContentResolver(), mRoutine);

			// Dismiss the dialog and close the activity (because the routine has been deleted)
			dialog.dismiss();
			finish();
		}
	};

	// endregion

	// region RoutineLoader

	/**
	 * The loader that is used to fetch routine data from the database
	 */
	private class RoutineLoader extends AsyncTask<Routine, Void, Cursor>
	{
		@Override
		protected Cursor doInBackground(Routine... params)
		{
			// Queries the database using the provided routine id
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

			// Get the title from the cursor
			mRoutine.title = cursor.getString(cursor.getColumnIndex(RoutineTable.TITLE));

			// Get the other routine parameters
			mRoutine.cycles = cursor.getInt(cursor.getColumnIndex(RoutineTable.CYCLES));
			mRoutine.restBetweenCycles = cursor.getInt(cursor.getColumnIndex(RoutineTable.REST_BETWEEN_CYCLES));
			mRoutine.restBetweenExercises = cursor.getInt(cursor.getColumnIndex(RoutineTable.REST_BETWEEN_EXERCISES));

			// Get the exercise list
			String[] exerciseStrings = cursor.getString(cursor.getColumnIndex(RoutineTable.EXERCISES)).split("\\|");
			mRoutine.exercises = new Exercise[exerciseStrings.length];

			for (int i = 0, n = exerciseStrings.length; i < n; i++)
			{
				mRoutine.exercises[i] = new Exercise(exerciseStrings[i]);
			}

			// Set up the views using the loaded routine data
			setupContent();
		}
	}

	// endregion
}
