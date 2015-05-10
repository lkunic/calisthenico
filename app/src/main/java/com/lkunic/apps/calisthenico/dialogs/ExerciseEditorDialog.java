package com.lkunic.apps.calisthenico.dialogs;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.database.Exercise;
import com.lkunic.libs.apptoolbox.dialogs.BaseDialog;

/**
 * Copyright (c) Luka Kunic 2015 / "ExerciseEditorDialog.java"
 * Created by lkunic on 08/05/2015.
 *
 * A dialog for creating or editing exercises.
 */
public class ExerciseEditorDialog extends BaseDialog<Exercise>
{
	private Exercise mExercise;

	/**
	 * Factory method for creating a new instance of the ExerciseEditorDialog.
	 * Use this instead of calling the default constructor.
	 * @param exercise Exercise to edit, or null for creating a new exercise.
	 */
	public static ExerciseEditorDialog newInstance(Exercise exercise)
	{
		ExerciseEditorDialog dialog = new ExerciseEditorDialog();

		dialog.setExercise(exercise);

		return dialog;
	}

	public ExerciseEditorDialog()
	{
		// Required empty constructor
	}

	public void setExercise(Exercise exercise)
	{
		mExercise = exercise;
	}

	/**
	 * Implement this method to provide a layout resource to the fragment.
	 * @return Layout resource id.
	 */
	@Override
	protected int getViewResource()
	{
		return R.layout.dialog_exercise_editor;
	}

	/**
	 * Setup the content of the dialog views.
	 * @param view Parent view for the dialog fragment layout.
	 */
	@Override
	protected void setupContent(View view)
	{
		// Get view references
		final EditText etExerciseTitle = (EditText) view.findViewById(R.id.et_exercise_title);
		final EditText etExerciseReps = (EditText) view.findViewById(R.id.et_reps);
		final CheckBox cbIsTimed = (CheckBox) view.findViewById(R.id.cb_is_timed);

		if (mExercise != null)
		{
			// Populate the initial values for the exercise being edited
			etExerciseTitle.setText(mExercise.title);
			etExerciseReps.setText(mExercise.reps != 0 ? String.valueOf(mExercise.reps) : null);
			cbIsTimed.setChecked(mExercise.isTimed);
		}

		// Setup the cancel button
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				cancelDialog();
			}
		});

		// Setup the done button
		Button btnDone = (Button) view.findViewById(R.id.btn_done);
		btnDone.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (etExerciseTitle.getText().toString().length() == 0)
				{
					// The exercise title can not be empty
					Toast.makeText(getActivity(),
							getResources().getString(R.string.toast_empty_exercise_title), Toast.LENGTH_SHORT).show();
					return;
				}

				// Read the exercise values and set the dialog result
				mExercise.title = etExerciseTitle.getText().toString();
				mExercise.reps = Integer.parseInt(String.format("0%s", etExerciseReps.getText().toString()));
				mExercise.isTimed = cbIsTimed.isChecked();

				notifyResultPositive(mExercise);
			}
		});
	}
}
