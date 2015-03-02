// Copyright 2007-2014 metaio GmbH. All rights reserved.
package com.metaio.Example;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;


@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity
{
	
AssetsExtracter mTask;
	

	boolean mLaunchingTutorial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.webview);
		
		// Enable metaio SDK log messages based on build configuration
		MetaioDebug.enableLogging(BuildConfig.DEBUG);
		
		mTask = new AssetsExtracter();
		mTask.execute(0);
		
	
		//Intent i = new Intent(getApplicationContext(),TutorialLocationBasedAR.class);
		//startActivity(i);
		btnClassroomClick();
		btnHostelClick();
		btnOtherClick();
		btnrecclick();
		helpbtn();
	}
	public void helpbtn(){
		
	}
	public void btnrecclick(){
		Button	button = (Button) findViewById(R.id.btnrec);
		 
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
 
			
			  Intent intent = new Intent(getApplicationContext(),recreationClass.class);
			   
			  startActivity(intent);
 
			}
 
		});
		
	}
	
	public void btnClassroomClick() {
		 
	Button	button = (Button) findViewById(R.id.btnclass);
 
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
 
			
			  Intent intent = new Intent(getApplicationContext(),TutorialLocationBasedAR.class);
			   
			  startActivity(intent);
 
			}
 
		});
 
	}
	public void btnHostelClick() {
		 
		Button	button = (Button) findViewById(R.id.button2);
	 
			button.setOnClickListener(new OnClickListener() {
	 
				@Override
				public void onClick(View arg0) {
	 
				
				  Intent intent = new Intent(getApplicationContext(),BCHostels.class);
				   
				  startActivity(intent);
	 
				}
	 
			});
	 
		}
	
	public void btnOtherClick() {
		 
		Button	button = (Button) findViewById(R.id.button1);
	 
			button.setOnClickListener(new OnClickListener() {
	 
				@Override
				public void onClick(View arg0) {
	 
				
				  Intent intent = new Intent(getApplicationContext(),BCoffices.class);
				   
				  startActivity(intent);
	 
				}
	 
			});
	 
		}
	
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean>
	{

		
		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			try 
			{
				
				AssetsManager.extractAllAssets(getApplicationContext(), true);
			} 
			catch (IOException e) 
			{
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}

			return true;
		}
		
	
	}
	
}

