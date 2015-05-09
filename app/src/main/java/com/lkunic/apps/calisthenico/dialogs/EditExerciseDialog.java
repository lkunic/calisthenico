package com.lkunic.apps.calisthenico.dialogs;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.database.Exercise;
import com.lkunic.libs.apptoolbox.dialogs.BaseDialog;

/**
 * Copyright (c) Luka Kunic 2015 / "EditExerciseDialog.java"
 * Created by lkunic on 08/05/2015.
 */
public class EditExerciseDialog extends BaseDialog<Exercise>
{
	private Exercise mExercise;

	public static EditExerciseDialog newInstance(Exercise exercise)
	{
		EditExerciseDialog dialog = new EditExerciseDialog();

		dialog.setExercise(exercise);

		return dialog;
	}

	public EditExerciseDialog()
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
		return R.layout.dialog_edit_exercise;
	}

	/**
	 * Setup the content of the dialog views.
	 * @param view Parent view for the dialog fragment layout.
	 */
	@Override
	protected void setupContent(View view)
	{
		final EditText etExerciseTitle = (EditText) view.findViewById(R.id.et_exercise_title);
		final EditText etExerciseReps = (EditText) view.findViewById(R.id.et_reps);
		final CheckBox cbIsTimed = (CheckBox) view.findViewById(R.id.cb_is_timed);

		if (mExercise != null)
		{
			etExerciseTitle.setText(mExercise.title);
			etExerciseReps.setText(String.valueOf(mExercise.reps));
			cbIsTimed.setChecked(mExercise.isTimed);
		}

		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				cancelDialog();
			}
		});

		Button btnDone = (Button) view.findViewById(R.id.btn_done);
		btnDone.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mExercise.title = etExerciseTitle.getText().toString();
				mExercise.reps = Integer.parseInt(etExerciseReps.getText().toString());
				mExercise.isTimed = cbIsTimed.isChecked();

				notifyResultPositive(mExercise);
			}
		});
	}
}
