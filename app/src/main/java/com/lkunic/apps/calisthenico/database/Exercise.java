package com.lkunic.apps.calisthenico.database;

/**
 * Copyright (c) Luka Kunic 2015 / "Exercise.java"
 * Created by lkunic on 03/05/2015.
 *
 * Structure containing exercise data.
 */
public class Exercise
{
	public String title;
	public int reps;
	public boolean isTimed;

	public Exercise(String title, int reps, boolean isTimed)
	{
		this.title = title;
		this.reps = reps;
		this.isTimed = isTimed;
	}

	public Exercise(String exercise)
	{
		String[] split = exercise.split(";");

		if (split.length != 3)
		{
			throw new IllegalArgumentException("Exercise creation string is invalid: " + exercise);
		}

		title = split[0];
		reps = Integer.parseInt(split[1]);
		isTimed = split[2] == "1";
	}

	@Override
	public String toString()
	{
		return String.format("%s;%d;%d", title, reps, isTimed ? 1 : 0);
	}
}
