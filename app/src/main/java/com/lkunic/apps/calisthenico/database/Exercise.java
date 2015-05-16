package com.lkunic.apps.calisthenico.database;

/**
 * Copyright (c) Luka Kunic 2015 / "Exercise.java"
 * Created by lkunic on 03/05/2015.
 *
 * Structure containing exercise data.
 */
public class Exercise
{
	public String name;
	public int reps;
	public boolean isTimed;

	/**
	 * Creates a new Exercise object with default values.
	 */
	public Exercise()
	{
		this(null, 0, false);
	}

	/**
	 * Creates a new Exercise object with the given values.
	 * @param name Exercise name.
	 * @param reps Exercise reps / time.
	 * @param isTimed Whether the exercise is time-based.
	 */
	public Exercise(String name, int reps, boolean isTimed)
	{
		this.name = name;
		this.reps = reps;
		this.isTimed = isTimed;
	}

	/**
	 * Creates a new Exercise object by parsing the given string.
	 * @param exercise String representation of the exercise (Title;Reps;[1,0]).
	 */
	public Exercise(String exercise)
	{
		String[] split = exercise.split(";");

		if (split.length != 3)
		{
			// The exercise string is not valid
			throw new IllegalArgumentException("Exercise creation string is invalid: " + exercise);
		}

		// Read the values from the split string
		name = split[0];
		reps = Integer.parseInt(split[1]);
		isTimed = split[2].equals("1");
	}

	@Override
	public String toString()
	{
		// Return the string representation of the exercise
		return String.format("%s;%d;%d", name, reps, isTimed ? 1 : 0);
	}
}
