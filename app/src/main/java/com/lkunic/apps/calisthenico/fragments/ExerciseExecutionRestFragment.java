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
 * Copyright (c) Luka Kunic 2015 / "ExerciseExecutionRestFragment.java"
 * Created by lkunic on 16/05/2015.
 *
 * Fragment that is used to display a rest time between exercises in the routine execution activity.
 */
public class ExerciseExecutionRestFragment extends AExerciseExecutionFragment
{
	private static final String ARG_NEXT_EXERCISE_NAME = "next_exercise_name";
	private static final String ARG_TIME = "time";

	private String mNextExerciseName;
	private int mTime;

	public static ExerciseExecutionRestFragment newInstance(String nextExerciseName, int time)
	{
		ExerciseExecutionRestFragment fragment = new ExerciseExecutionRestFragment();

		Bundle args = new Bundle();
		args.putString(ARG_NEXT_EXERCISE_NAME, nextExerciseName);
		args.putInt(ARG_TIME, time);
		fragment.setArguments(args);

		return fragment;
	}

	public ExerciseExecutionRestFragment()
	{
		// Required empty constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mNextExerciseName = getArguments().getString(ARG_NEXT_EXERCISE_NAME);
		mTime = getArguments().getInt(ARG_TIME);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_exercise_execution_rest, container, false);

		TextView txtNextExerciseName = (TextView) view.findViewById(R.id.txt_next_exercise);
		txtNextExerciseName.setText(
				String.format(getResources().getString(R.string.label_next_exercise), mNextExerciseName));

		final CountdownTimerView countdownTimer = (CountdownTimerView) view.findViewById(R.id.rest_countdown);
		countdownTimer.setInitialSeconds(mTime);
		countdownTimer.setOnCountdownCompletedListener(new CountdownTimerView.OnCountdownCompletedListener()
		{
			@Override
			public void onCountdownCompleted()
			{
				notifyRestCompleted();
			}
		});

		final Button btnSkip = (Button) view.findViewById(R.id.btn_skip);
		btnSkip.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				countdownTimer.stopCountdown();
			}
		});

		countdownTimer.startCountdown();

		return view;
	}
}