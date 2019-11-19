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



public ArrayList<String> question1(String[][] rankings){
		ArrayList<String> rlibStrings = new ArrayList<String>();
		String qid, ranklibString, fetValString;
		int noOfDocs = 12, noOfFeatures = 4, rank = 0, target = 0;
		double[] v = new double[noOfFeatures];
		for(int i=1; i<=noOfDocs; i++){
			target = 0;
			fetValString = "";
			for(int j=1; j<=noOfFeatures; j++){
				rank = this.getRank(i, rankings[j-1]);
				if(rank > 0){
					v[j-1] = 1.0/(double)rank;
					target = 1;
				}
				else{
					v[j-1] = 0;
				}
				fetValString = fetValString+" "+j+":"+v[j-1];
			}
			if(target > 0){
				ranklibString = target+" qid:q1"+fetValString+" #D"+i;
				rlibStrings.add(ranklibString);
				System.out.println(ranklibString);
			}
		}
		return rlibStrings;
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