package com.lkunic.apps.calisthenico.database;

import android.content.ContentValues;
import android.net.Uri;

import com.lkunic.libs.apptoolbox.database.DatabaseTable;
import com.lkunic.libs.apptoolbox.database.IQueryable;

/**
 * Copyright (c) Luka Kunic 2015 / "Routine.java"
 * Created by lkunic on 03/05/2015.
 */
public class Routine implements IQueryable
{
	private static final String URI_PATH = "routines";

	public static final Uri URI = Uri.parse(String.format("content://%s/%s", CalisthenicoContentProvider.AUTHORITY, URI_PATH));

	public long id;
	public String title;
	public int cycles;
	public int exerciseCount;
	public Exercise[] exercises;
	public int restBetweenExercises;
	public int restBetweenCycles;

	public Routine()
	{
		cycles = restBetweenCycles = restBetweenExercises = -1;
	}

	/**
	 * Returns content values for this item instance.
	 */
	@Override
	public ContentValues getContentValues()
	{
		ContentValues values = new ContentValues();

		if (title != null)
		{
			values.put(RoutineTable.TITLE, title);
		}

		if (cycles != -1)
		{
			values.put(RoutineTable.CYCLES, cycles);
		}

		if (exercises != null && exercises.length != 0)
		{
			values.put(RoutineTable.EXERCISES, exercisesToString());
			values.put(RoutineTable.EXERCISE_COUNT, exercises.length);
		}
		else if (exerciseCount != -1)
		{
			values.put(RoutineTable.EXERCISE_COUNT, exerciseCount);
		}

		if (restBetweenCycles != -1)
		{
			values.put(RoutineTable.REST_BETWEEN_CYCLES, restBetweenCycles);
		}

		if (restBetweenExercises != -1)
		{
			values.put(RoutineTable.REST_BETWEEN_EXERCISES, restBetweenExercises);
		}

		return values;
	}

	/**
	 * Returns the collection level uri (only contains the main part of the uri for this item type).
	 */
	@Override
	public Uri getCollectionUri()
	{
		return URI;
	}

	/**
	 * Returns the item level uri (contains the item id appended to the end of the uri).
	 */
	@Override
	public Uri getItemUri()
	{
		return Uri.withAppendedPath(URI, String.valueOf(id));
	}

	/**
	 * Returns a path for this item type that will be included in the uri.
	 */
	@Override
	public String getUriPath()
	{
		return URI_PATH;
	}

	/**
	 * Returns the database table object representing this IQueryable.
	 */
	@Override
	public DatabaseTable getDatabaseTable()
	{
		return new RoutineTable();
	}

	/**
	 * Returns a string representation of the exercise array.
	 */
	private String exercisesToString()
	{
		StringBuilder builder = new StringBuilder();

		for (int i = 0, n = exercises.length; i < n; i++)
		{
			builder.append(exercises[i].toString());

			if (i != n - 1)
			{
				builder.append("|");
			}
		}

		return builder.toString();
	}
}
