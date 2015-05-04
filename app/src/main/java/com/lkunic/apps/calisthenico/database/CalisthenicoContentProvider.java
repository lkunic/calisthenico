package com.lkunic.apps.calisthenico.database;

import com.lkunic.libs.apptoolbox.database.DbContentProvider;
import com.lkunic.libs.apptoolbox.database.IQueryable;

public class CalisthenicoContentProvider extends DbContentProvider
{
	private static final String DATABASE_NAME = "Calisthenico.db";
	private static final int DATABASE_VERSION = 1;

	public static final String AUTHORITY = "com.lkunic.apps.calisthenico.database.CalisthenicoContentProvider";

	/**
	 * Implement to provide a list of data types that can be added to the database (database tables).
	 */
	@Override
	protected IQueryable[] getDatabaseItemTypes()
	{
		return new IQueryable[]
				{
						new Routine()
				};
	}

	/**
	 * The unique authority string for this content provider.
	 */
	@Override
	public String getAuthority()
	{
		return AUTHORITY;
	}

	/**
	 * Name of the database.
	 */
	@Override
	public String getDatabaseName()
	{
		return DATABASE_NAME;
	}

	/**
	 * Version number of the database.
	 */
	@Override
	public int getDatabaseVersion()
	{
		return DATABASE_VERSION;
	}
}
