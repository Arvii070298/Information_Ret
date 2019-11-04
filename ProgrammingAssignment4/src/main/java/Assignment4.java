import java.io.File;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.blocktree.Stats;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.search.similarities.LMSimilarity.LMStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import co.nstant.in.cbor.CborException;
import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class Assignment4 {
    //
    
	private static List<String> out1= new ArrayList<String>();
	
	// initialize indexsearcher to null
	
	private IndexSearcher is = null;
	
	
//initialize query parser to null

	private QueryParser qp = null;
// initialize smoothing 
	private int smoothing ;
	static final String INDEX_DIR = "index";
	//static final String CBOR_FILE = "D:/test200/test200-train/train.pages.cbor-paragraphs.cbor";
//	static final String CBOR = "D:/test200/test200-train/train.pages.cbor-outlines.cbor";
	private static final String CUSTOM_SCORE_FILE = "file.txt";
	
	// custom out
	static final String CUSTOM_OUT = "";
//cbor_file
	public static String CBOR_FILE = null;
	// initilaze cbor to zero
	public static String CBOR = null;
	
	public Assignment4(int sm){
	    
	    // Idea: adjust the urn to make impossible events
//possible (yet unlikely)

//Shift part of the probability mass to unseen


		this.smoothing = sm;
	}
	// aguments include 1.vocabulary  size 
	//                  2.lambda
	//                  3. mu
	
	public SimilarityBase getCustomSimilarity(final int smoothing, final int vocabSize, final float lambda, final float mu){
		LMSimilarity mySimiliarity = new LMSimilarity() {
		    
		    
		    //Laplace Smoothing (add one ball per color)


			protected float score(BasicStats stats, float freq, float docLen) {
				float score = 0;
				switch(smoothing){
				case 1://Laplace
//arguments include requency, document length and vocabulary size( i.e total number of unique terms )
				
					score = ULaplaceScore(freq, docLen, vocabSize);
					break;
					
// //Jelinek Mercer Smoothing (interpolate with collection urn)
				case 2://Jelinek-Mercer
// arguments include frequency, document length and lambda 
// High value of lambda ; "AND like " retrive all the documents containing query terms
// Low value of lambda ; "OR like " suitable for long queries

					score = UJMScore(freq, docLen, lambda, (LMSimilarity.LMStats)stats);
					break;
					
//Dirichlet Smoothing (add 𝜇balls from collection urn)
				case 3://Dirichlet
					score = UDirichletScore(freq, docLen, mu, (LMSimilarity.LMStats)stats);
					break;

				case 4://Bigram with Laplace (B-L):
					score = BLaplaceScore(freq, docLen, vocabSize,(LMSimilarity.LMStats)stats);
					break;
				}
				return score;
			}

			@Override
			public String toString() {
				return null;
			}

			@Override
			public String getName() {
				
				return null;
			}
		};
		return mySimiliarity;
	
	
	}
	public float ULaplaceScore(float termFreq, float docLength, long vocabSize){
		float score = 0;
		score = (termFreq+1)/(docLength+vocabSize);
		return score;
	}
	
	public float UJMScore(float termFreq, float docLength, float lambda, LMSimilarity.LMStats stats){
		float score;
		score = lambda*termFreq/docLength+(1-lambda)*stats.getCollectionProbability();
		return score;
	}
	
	public float UDirichletScore(float termFreq, float docLength, float mu, LMSimilarity.LMStats stats){
		float score;
		score = (termFreq+mu*stats.getCollectionProbability()) / (docLength + mu);
		return score;
	}
	
	public float BLaplaceScore(float termFreq, float docLength, long vocabSize,LMSimilarity.LMStats stats){
			
		
		float score ;
		score = stats.getBoost()*(termFreq+1)/(docLength + stats.getTotalTermFreq());
		return score;
	}
	
	public String getcbor() {
		return CBOR;}
	public void setcbor(String s) {
		CBOR=s;
	}
	public String getcborfile() {
		return CBOR_FILE;
	}
	public void setcborfile(String s) {
		CBOR_FILE=s;
	}
	
	
	public void indexAllParas() throws CborException, IOException {
		Directory indexdir = FSDirectory.open((new File(INDEX_DIR)).toPath());
		IndexWriterConfig conf = new IndexWriterConfig(new EnglishAnalyzer());
		
		conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iw = new IndexWriter(indexdir, conf);
		Assignment3 a3 = new Assignment3();
		for (Data.Paragraph p : DeserializeData.iterableParagraphs(
				new FileInputStream(new File(CBOR_FILE)))) {
			a3.indexPara(iw, p);
		}
		iw.close();
	}
	
	public int getVocabSize(IndexReader rd){
		int vsize = 0;
		try {
			Fields fields = MultiFields.getFields(rd);
			for (String field:fields)
			{
				Terms terms = fields.terms(field);
				TermsEnum termsEnum = terms.iterator();
				while (termsEnum.next() != null)
					vsize++;
			}
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		return vsize;
	}
	
	public void rankParas(Data.Page page, int n, String out) throws IOException, ParseException{
		if ( is == null ) {
			is = new IndexSearcher(DirectoryReader.open(FSDirectory.open(
					(new File(INDEX_DIR).toPath()))));
		}
		

	
		if (qp == null) {
			qp = new QueryParser("parabody", new EnglishAnalyzer());
		}
		

		Query q;
		TopDocs tds;
		ScoreDoc[] retDocs;
		int vocabSize;
		

		q = qp.parse(page.getPageName());
		vocabSize = getVocabSize(is.getIndexReader());
		is.setSimilarity(this.getCustomSimilarity(this.smoothing, vocabSize, 0.9f, 1000));
		tds = is.search(q, n);
		retDocs = tds.scoreDocs;
		Document d;
		ArrayList<String> runStringsForPage = new ArrayList<String>();
		String method = "customLM"+this.smoothing;
             
        
		for (int i = 0; i < retDocs.length; i++) {
			d = is.doc(retDocs[i].doc);
			
			String runFileString = page.getPageId()+" Q0 "+d.getField("paraid").stringValue()
					+" "+i+" "+tds.scoreDocs[i].score+" team7-"+method;
					out1.add(runFileString);
			runStringsForPage.add(runFileString);
		}
		
		
		
	}
	
	public void rankParas(Data.Page page, int n) throws IOException, ParseException {
		this.rankParas(page, n, CUSTOM_OUT);
	}

	public static void main(String[] args) {
		final String CBOR = args[1];
		
		 final String CBOR_FILE = args[0]; 
	
		 int x=0;
		x=Integer.parseInt( args[2]);
		 
		 		Assignment4 a4 = new Assignment4(x);
		a4.setcbor(args[1]);
		a4.setcborfile(args[0]);
		Assignment3 a3 = new Assignment3();
		try {
			a4.indexAllParas();
			ArrayList<Data.Page> pagelist = a3.getPageListFromPath(CBOR);
			for(Data.Page p:pagelist){
				a4.rankParas(p, 100);
			} 
			String op;
			if(x==1) {
				op="UL.txt";
			}else if(x == 2) {
				op="U-JM.txt";
				
			}else if(x == 3) {
				op="U-DS.txt";
			}else {
				op="B-L.txt";
			}
			FileWriter writer = new FileWriter(op);
			for(String output: out1) {
	            writer.write(output + System.lineSeparator());

	        }System.out.println("File :"+op+"is being created");
			
			
	        writer.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

}