package com.engineering.printer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 *
 * @author Siyong Liang
 */
public class ControlPanelScreen extends Activity{
	private Button browserButton;
	//private Button cloudButton;
	//private Button printButton;
	//private Button historytButton;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controlpanel);  
        
        browserButton = (Button) findViewById(R.id.file_browser_button);
        browserButton.setEnabled(true);
    }
    
    public void onBrowserBtnClick(View v) {
    	Intent myIntent = new Intent(v.getContext(), FilePicker.class);
        startActivityForResult(myIntent, 0);
	}
    
     public void onResume() {
            super.onResume();
	 }
}
