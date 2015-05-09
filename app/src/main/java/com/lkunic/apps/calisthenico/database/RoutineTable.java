package com.lkunic.apps.calisthenico.database;

import com.lkunic.libs.apptoolbox.database.DatabaseTable;

/**
 * Copyright (c) Luka Kunic 2015 / "RoutineTable.java"
 * Created by lkunic on 04/05/2015.
 *
 * Model for the 'Routines' table in the database.
 */
public class RoutineTable extends DatabaseTable
{
	public static final String TABLE_NAME = "Routines";

	public static final String ID = "_id";
	public static final String TITLE = "Title";
	public static final String CYCLES = "Cycles";
	public static final String EXERCISES = "Exercises";
	public static final String EXERCISE_COUNT = "ExerciseCount";
	public static final String REST_BETWEEN_EXERCISES = "RestBetweenExercises";
	public static final String REST_BETWEEN_CYCLES = "RestBetweenCycles";

	/**
	 * Returns the name of this table
	 */
	@Override
	protected String getTableName()
	{
		return TABLE_NAME;
	}

	/**
	 * Returns the create statement in SQL for this table.
	 */
	@Override
	protected String getSqlCreateStatement()
	{
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
					ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					TITLE + " TEXT NOT NULL, " +
					CYCLES + " INTEGER NOT NULL, " +
					EXERCISES + " TEXT NOT NULL, " +
					EXERCISE_COUNT + " INTEGER NOT NULL, " +
					REST_BETWEEN_CYCLES + " INTEGER NOT NULL, " +
					REST_BETWEEN_EXERCISES + " INTEGER NOT NULL " +
				");";

	}
}
