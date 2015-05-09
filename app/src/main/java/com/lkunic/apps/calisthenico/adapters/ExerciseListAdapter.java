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
 */
public class ExerciseListAdapter extends ArrayAdapter<Exercise>
{
	/**
	 * Constructor
	 * @param context The current context.
	 * @param objects The objects to represent in the ListView.
	 */
	public ExerciseListAdapter(Context context, List<Exercise> objects)
	{
		super(context, R.layout.row_exercise, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_exercise, parent, false);

			holder = new ViewHolder();
			holder.txtExerciseTitle = (TextView) convertView.findViewById(R.id.txt_exercise_title);
			holder.txtReps = (TextView) convertView.findViewById(R.id.txt_reps);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		Exercise exercise = getItem(position);

		holder.txtExerciseTitle.setText(exercise.title);
		holder.txtReps.setText(exercise.isTimed ?
			String.format("%ds", exercise.reps) :
			String.valueOf(exercise.reps));

		return convertView;
	}

	private class ViewHolder
	{
		TextView txtExerciseTitle;
		TextView txtReps;
	}
}
