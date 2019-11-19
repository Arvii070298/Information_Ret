package team07.assign01;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.queryparser.classic.ParseException;

import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.CborException;
import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
public class Assignment5 {



	public HashMap<String, ArrayList<String>> getRunFileMap(String runfile){
		HashMap<String, ArrayList<String>> runfileMap = new HashMap<String, ArrayList<String>>();
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(runfile));
			String line;
			String[] lineData = new String[6];
			while((line = br.readLine()) != null){
				lineData = line.split(" ");
				if(runfileMap.keySet().contains(lineData[0]))
					runfileMap.get(lineData[0]).add(lineData[2]);
				else{
					ArrayList<String> curr = new ArrayList<String>();
					curr.add(lineData[2]);
					runfileMap.put(lineData[0], curr);
				}
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return runfileMap;
	}
	
		public int getRank(int d, String[] ranking){
		int rank = -1, count = 1;
		for(int i=0; i<ranking.length; i++){
			if(ranking[i].equals("D"+d)){
				rank = count;
				break;
			}
			count++;
		}
		return rank;
	}







public static void main(String[] args) {
		// TODO Auto-generated method stub
		Assignment5 a5 = new Assignment5();
		ArrayList<HashMap<String, ArrayList<String>>> runMaps = 
				new ArrayList<HashMap<String,ArrayList<String>>>();
		String[][] rankings = {{"D1","D2","D3","D4","D5","D6"},
				{"D2","D5","D6","D7","D8","D9","D10","D11"},
				{"D1","D2","D5"},
				{"D1","D2","D8","D10","D12"}
		};
		
		String[] runs = {"output_lm/laplace", "output_lm/jms", "output_lm/dir",
				"output_lm/lncltn", "output_lm/bnnbnn"};
		for(int i=0; i<runs.length; i++)
			runMaps.add(a5.getRunFileMap(runs[i]));
			}
			
}