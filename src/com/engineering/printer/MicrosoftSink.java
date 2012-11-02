package com.engineering.printer;

public class MicrosoftSink  {//extends Activity {
//    
//    public void onCreate() {
//        super.onStart();
//        InputStream is = null;
//        try {
//            is = getContentResolver().openInputStream(getIntent().getData());
//        }
//        catch  (FileNotFoundException fnf){
//            Log.e("Connection","File Not Found");
//        }
//        Document.load(is);
//        Document.setDescriptor(getIntent().getData());
//             
//       Intent myIntent =  new Intent(getApplicationContext(),EngineeringPrinter.class).putExtra("Microsoft", true);
//        startActivityForResult(myIntent, 0);
//    }
//    
//    public void onRestart() {
//        super.onRestart();
//        finish();
//    }

    public static boolean Filter(String type) {
        if (type.equals("application/vnd.ms-powerpoint") || 
                type.equals("application/vnd.ms-excel")||
                type.equals("application/msword")) {
            return true;
        }
        return false;
    }
    
}
