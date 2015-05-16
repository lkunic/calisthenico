package com.lkunic.apps.calisthenico.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.libs.apptoolbox.views.CountdownTimerView;

/**
 * Copyright (c) Luka Kunic 2015 / "ExerciseExecutionTimeFragment.java"
 * Created by lkunic on 16/05/2015.
 *
 * Fragment that is used in the routine execution activity for displaying timed exercises.
 */
public class ExerciseExecutionTimeFragment extends AExerciseExecutionFragment
{
	private static final String ARG_EXERCISE_NAME = "exercise_name";
	private static final String ARG_TIME = "time";

	private String mExerciseName;
	private int mTime;

	public static ExerciseExecutionTimeFragment newInstance(String exerciseName, int time)
	{
		ExerciseExecutionTimeFragment fragment = new ExerciseExecutionTimeFragment();

		Bundle args = new Bundle();
		args.putString(ARG_EXERCISE_NAME, exerciseName);
		args.putInt(ARG_TIME, time);
		fragment.setArguments(args);

		return fragment;
	}

	public ExerciseExecutionTimeFragment()
	{
		// Required empty constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mExerciseName = getArguments().getString(ARG_EXERCISE_NAME);
		mTime = getArguments().getInt(ARG_TIME);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_exercise_execution_time, container, false);

		TextView txtExerciseName = (TextView) view.findViewById(R.id.txt_exercise_name);
		txtExerciseName.setText(mExerciseName);

		final CountdownTimerView countdownTimer = (CountdownTimerView) view.findViewById(R.id.exercise_countdown);
		countdownTimer.setInitialSeconds(mTime);
		countdownTimer.setOnCountdownCompletedListener(new CountdownTimerView.OnCountdownCompletedListener()
		{
			@Override
			public void onCountdownCompleted()
			{
				notifyExerciseExecutionCompleted();
			}
		});

		final Button btnDone = (Button) view.findViewById(R.id.btn_done);
		btnDone.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				countdownTimer.stopCountdown();
			}
		});

		final Button btnStart = (Button) view.findViewById(R.id.btn_start);
		btnStart.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				btnStart.setVisibility(View.INVISIBLE);
				btnDone.setVisibility(View.VISIBLE);
				countdownTimer.startCountdown();
			}
		});

		return view;
	}
}