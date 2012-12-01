/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engineering.printer;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * @author Siyong Liang
 */
public class PrintHistoryScreen extends Activity
{
    @Override
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printhistory);  
        View linearLayout = findViewById(R.id.print_history_list);
        
        List<String> history = Document.getHistory();
        for(String item: history)
        {
        	TextView valueTV = new TextView(this);
        	valueTV.setText(item.startsWith("/")?item.substring(1):item);
        	valueTV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
        	((LinearLayout) linearLayout).addView(valueTV);
        }
    }
    
     @Override
	public void onResume() 
     {
        super.onResume();
    }
}
