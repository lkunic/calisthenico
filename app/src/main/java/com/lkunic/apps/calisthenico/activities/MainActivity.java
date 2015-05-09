package com.lkunic.apps.calisthenico.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lkunic.apps.calisthenico.R;

public class MainActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		if (id == R.id.action_create_routine)
		{
			Intent i = new Intent(this, RoutineCreateActivity.class);
			startActivity(i);

			return true;
		}

		if (id == R.id.action_browse_routines)
		{
			Intent i = new Intent(this, RoutineBrowserActivity.class);
			startActivity(i);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
