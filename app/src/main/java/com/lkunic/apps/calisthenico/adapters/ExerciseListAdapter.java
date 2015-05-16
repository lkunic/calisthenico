package com.lkunic.apps.calisthenico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.database.Exercise;

import java.util.List;

/**
 * Copyright (c) Luka Kunic 2015 / "ExerciseListAdapter.java"
 * Created by lkunic on 08/05/2015.
 *
 * List adapter used for populating an exercise list.
 */
public class ExerciseListAdapter extends ArrayAdapter<Exercise>
{
	// The layout resource id
	private int mLayoutResId;

	/**
	 * Creates a new instance of the ExerciseListAdapter.
	 * @param context The current context.
	 * @param layoutResId Layout resource to use for the list rows.
	 * @param exercises List of exercises to display.
	 */
	public ExerciseListAdapter(Context context, int layoutResId, List<Exercise> exercises)
	{
		super(context, layoutResId, exercises);

		mLayoutResId = layoutResId;
	}

	/**
	 * Creates a new instance of the ExerciseListAdapter.
	 * @param context The current context.
	 * @param layoutResId Layout resource to use for the list rows.
	 * @param exercises List of exercises to display.
	 */
	public ExerciseListAdapter(Context context, int layoutResId, Exercise[] exercises)
	{
		super(context, layoutResId, exercises);

		mLayoutResId = layoutResId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if (convertView == null)
		{
			// Inflate the row
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(mLayoutResId, parent, false);

			// Store the view references into the view holder to improve list performance
			holder = new ViewHolder();
			holder.txtExerciseName = (TextView) convertView.findViewById(R.id.txt_exercise_name);
			holder.txtReps = (TextView) convertView.findViewById(R.id.txt_reps);

			// Add the view holder to the view as a tag
			convertView.setTag(holder);
		}
		else
		{
			// Get the view holder from the recycled row view
			holder = (ViewHolder) convertView.getTag();
		}

		// Setup the row values
		Exercise exercise = getItem(position);
		holder.txtExerciseName.setText(exercise.name);
		holder.txtReps.setText(exercise.isTimed ?
			String.format("%ds", exercise.reps) :
			String.valueOf(exercise.reps));

		// Add alternating backgrounds to the list items
		convertView.setBackgroundColor(position % 2 == 0 ?
			getContext().getResources().getColor(R.color.list_background_1) :
			getContext().getResources().getColor(R.color.list_background_2));

		return convertView;
	}

	/**
	 * The view holder structure containing view references for the list rows.
	 */
	private class ViewHolder
	{
		public TextView txtExerciseName;
		public TextView txtReps;
	}
}
