package com.sreenivas.fewminuteseveryday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends Activity{
	
	Button goNext;
	TextView welcome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		goNext = (Button) findViewById(R.id.btnContinue);
		
		// starting the main activity on button click
		goNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mainActivity = new Intent("com.sreenivas.fewminuteseveryday.MAINACTIVITY");
				startActivity(mainActivity);
			}
			
		});
	}
	
}
