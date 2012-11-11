package com.engineering.printer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;

import com.trilead.ssh2.log.Logger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;
/**
 *
 * @author Siyong Liang
 */
public class ControlPanelScreen extends Activity{
	private Button browserButton;
	private Button cloudButton;
	//private Button printButton;
	//private Button historytButton;
	private String filePickerAPIKey = "ArYkjf8hmSpCG8cycKjmsz" ; 
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controlpanel);  
        
        browserButton = (Button) findViewById(R.id.file_browser_button);
        browserButton.setEnabled(true);
        
        cloudButton = (Button)findViewById(R.id.cloud_browser_button);
        cloudButton.setEnabled(true);
    }
    
    public void onBrowserBtnClick(View v) {
    	Intent myIntent = new Intent(v.getContext(), LocalFilePicker.class);
        startActivityForResult(myIntent, 0);
	}
    
    public void onCloudBtnClick(View v)
    {
        FilePickerAPI.setKey(filePickerAPIKey);
        Intent myIntent = new Intent(v.getContext(), FilePicker.class);
        startActivityForResult(myIntent, FilePickerAPI.REQUEST_CODE_GETFILE);
    }
    public void onHistoryBtnClick(View v)
    {
        FilePickerAPI.setKey(filePickerAPIKey);
        Intent myIntent = new Intent(v.getContext(), PrintHistoryScreen.class);
        startActivityForResult(myIntent, 0);
    }
    public void onPrintBtnClick(View v)
    {
        Intent myIntent = new Intent(v.getContext(), SettingScreen.class);
        startActivityForResult(myIntent, 0);
    }
    
    public void onResume() {
        super.onResume();
    }
     
    @Override
    protected void onActivityResult(int requestCode,
                                int resultCode, Intent data) {
        if (requestCode == FilePickerAPI.REQUEST_CODE_GETFILE) {
            if (resultCode != RESULT_OK)
            //Result was cancelled by the user or there was an error
                    return;
            Uri uri = data.getData();
            InputStream is = null;
            try{
            	is = getContentResolver().openInputStream(uri);
        	}catch (FileNotFoundException ex){
//        		Logger.getLogger(ControlPanelScreen.class.getName()).log(Level.SEVERE,null,ex);
        	}
            Document.load(is);
            Document.setDescriptor(uri);
            data.setClass(this, PrinterSelectScreen.class);
            startActivityForResult(data,0);
    	}
            
    }
}
