package LCD;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Record {

	public static ArrayList<Double> getData() {
		return data;
	}

	public static void setData(ArrayList<Double> data) {
		Record.data = data;
	}

	String name;
	static ArrayList<Double> data = new ArrayList<Double>();
	
	public Record() {
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		name = dtf.format(now);
		name = name.replace("/", "_");
		name = name.replace(":", ".");
	}

	public void writeValue() throws IOException {
		
		FileWriter outfile =  new FileWriter(name + ".csv");
	   
		int count = 0;
		
		outfile.write("Data point");
		outfile.write("Temperature \n");
		
	
		while(count < data.size()) {
			String num = Double.toString(data.get(count));
			outfile.write(Integer.toString(count) + "," + num);
			outfile.write("\n");
			count++;
		}
		
		outfile.close();
			
	
	}

}
