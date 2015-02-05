/*
 * Authors ::
 * Group : Networking 1
 * Venkatesh Avula
 * Sasidhar Evuru
 * */

package com.codeoncloud.androidclient;

import java.io.BufferedReader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
	private EditText ipAddrTextView;
	private EditText portNumber;
	String result ="";
	public final String FOLDER_NAME="MsgFolder";
	public   String FILE_NAME="client.vcf";
	public   String IMAGE_NAME="Client.jpg";
	String percentage;
	BufferedReader readFile;
	String a[];
	long stop;
	long etime;
	long start;
	public final static String IP_ADDDR = "com.utd.acn.IP_ADDDR";
	public final static String PORT_NUMBER = "com.utd.acn.PORT_NUMBER";

	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		start=System.currentTimeMillis();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		stop=System.currentTimeMillis();
		etime=stop-start;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
	 	return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("NewApi")
	public void connect(View v) {
		 	Intent intent = new Intent(MainActivity.this, DownloadActivity.class );
		 	ipAddrTextView = (EditText) findViewById(R.id.ipAddress);
			portNumber = (EditText) findViewById(R.id.portNumber);
			intent.putExtra(IP_ADDDR, ipAddrTextView.getText().toString());
			intent.putExtra(PORT_NUMBER, portNumber.getText().toString());
			startActivity(intent);      
	}
	 
  }
