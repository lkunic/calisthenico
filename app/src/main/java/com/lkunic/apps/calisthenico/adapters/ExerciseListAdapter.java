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
	private int layoutResId;
	private boolean alternateRowBackground;

	public ExerciseListAdapter(Context context, List<Exercise> exercises)
	{
		super(context, R.layout.row_add_exercise, exercises);

		layoutResId = R.layout.row_add_exercise;
		alternateRowBackground = false;
	}

	public ExerciseListAdapter(Context context, Exercise[] exercises)
	{
		super(context, R.layout.row_view_exercise, exercises);

		layoutResId = R.layout.row_view_exercise;
		alternateRowBackground = true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layoutResId, parent, false);

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

		if (alternateRowBackground)
		{
			convertView.setBackgroundColor(position % 2 == 0 ?
				getContext().getResources().getColor(R.color.list_background_1) :
				getContext().getResources().getColor(R.color.list_background_2));
		}

		return convertView;
	}

	private class ViewHolder
	{
		public TextView txtExerciseTitle;
		public TextView txtReps;
	}
}
