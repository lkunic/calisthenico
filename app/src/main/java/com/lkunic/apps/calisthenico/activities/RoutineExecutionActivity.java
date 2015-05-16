package com.lkunic.apps.calisthenico.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.database.Exercise;
import com.lkunic.apps.calisthenico.database.Routine;
import com.lkunic.apps.calisthenico.database.RoutineTable;
import com.lkunic.apps.calisthenico.fragments.AExerciseExecutionFragment;
import com.lkunic.apps.calisthenico.fragments.ExerciseExecutionRepsFragment;
import com.lkunic.apps.calisthenico.fragments.ExerciseExecutionRestFragment;
import com.lkunic.apps.calisthenico.fragments.ExerciseExecutionTimeFragment;
import com.lkunic.libs.apptoolbox.database.DbQuery;

public class RoutineExecutionActivity extends AppCompatActivity
		implements AExerciseExecutionFragment.OnExerciseExecutionCompletedListener
{
	public static final String ARG_ROUTINE_ID = "routine_id";

	private static final String ARG_CURRENT_CYCLE = "current_cycle";
	private static final String ARG_CURRENT_EXERCISE = "current_exercise";

	private int mCurrentCycle;
	private int mCurrentExercise;
	private Routine mRoutine;
	private boolean mCanUpdate;
	private Fragment mFragmentBacklog;

	private TextView mTxtCycles;
	private TextView mTxtExercises;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routine_execution);

		mRoutine = new Routine();
		mRoutine.id = getIntent().getLongExtra(ARG_ROUTINE_ID, -1);

		new RoutineLoader().execute(mRoutine);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mCanUpdate = false;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mCanUpdate = true;

		if (mFragmentBacklog != null)
		{
			getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragmentBacklog).commit();
			mFragmentBacklog = null;
		}
	}

	private void setupContent()
	{
		setTitle(mRoutine.title);

		mTxtCycles = (TextView) findViewById(R.id.txt_cycles);
		mTxtExercises = (TextView) findViewById(R.id.txt_exercises);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mProgressBar.setMax(mRoutine.cycles * mRoutine.exerciseCount);

		mCurrentCycle = 1;
		mCurrentExercise = 1;
		startNextExercise();
	}

	private void startNextExercise()
	{
		mTxtCycles.setText(String.format("%d/%d", mCurrentCycle, mRoutine.cycles));
		mTxtExercises.setText(String.format("%d/%d", mCurrentExercise, mRoutine.exerciseCount));
		mProgressBar.setProgress((mCurrentCycle - 1) * mRoutine.exerciseCount + mCurrentExercise);

		displayNextExerciseFragment();
	}

	private void displayNextExerciseFragment()
	{
		Exercise exercise = mRoutine.exercises[mCurrentExercise - 1];

		AExerciseExecutionFragment exerciseFragment;

		if (exercise.isTimed)
		{
			exerciseFragment = ExerciseExecutionTimeFragment.newInstance(exercise.name, exercise.reps);
		}
		else
		{
			exerciseFragment = ExerciseExecutionRepsFragment.newInstance(exercise.name, exercise.reps);
		}

		if (mCanUpdate)
		{
			getSupportFragmentManager().beginTransaction().replace(R.id.container, exerciseFragment).commit();
		}
		else
		{
			mFragmentBacklog = exerciseFragment;
		}
	}

	private void displayRestFragment()
	{
		Exercise nextExercise = mRoutine.exercises[mCurrentExercise - 1];
		int restTime = mCurrentExercise == 1 ? mRoutine.restBetweenCycles : mRoutine.restBetweenExercises;

		ExerciseExecutionRestFragment restFragment = ExerciseExecutionRestFragment.newInstance(nextExercise.name, restTime);

		if (mCanUpdate)
		{
			getSupportFragmentManager().beginTransaction().replace(R.id.container, restFragment).commit();
		}
		else
		{
			mFragmentBacklog = restFragment;
		}
	}

	@Override
	public void onExerciseExecutionCompleted()
	{
		if (mCurrentCycle == mRoutine.cycles && mCurrentExercise == mRoutine.exerciseCount)
		{
			// Routine completed
			finish();
			return;
		}

		if (mCurrentExercise == mRoutine.exerciseCount)
		{
			mCurrentCycle++;
			mCurrentExercise = 1;
		}
		else
		{
			mCurrentExercise++;
		}

		displayRestFragment();
	}

	@Override
	public void onRestCompleted()
	{
		startNextExercise();
	}

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

			// Get the name from the cursor
			mRoutine.title = cursor.getString(cursor.getColumnIndex(RoutineTable.TITLE));

			// Get the other routine parameters
			mRoutine.cycles = cursor.getInt(cursor.getColumnIndex(RoutineTable.CYCLES));
			mRoutine.restBetweenCycles = cursor.getInt(cursor.getColumnIndex(RoutineTable.REST_BETWEEN_CYCLES));
			mRoutine.restBetweenExercises = cursor.getInt(cursor.getColumnIndex(RoutineTable.REST_BETWEEN_EXERCISES));

			// Get the exercise list
			String[] exerciseStrings = cursor.getString(cursor.getColumnIndex(RoutineTable.EXERCISES)).split("\\|");
			mRoutine.exerciseCount = exerciseStrings.length;
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
