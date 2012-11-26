package com.engineering.printer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class PrintCaller {
    
    private CommandConnection mConn;
    
    public PrintCaller(CommandConnection conn) {
        mConn = conn;
    }
    
	private String DEFAULT_PRINTER = null;
	
	public String runCommand(String cmd) throws IOException{ 
		String out = mConn.execWithReturn(cmd);
		return out;
	}
	
	@Override
	public String toString() {
		String out = "";
		try {
			out = runCommand("lpstat -a");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringTokenizer st = new StringTokenizer(out, "\n");
		String printers = "";
		while(st.hasMoreTokens()) {
			StringTokenizer st2 = new StringTokenizer(st.nextToken());
			printers += st2.nextToken() + "\n";
		}
		return printers;
	}
	
	public List<String> getPrinters() throws IOException {
		String out = runCommand("lpstat -a");
		StringTokenizer st = new StringTokenizer(out, "\n");
		LinkedList<String> printers = new LinkedList<String>();
		while(st.hasMoreTokens()) {
			StringTokenizer st2 = new StringTokenizer(st.nextToken());
			printers.add(st2.nextToken());
		}
		return printers;
	}
	
	public void printFile(String fileName, String printerName, int numCopies, boolean duplex) throws IOException {
	    if (EngineeringPrinter.Microsoft) {
    	    runCommand("ooffice -norestore -nofirststartwizard -nologo -headless -pt " + printerName + " " + fileName);
    	}
    	else {
    	       runCommand("lpr -P" + printerName + " -# " + numCopies + (duplex ? " -o sides=two-sided-long-edge" : "") + " " + fileName);      
    	}
	}
	
	public String getDefaultPrinter() throws IOException {
		return (DEFAULT_PRINTER == null) ? getPrinters().get(0): DEFAULT_PRINTER;
	}
	
	public void setDefaultPrinter(String printer) {
		DEFAULT_PRINTER = printer;
	}

	/*public void printFile(String local_filename, String local_printer,
			int local_number, boolean local_duplex, boolean local_timedPrinting) {
		if (!local_timedPrinting){
			printFile(local_filename, local_printer, local_number, local_duplex, local_timedPrinting);
		}else{ 
			//timedPrinting. divide to 5 pages per chunk sent to printer. only work for PDFs and Microsoft office documents
			ArrayList<PagePair> pageIndexes = calcPageIndexes(local_filename);
		}
		
	}
	
	public ArrayList<PagePair> calcPageIndexes(String local_filename){
		ArrayList<PagePair> pageIndexes = new ArrayList<PagePair>();
		
		return pageIndexes;
	}*/
	
}

/*class PagePair{
	int pageFrom;
	int pageTo;
	public PagePair(int from, int to){
		pageFrom = from;
		pageTo = to;
	}
}*/
