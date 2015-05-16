package com.lkunic.apps.calisthenico.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.adapters.ExerciseListAdapter;
import com.lkunic.apps.calisthenico.database.Exercise;
import com.lkunic.apps.calisthenico.database.Routine;
import com.lkunic.apps.calisthenico.database.RoutineTable;
import com.lkunic.apps.calisthenico.dialogs.ExerciseEditorDialog;
import com.lkunic.libs.apptoolbox.database.DbQuery;
import com.lkunic.libs.apptoolbox.database.DbUtil;
import com.lkunic.libs.apptoolbox.dialogs.BaseDialog;
import com.lkunic.libs.apptoolbox.dialogs.EditTextDialog;
import com.lkunic.libs.apptoolbox.dialogs.OnDialogResultListener;
import com.lkunic.libs.apptoolbox.views.SortableListView;
import com.lkunic.libs.apptoolbox.views.TextButton;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Luka Kunic 2015 / "RoutineEditorActivity.java"
 * Created by lkunic on 08/05/2015.
 *
 * Activity that allows creating and editing routines.
 */
public class RoutineEditorActivity extends AppCompatActivity
{
	// Argument key for the routine id (used for editing)
	public static final String ARG_ROUTINE_ID = "routine_id";

	// Dialog tags used for differentiating between dialog results
	private static final String DIALOG_TAG_CYCLES = "set_cycles_dialog";
	private static final String DIALOG_TAG_REST_CYCLES = "set_rest_between_cycles_dialog";
	private static final String DIALOG_TAG_REST_EXERCISES = "set_rest_between_exercises_dialog";
	private static final String DIALOG_TAG_ADD_EXERCISE = "add_exercise_dialog";
	private static final String DIALOG_TAG_EDIT_EXERCISE = "edit_exercise_dialog";

	// String pattern used for displaying time
	private static final String PATTERN_REST_TIME = "%ds";

	// Values for the routine parameters
	private Routine mRoutine;
	private int mCycles;
	private int mRestBetweenCycles;
	private int mRestBetweenExercises;
	private List<Exercise> mExercises;

	// View references
	private EditText mEtRoutineTitle;
	private TextButton mTbCycles;
	private TextButton mTbRestCycles;
	private TextButton mTbRestExercises;
	private SortableListView mLvExercises;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routine_editor);

		// Initialize the exercise list
		mExercises = new ArrayList<>();

		// Get view references and setup view behavior
		setupContent();

		// See if a routine id has been provided as an intent extra, which would mean that a routine is being edited
		mRoutine = new Routine();
		mRoutine.id = getIntent().getLongExtra(ARG_ROUTINE_ID, -1);

		if (mRoutine.id != -1)
		{
			// An id is available, get the routine and populate the views with initial values
			new RoutineLoader().execute(mRoutine);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_routine_editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.action_done)
		{
			if (mExercises.size() == 0)
			{
				// No exercises have been added, don't do anything
				Toast.makeText(getBaseContext(),
						getResources().getString(R.string.toast_empty_exercises), Toast.LENGTH_SHORT).show();
			}
			else if (mEtRoutineTitle.getText().length() == 0)
			{
				// The routine name is empty, don't do anything
				Toast.makeText(getBaseContext(),
						getResources().getString(R.string.toast_empty_routine_title), Toast.LENGTH_SHORT).show();
			}
			else
			{
				// Save the routine and close the activity
				saveRoutine();
				finish();
			}

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// region Private support methods

	/**
	 * Get references to the views and set up the view behavior.
	 */
	private void setupContent()
	{
		// Get reference to the name view
		mEtRoutineTitle = (EditText) findViewById(R.id.et_title);

		// Setup the cycle count view
		mTbCycles = (TextButton) findViewById(R.id.tb_cycles);
		mTbCycles.setPrimaryText(String.valueOf(mCycles));
		mTbCycles.findViewById(R.id.container).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Display a dialog for setting the value
				EditTextDialog dialog = EditTextDialog.newInstance(
						mCycles != 0 ? String.valueOf(mCycles) : null,
						getResources().getString(R.string.hint_cycles),
						InputType.TYPE_CLASS_NUMBER);

				dialog.setDialogTitle(getResources().getString(R.string.dialog_cycles));
				dialog.setDialogResultListener(onTextButtonDialogResultListener);
				dialog.setDialogTag(DIALOG_TAG_CYCLES);

				dialog.display(getSupportFragmentManager());
			}
		});

		// Setup the cycle rest duration view
		mTbRestCycles = (TextButton) findViewById(R.id.tb_rest_cycles);
		mTbRestCycles.setPrimaryText(String.format(PATTERN_REST_TIME, mRestBetweenCycles));
		mTbRestCycles.findViewById(R.id.container).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				EditTextDialog dialog = EditTextDialog.newInstance(
						// Display a dialog for setting the value
						mRestBetweenCycles != 0 ? String.valueOf(mRestBetweenCycles) : null,
						getResources().getString(R.string.hint_rest_between_cycles),
						InputType.TYPE_CLASS_NUMBER);

				dialog.setDialogTitle(getResources().getString(R.string.dialog_rest_between_cycles));
				dialog.setDialogResultListener(onTextButtonDialogResultListener);
				dialog.setDialogTag(DIALOG_TAG_REST_CYCLES);

				dialog.display(getSupportFragmentManager());
			}
		});

		// Setup the exercise rest duration view
		mTbRestExercises = (TextButton) findViewById(R.id.tb_rest_exercises);
		mTbRestExercises.setPrimaryText(String.format(PATTERN_REST_TIME, mRestBetweenExercises));
		mTbRestExercises.findViewById(R.id.container).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Display a dialog for setting the value
				EditTextDialog dialog = EditTextDialog.newInstance(
						mRestBetweenExercises != 0 ? String.valueOf(mRestBetweenExercises) : null,
						getResources().getString(R.string.hint_rest_between_exercises),
						InputType.TYPE_CLASS_NUMBER);

				dialog.setDialogTitle(getResources().getString(R.string.dialog_rest_between_exercises));
				dialog.setDialogResultListener(onTextButtonDialogResultListener);
				dialog.setDialogTag(DIALOG_TAG_REST_EXERCISES);

				dialog.display(getSupportFragmentManager());
			}
		});

		// Setup the 'Add exercise' floating action button
		FloatingActionButton fabAddExercise = (FloatingActionButton) findViewById(R.id.btn_add_exercise);
		fabAddExercise.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Display a dialog for adding an exercise
				ExerciseEditorDialog dialog = ExerciseEditorDialog.newInstance(new Exercise());
				dialog.setDialogTitle(getResources().getString(R.string.dialog_add_exercise));
				dialog.setDialogResultListener(onEditExerciseDialogResultListener);
				dialog.setDialogTag(DIALOG_TAG_ADD_EXERCISE);

				dialog.display(getSupportFragmentManager());
			}
		});

		// Setup the exercise list view
		mLvExercises = (SortableListView) findViewById(R.id.lv_exercises);
		mLvExercises.setAdapter(new ExerciseListAdapter(this, R.layout.row_add_exercise, mExercises));
		mLvExercises.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// Display a dialog for editing the exercise
				ExerciseEditorDialog dialog = ExerciseEditorDialog.newInstance(mExercises.get(position));
				dialog.setDialogTitle(getResources().getString(R.string.dialog_add_exercise));
				dialog.setDialogResultListener(onEditExerciseDialogResultListener);
				dialog.setDialogTag(DIALOG_TAG_EDIT_EXERCISE + "#" + position);

				dialog.display(getSupportFragmentManager());
			}
		});
		mLvExercises.setDropListener(new SortableListView.DropListener()
		{
			@Override
			public void drop(int from, int to)
			{
				if (to != from)
				{
					// The exercise position changed by dragging and dropping, reorder the exercises in the list
					reorderExercises(from, to);
					notifyExerciseDataChanged();
				}
			}
		});
		mLvExercises.setRemoveListener(new SortableListView.RemoveListener()
		{
			@Override
			public void remove(int which)
			{
				// Remove the exercise from the list
				mExercises.remove(which);
				notifyExerciseDataChanged();
			}
		});
	}

	/**
	 * Changes the location of an exercise in the list.
	 * @param from Initial position of the exercise.
	 * @param to Position to which the exercise needs to be moved.
	 */
	private void reorderExercises(int from, int to)
	{
		// Add the exercise to the new position
		mExercises.add(to, mExercises.get(from));

		// Remove the old instance of the exercise
		if (to < from)
		{
			// Increase index by 1 since the inserted exercise caused this one to be pushed down
			mExercises.remove(from + 1);
		}
		else
		{
			mExercises.remove(from);
		}

		// Notify the list adapter about the change
		notifyExerciseDataChanged();
	}

	/**
	 * Notifies the exercise list adapter that the underlying list object has changed
	 * and that the list view needs to be updated with the new values.
	 */
	private void notifyExerciseDataChanged()
	{
		if (mLvExercises != null)
		{
			((ExerciseListAdapter)mLvExercises.getAdapter()).notifyDataSetChanged();
		}
	}

	/**
	 * Saves the routine to the database (updates existing entry if editing).
	 */
	private void saveRoutine()
	{
		// Read the routine values from the views
		mRoutine.title = mEtRoutineTitle.getText().toString();
		mRoutine.cycles = mCycles;
		mRoutine.restBetweenCycles = mRestBetweenCycles;
		mRoutine.restBetweenExercises = mRestBetweenExercises;
		mRoutine.exercises = mExercises.toArray(new Exercise[mExercises.size()]);

		if (mRoutine.id == -1)
		{
			// Insert the new routine into the routine table
			DbUtil.insert(getContentResolver(), mRoutine);
		}
		else
		{
			// Update the existing routine entry
			DbUtil.update(getContentResolver(), mRoutine);
		}
	}

	// endregion

	// region Dialog result listeners

	/**
	 * Listener used for setting cycle count and rest times values from corresponding dialogs.
	 */
	private OnDialogResultListener onTextButtonDialogResultListener = new OnDialogResultListener()
	{
		@Override
		public void onDialogCancelled(BaseDialog<?> dialog)
		{
			// The dialog was cancelled, dismiss it
			dialog.dismiss();
		}

		@Override
		public void onDialogResultPositive(BaseDialog<?> dialog)
		{
			EditTextDialog etDialog = (EditTextDialog) dialog;
			String dialogResult = etDialog.getDialogResult();

			switch (etDialog.getDialogTag())
			{
				case DIALOG_TAG_CYCLES:
					mCycles = Integer.parseInt(dialogResult);
					mTbCycles.setPrimaryText(String.valueOf(mCycles));
					break;

				case DIALOG_TAG_REST_CYCLES:
					mRestBetweenCycles = Integer.parseInt(dialogResult);
					mTbRestCycles.setPrimaryText(String.format(PATTERN_REST_TIME, mRestBetweenCycles));
					break;

				case DIALOG_TAG_REST_EXERCISES:
					mRestBetweenExercises = Integer.parseInt(dialogResult);
					mTbRestExercises.setPrimaryText(String.format(PATTERN_REST_TIME, mRestBetweenExercises));
					break;
			}

			// Dismiss the dialog
			dialog.dismiss();
		}

		@Override
		public void onDialogResultNegative(BaseDialog<?> dialog)
		{
			// Not used
		}
	};

	/**
	 * Listener used for handling results from the ExerciseEditorDialog.
	 */
	private OnDialogResultListener onEditExerciseDialogResultListener = new OnDialogResultListener()
	{
		@Override
		public void onDialogCancelled(BaseDialog<?> dialog)
		{
			// The dialog was cancelled, dismiss it
			dialog.dismiss();
		}

		@Override
		public void onDialogResultPositive(BaseDialog<?> dialog)
		{
			ExerciseEditorDialog eeDialog = (ExerciseEditorDialog) dialog;
			String tag = eeDialog.getDialogTag();

			if (tag.equals(DIALOG_TAG_ADD_EXERCISE))
			{
				// The dialog was used to add a new exercise
				mExercises.add(eeDialog.getDialogResult());
			}
			else if (tag.contains(DIALOG_TAG_EDIT_EXERCISE))
			{
				// The dialog was used to edit an exercise, get the exercise position and update it
				int position = Integer.parseInt(tag.split("#")[1]);
				mExercises.set(position, eeDialog.getDialogResult());
			}

			// Notify the list adapter about the change
			notifyExerciseDataChanged();
			dialog.dismiss();
		}

		@Override
		public void onDialogResultNegative(BaseDialog<?> dialog)
		{
			// Not used
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

			// Set the name
			mEtRoutineTitle.setText(cursor.getString(cursor.getColumnIndex(RoutineTable.TITLE)));

			// Fetch the routine parameters
			mCycles = cursor.getInt(cursor.getColumnIndex(RoutineTable.CYCLES));
			mRestBetweenCycles = cursor.getInt(cursor.getColumnIndex(RoutineTable.REST_BETWEEN_CYCLES));
			mRestBetweenExercises = cursor.getInt(cursor.getColumnIndex(RoutineTable.REST_BETWEEN_EXERCISES));

			// Update views for the routine parameters
			mTbCycles.setPrimaryText(String.valueOf(mCycles));
			mTbRestCycles.setPrimaryText(String.valueOf(mRestBetweenCycles));
			mTbRestExercises.setPrimaryText(String.valueOf(mRestBetweenExercises));

			// Parse the exercise string and populate the exercise list
			String[] exerciseStrings = cursor.getString(cursor.getColumnIndex(RoutineTable.EXERCISES)).split("\\|");
			mRoutine.exercises = new Exercise[exerciseStrings.length];

			for (String exerciseString : exerciseStrings)
			{
				mExercises.add(new Exercise(exerciseString));
			}

			// Notify the list adapter about the change
			notifyExerciseDataChanged();
		}
	}

	// endregion
}
