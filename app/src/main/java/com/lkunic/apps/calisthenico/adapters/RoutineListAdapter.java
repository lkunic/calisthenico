package com.lkunic.apps.calisthenico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lkunic.apps.calisthenico.R;
import com.lkunic.apps.calisthenico.database.Routine;

import java.util.List;

/**
 * Copyright (c) Luka Kunic 2015 / "RoutineListAdapter.java"
 * Created by lkunic on 09/05/2015.
 *
 * List adapter for populating a routine list.
 */
public class RoutineListAdapter extends ArrayAdapter<Routine>
{
	/**
	 * Creates a new instance of the RoutineListAdapter.
	 * @param context The current context.
	 * @param routines List of routines to display.
	 */
	public RoutineListAdapter(Context context, List<Routine> routines)
	{
		super(context, R.layout.row_routine, routines);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if (convertView == null)
		{
			// Inflate the row
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_routine, parent, false);

			// Store the view references into the view holder to improve list performance
			holder = new ViewHolder();
			holder.txtRoutineTitle = (TextView) convertView.findViewById(R.id.txt_title);
			holder.txtCycles = (TextView) convertView.findViewById(R.id.txt_cycles);
			holder.txtExerciseCount = (TextView) convertView.findViewById(R.id.txt_exercises);

			// Add the view holder to the view as a tag
			convertView.setTag(holder);
		}
		else
		{
			// Get the view holder from the recycled row view
			holder = (ViewHolder) convertView.getTag();
		}

		// Setup the row values
		Routine routine = getItem(position);
		holder.txtRoutineTitle.setText(routine.title);
		holder.txtCycles.setText(String.valueOf(routine.cycles));
		holder.txtExerciseCount.setText(String.valueOf(routine.exerciseCount));

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
		public TextView txtRoutineTitle;
		public TextView txtCycles;
		public TextView txtExerciseCount;
	}
}