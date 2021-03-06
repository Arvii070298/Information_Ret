import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import co.nstant.in.cbor.CborException;
import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class Assignment3 {

	static final String INDEX_DIR = "lucene_index/dir";
	static final String CBOR_FILE = "train.pages.cbor-paragraphs.cbor";
	static final String CBOR_OUTLINE = "train.pages.cbor-outlines.cbor";
	static final String OUTPUT_DIR = "output";
	static final String LUCENE_OUT = "lucene_tfidf_run2";
	static final String CUSTOM_OUT = "";
	static final String LUCENE_OUT_SEC = "default.txt";
	static final String CUSTOM_OUT_SEC = "lnn.txt";
	
	private IndexSearcher is = null;
	private QueryParser qp = null;
	private boolean customScore = true; // make it true to use the custom tfidf scores

	public void indexAllParas(int tfidf) throws CborException, IOException {
		Directory indexdir = FSDirectory.open((new File(INDEX_DIR)).toPath());
		IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
		if(customScore)
			conf.setSimilarity(this.getCustomSimilarity(tfidf));
		conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iw = new IndexWriter(indexdir, conf);
		for (Data.Paragraph p : DeserializeData.iterableParagraphs(new FileInputStream(new File(CBOR_FILE)))) {
			this.indexPara(iw, p);
		}
		iw.close();
	}
	public SimilarityBase getCustomSimilarity(final int tfidf){
		SimilarityBase mySimiliarity = new SimilarityBase() {
			
			protected float score(BasicStats stats, float freq, float docLen) {
			    // frescore initialized = 0
				float freqScore = 0;
				switch(tfidf){
				case 1:
					freqScore = 1+(float)Math.log(freq);
				case 2:
					freqScore = (1+(float)Math.log(freq))*
					((float)Math.log(stats.getNumberOfDocuments()/stats.getDocFreq()));
				case 3:
					if(freq>0)
						freqScore = 1;
					else
						freqScore = 0;
				
					
				}
				// return frescore
				return freqScore;
			}

			@Override
			public String toString() {
				return null;
			}
		};
		return mySimiliarity;
	}

	public void indexPara(IndexWriter iw, Data.Paragraph para) throws IOException {
		Document paradoc = new Document();
		paradoc.add(new StringField("paraid", para.getParaId(), Field.Store.YES));
		paradoc.add(new TextField("parabody", para.getTextOnly(), Field.Store.YES));
		iw.addDocument(paradoc);
	}

	public void rankParas(Data.Page page, int n, int tfidf, String outfilePath) throws IOException, ParseException {
		if ( is == null ) {
			is = new IndexSearcher(DirectoryReader.open(FSDirectory.open((new File(INDEX_DIR).toPath()))));
		}
		
		/*
		 * The first arg of QueryParser constructor specifies which field of document to
		 * match with query, here we want to search in the para text, so we chose
		 * parabody.
		 * 
		 */
		if (qp == null) {
			qp = new QueryParser("parabody", new StandardAnalyzer());
		}

		Query q;
		TopDocs tds;
		ScoreDoc[] retDocs;
		
	//	System.out.println("Query: " + page.getPageName());
		q = qp.parse(page.getPageName());
		if(customScore)
			is.setSimilarity(this.getCustomSimilarity(tfidf));
		tds = is.search(q, n);
		retDocs = tds.scoreDocs;
		Document d;
		ArrayList<String> runStringsForPage = new ArrayList<String>();
		String method = "lucene";
		if(outfilePath == null){
			String outfile = Assignment3.LUCENE_OUT;
			if(customScore){
				method = "custom";
				outfile = Assignment3.CUSTOM_OUT;
			}
			outfilePath = Assignment3.OUTPUT_DIR+"/"+outfile;
		}
		for (int i = 0; i < retDocs.length; i++) {
			d = is.doc(retDocs[i].doc);
		//	System.out.println("Doc " + i);
		//	System.out.println("Score " + tds.scoreDocs[i].score);
			//System.out.println(d.getField("paraid").stringValue());
			//System.out.println(d.getField("parabody").stringValue() + "\n");
			// runFile string format $queryId Q0 $paragraphId $rank $score $teamname-$methodname
			String runFileString = page.getPageId()+" Q0 "+d.getField("paraid").stringValue()
					+" "+i+" "+tds.scoreDocs[i].score+" team7-"+method;
			runStringsForPage.add(runFileString);
		}
		
		FileWriter fw = new FileWriter(outfilePath, true);
		for(String runString:runStringsForPage)
			fw.write(runString+"\n");
		fw.close();
	}
	
	public void rankParasUsingSections(Data.Page page, Data.Section section, String parentId, int n, int tfidf) throws IOException, ParseException {
		if ( is == null ) {
			is = new IndexSearcher(DirectoryReader.open(FSDirectory.open((new File(INDEX_DIR).toPath()))));
		}
		

		/*
		 * The first arg of QueryParser constructor specifies which field of document to
		 * match with query, here we want to search in the para text, so we chose
		 * parabody.
		 * 
		 */
		if (qp == null) {
			qp = new QueryParser("parabody", new StandardAnalyzer());
		}

		String qString = parentId+"/"+section.getHeadingId();
		Query q;
		TopDocs tds;
		ScoreDoc[] retDocs;
		
		q = qp.parse(qString.replaceAll("\\W", " "));
		System.out.println("Query: " + qString);
		if(customScore)
			is.setSimilarity(this.getCustomSimilarity(tfidf));
		tds = is.search(q, n);
		retDocs = tds.scoreDocs;
		Document d;
		ArrayList<String> runStringsForPage = new ArrayList<String>();
		String method = "lucene_sec";
		String outfile = Assignment3.LUCENE_OUT_SEC;
		if(customScore){
			method = "custom_sec";
			outfile = Assignment3.CUSTOM_OUT_SEC;
		}
		for (int i = 0; i < retDocs.length; i++) {
			d = is.doc(retDocs[i].doc);
			System.out.println("Doc " + i);
			System.out.println("Score " + tds.scoreDocs[i].score);
			String runFileString = qString+" Q0 "+d.getField("paraid").stringValue()
					+" "+i+" "+tds.scoreDocs[i].score+" team7-"+method;
			runStringsForPage.add(runFileString);
		}
		
		FileWriter fw = new FileWriter(Assignment3.OUTPUT_DIR+"/"+outfile, true);
		for(String runString:runStringsForPage)
			fw.write(runString+"\n");
		fw.close();
	}
	
	//public HashMap<String, double> getPageRprecMap();
	
	public ArrayList<Data.Page> getPageListFromPath(String path){
		ArrayList<Data.Page> pageList = new ArrayList<Data.Page>();
		try {
			FileInputStream fis = new FileInputStream(new File(path));
			for(Data.Page page: DeserializeData.iterableAnnotations(fis))
				pageList.add(page);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pageList;
	}

	public static void main(String[] args) {
		Assignment3 a = new Assignment3();
		
		try {
			a.indexAllParas(1); // tfidf for documents 1st one
			ArrayList<Data.Page> pagelist = a.getPageListFromPath(Assignment3.CBOR_OUTLINE);
			String parentId = "";
			for(Data.Page page:pagelist){
				//a.rankParas(page, 100, 3);
				parentId = page.getPageId();
				for(Data.Section secL1:page.getChildSections()){
					a.rankParasUsingSections(page, secL1, parentId, 100, 2);
					parentId = parentId+"/"+secL1.getHeadingId();
					for(Data.Section secL2:secL1.getChildSections()){
						a.rankParasUsingSections(page, secL2, parentId, 100, 2);
					}
				}
					//a.rankParasUsingSections(page, sec, 100, 3); // tfidf for queries 2nd one
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}
}