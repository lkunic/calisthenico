package com.lkunic.apps.calisthenico.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.adapters.ExerciseListAdapter;
import com.lkunic.apps.calisthenico.database.Exercise;
import com.lkunic.apps.calisthenico.database.Routine;
import com.lkunic.apps.calisthenico.dialogs.EditExerciseDialog;
import com.lkunic.libs.apptoolbox.database.DbUtil;
import com.lkunic.libs.apptoolbox.dialogs.BaseDialog;
import com.lkunic.libs.apptoolbox.dialogs.EditTextDialog;
import com.lkunic.libs.apptoolbox.dialogs.OnDialogResultListener;
import com.lkunic.libs.apptoolbox.views.SortableListView;
import com.lkunic.libs.apptoolbox.views.TextButton;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RoutineCreateActivity extends AppCompatActivity
{
	private static final String DIALOG_TAG_CYCLES = "set_cycles_dialog";
	private static final String DIALOG_TAG_REST_CYCLES = "set_rest_between_cycles_dialog";
	private static final String DIALOG_TAG_REST_EXERCISES = "set_rest_between_exercises_dialog";
	private static final String DIALOG_TAG_ADD_EXERCISE = "add_exercise_dialog";
	private static final String DIALOG_TAG_EDIT_EXERCISE = "edit_exercise_dialog";

	private static final String PATTERN_REST_TIME = "%ds";

	private int mCycles;
	private int mRestBetweenCycles;
	private int mRestBetweenExercises;

	private EditText mEtRoutineTitle;
	private TextButton mTbCycles;
	private TextButton mTbRestCycles;
	private TextButton mTbRestExercises;
	private SortableListView mLvExercises;

	private List<Exercise> mExercises;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_routine);

		mExercises = new ArrayList<>();

		mEtRoutineTitle = (EditText) findViewById(R.id.et_title);
		setupTextButtons();
		setupExerciseList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_create_routine, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.action_done)
		{
			// Save the routine
			saveRoutine();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// region Private support methods

	private void setupTextButtons()
	{
		mTbCycles = (TextButton) findViewById(R.id.tb_cycles);
		mTbCycles.setPrimaryText(String.valueOf(mCycles));
		mTbCycles.findViewById(R.id.container).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
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

		mTbRestCycles = (TextButton) findViewById(R.id.tb_rest_cycles);
		mTbRestCycles.setPrimaryText(String.format(PATTERN_REST_TIME, mRestBetweenCycles));
		mTbRestCycles.findViewById(R.id.container).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				EditTextDialog dialog = EditTextDialog.newInstance(
						mRestBetweenCycles != 0 ? String.valueOf(mRestBetweenCycles) : null,
						getResources().getString(R.string.hint_rest_between_cycles),
						InputType.TYPE_CLASS_NUMBER);

				dialog.setDialogTitle(getResources().getString(R.string.dialog_rest_between_cycles));
				dialog.setDialogResultListener(onTextButtonDialogResultListener);
				dialog.setDialogTag(DIALOG_TAG_REST_CYCLES);

				dialog.display(getSupportFragmentManager());
			}
		});

		mTbRestExercises = (TextButton) findViewById(R.id.tb_rest_exercises);
		mTbRestExercises.setPrimaryText(String.format(PATTERN_REST_TIME, mRestBetweenExercises));
		mTbRestExercises.findViewById(R.id.container).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
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
	}

	private void setupExerciseList()
	{
		FloatingActionButton fabAddExercise = (FloatingActionButton) findViewById(R.id.btn_add_exercise);
		fabAddExercise.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				EditExerciseDialog dialog = EditExerciseDialog.newInstance(new Exercise());
				dialog.setDialogTitle(getResources().getString(R.string.dialog_add_exercise));
				dialog.setDialogResultListener(onEditExerciseDialogResultListener);
				dialog.setDialogTag(DIALOG_TAG_ADD_EXERCISE);

				dialog.display(getSupportFragmentManager());
			}
		});

		mLvExercises = (SortableListView) findViewById(R.id.lv_exercises);
		mLvExercises.setAdapter(new ExerciseListAdapter(this, mExercises));
		mLvExercises.setDropListener(new SortableListView.DropListener()
		{
			@Override
			public void drop(int from, int to)
			{
				if (to == from)
				{
					// Dropped to same location
					return;
				}

				reorderExercises(from, to);
			}
		});

		mLvExercises.setRemoveListener(new SortableListView.RemoveListener()
		{
			@Override
			public void remove(int which)
			{
				mExercises.remove(which);
				notifyExerciseDataChanged();
			}
		});

		mLvExercises.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				EditExerciseDialog dialog = EditExerciseDialog.newInstance(mExercises.get(position));
				dialog.setDialogTitle(getResources().getString(R.string.dialog_add_exercise));
				dialog.setDialogResultListener(onEditExerciseDialogResultListener);
				dialog.setDialogTag(DIALOG_TAG_EDIT_EXERCISE + "#" + position);

				dialog.display(getSupportFragmentManager());
			}
		});
	}

	private void reorderExercises(int from, int to)
	{
		mExercises.add(to, mExercises.get(from));

		if (to < from)
		{
			mExercises.remove(from + 1);
		}
		else
		{
			mExercises.remove(from);
		}

		notifyExerciseDataChanged();
	}

	private void notifyExerciseDataChanged()
	{
		if (mLvExercises != null)
		{
			((ExerciseListAdapter)mLvExercises.getAdapter()).notifyDataSetChanged();
		}
	}

	private void saveRoutine()
	{
		Routine routine = new Routine();

		routine.title = mEtRoutineTitle.getText().toString();
		routine.cycles = mCycles;
		routine.restBetweenCycles = mRestBetweenCycles;
		routine.restBetweenExercises = mRestBetweenExercises;
		routine.exercises = mExercises.toArray(new Exercise[mExercises.size()]);

		DbUtil.insert(getContentResolver(), routine);
		finish();
	}

	// endregion

	// region Dialog result listeners

	private OnDialogResultListener onTextButtonDialogResultListener = new OnDialogResultListener()
	{
		@Override
		public void onDialogCancelled(BaseDialog<?> dialog)
		{
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

			dialog.dismiss();
		}

		@Override
		public void onDialogResultNegative(BaseDialog<?> dialog)
		{
			// Not used
		}
	};

	private OnDialogResultListener onEditExerciseDialogResultListener = new OnDialogResultListener()
	{
		@Override
		public void onDialogCancelled(BaseDialog<?> dialog)
		{
			dialog.dismiss();
		}

		@Override
		public void onDialogResultPositive(BaseDialog<?> dialog)
		{
			EditExerciseDialog eeDialog = (EditExerciseDialog) dialog;
			String tag = eeDialog.getDialogTag();

			if (tag.equals(DIALOG_TAG_ADD_EXERCISE))
			{
				mExercises.add(eeDialog.getDialogResult());
			}
			else if (tag.contains(DIALOG_TAG_EDIT_EXERCISE))
			{
				int position = Integer.parseInt(tag.split("#")[1]);
				mExercises.set(position, eeDialog.getDialogResult());
			}

			notifyExerciseDataChanged();
			dialog.dismiss();
		}

		@Override
		public void onDialogResultNegative(BaseDialog<?> dialog)
		{
		}
	};

	// endregion
}
