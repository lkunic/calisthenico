package com.lkunic.apps.calisthenico.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lkunic.apps.calisthenico.R;

/**
 * Copyright (c) Luka Kunic 2015 / "ExerciseExecutionRepsFragment.java"
 * Created by lkunic on 16/05/2015.
 *
 * Fragment that is used in the routine execution activity for displaying an exercise with reps.
 */
public class ExerciseExecutionRepsFragment extends AExerciseExecutionFragment
{
	private static final String ARG_EXERCISE_NAME = "exercise_name";
	private static final String ARG_REPS = "reps";

	private String mExerciseName;
	private int mReps;

	public static ExerciseExecutionRepsFragment newInstance(String exerciseName, int reps)
	{
		ExerciseExecutionRepsFragment fragment = new ExerciseExecutionRepsFragment();

		Bundle args = new Bundle();
		args.putString(ARG_EXERCISE_NAME, exerciseName);
		args.putInt(ARG_REPS, reps);
		fragment.setArguments(args);

		return fragment;
	}

	public ExerciseExecutionRepsFragment()
	{
		// Required empty constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mExerciseName = getArguments().getString(ARG_EXERCISE_NAME);
		mReps = getArguments().getInt(ARG_REPS);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_exercise_execution_reps, container, false);

		TextView txtExerciseName = (TextView) view.findViewById(R.id.txt_exercise_name);
		txtExerciseName.setText(mExerciseName);

		TextView txtReps = (TextView) view.findViewById(R.id.txt_reps);
		txtReps.setText(String.valueOf(mReps));

		Button btnDone = (Button) view.findViewById(R.id.btn_done);
		btnDone.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				notifyExerciseExecutionCompleted();
			}
		});

		return view;
	}
}
