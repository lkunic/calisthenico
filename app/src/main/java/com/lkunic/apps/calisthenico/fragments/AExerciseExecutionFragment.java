package com.lkunic.apps.calisthenico.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Copyright (c) Luka Kunic 2015 / "AExerciseExecutionFragment.java"
 * Created by lkunic on 16/05/2015.
 *
 * Base class for exercise execution fragments.
 */
public abstract class AExerciseExecutionFragment extends Fragment
{
	private OnExerciseExecutionCompletedListener mListener;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		if (activity instanceof OnExerciseExecutionCompletedListener)
		{
			mListener = (OnExerciseExecutionCompletedListener) activity;
		}
		else
		{
			throw new ClassCastException("Parent activity must implement OnExerciseExecutionCompletedListener");
		}
	}

	protected void notifyExerciseExecutionCompleted()
	{
		if (mListener != null)
		{
			mListener.onExerciseExecutionCompleted();
		}
	}

	protected void notifyRestCompleted()
	{
		if (mListener != null)
		{
			mListener.onRestCompleted();
		}
	}

	public interface OnExerciseExecutionCompletedListener
	{
		void onExerciseExecutionCompleted();

		void onRestCompleted();
	}
}
