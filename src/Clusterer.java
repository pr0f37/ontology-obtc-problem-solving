/**
 * OBTC_MED_ANowik..Main.java
 */
import java.util.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import readers.ConceptReader;
import stemmers.FileStemmer;
import types.Pair;
import filters.FileListFilter;

/**
 * Implementation of text clusterer class - which is responsible for providing
 * tools for creating ontology to cluster texts. Clusterer read glossary and document files
 * and creates a word lexicon and concepts hierarchy. It also creates sets of most important terms of the documents  
 * and maps them to glossary concepts to create lists of keywords. 
 * Lists of keywords might then be used to cluster texts together.   
 * @author Adam Nowik
 */
public class Clusterer {

	/**
	 *  Frequency of term per document
	 */
	private HashMap <String, HashMap<String, Integer>> tf; 
	/**
	 * TFIDF function value of pair term, document
	 */
	private HashMap <String, SortedSet <Pair>> tfidf; 
	/**
	 * In how many documents word appear term frequency 
	 */
	private HashMap <String, Integer> df;
	/**
	 * W function value of term
	 */
	private HashMap <String, Double> w; 
	/**
	 * W function value of term sorted in descending order
	 */
	private SortedSet<Pair> sw;
	public static final String glossary = "data.noun";
	
	public SortedSet<Pair> getSw() {
		return sw;
	}

	public HashMap<String, Double> getW() {
		return w;
	}

	public HashMap<String, Integer> getDf() {
		return df;
	}

	public HashMap<String, HashMap<String, Integer>> getTf() {
		return tf;
	}

	public Clusterer()
	{
		tf = new HashMap<String, HashMap<String,Integer>>();
		tfidf = new HashMap <String, SortedSet <Pair>>();
		df = new HashMap <String, Integer>();
		w = new HashMap <String, Double>();
		sw = (SortedSet<Pair>) new TreeSet<Pair>();
	}
	
	/**
	 * Creates term stems from the text file given 
	 * @param fileName - text file name to be read.
	 */
	public void readFile(String fileName)
	{
		
			FileStemmer fs = new FileStemmer(fileName);
			fs.stem();
			tf.put(fileName, fs.getWords());
	}
	
	/**
	 * Read text files from a given directory
	 * @see Clusterer#readFile
	 * @param directory - directory including text files
	 */
	public void readFiles(File directory)
	{
		FilenameFilter textFilesFilter = new FileListFilter(null, "txt");
		String fileNames[] = directory.list(textFilesFilter);
		for (String i : fileNames)
			this.readFile(directory + "\\" + i);
	}
	
	/**
	 * @return the tfidf
	 */
	public HashMap <String, SortedSet<Pair>> getTfidf() {
		return tfidf;
	}
	/**
	 * Counts term frequency per document
	 */
	public void countDf()
	{
		Iterator<Entry<String, HashMap<String, Integer>>> tfIt = tf.entrySet().iterator();
		while (tfIt.hasNext()) {
			Iterator<Entry<String, Integer>> docIt = (tfIt.next()).getValue().entrySet().iterator(); 
			while (docIt.hasNext()) {
				Entry<String, Integer> term = docIt.next();
				if (df.containsKey(term.getKey()))
					df.put(term.getKey(), df.get(term.getKey()) + 1);
				else
					df.put(term.getKey(), 1);
			}
		}
	}
	
	
	/**
	 * Counts tfidf value per every document and summary W value of every term.
	 */
	public void countTfidf_w() {
		if (df.isEmpty())
			this.countDf();
		int n = tf.size(); // number of documents
		Iterator<Entry<String, HashMap<String, Integer>>> tfIt = tf.entrySet().iterator();
		while (tfIt.hasNext()) {
			Entry<String, HashMap<String, Integer>> doc = tfIt.next();
			String docName = doc.getKey();
			Iterator<Entry<String, Integer>> docIt = doc.getValue().entrySet().iterator(); 
			while (docIt.hasNext()) {
				Entry<String, Integer> term = docIt.next();
				Double entryValue = term.getValue() * Math.log(n / df.get(term.getKey()));
				if (tfidf.containsKey(docName))
					tfidf.get(docName).add(new Pair(term.getKey(), entryValue));
				else
				{
					SortedSet<Pair> tmp = new TreeSet<Pair>();
					tmp.add(new Pair(term.getKey(), entryValue));
					tfidf.put(docName, tmp);
				}

				if (w.containsKey(term.getKey()))
					w.put(term.getKey(), w.get(term.getKey()) + entryValue);
				else
					w.put(term.getKey(), entryValue);
				
			}
		}
		
	}
	
	/**
	 * Sets W values in descending order.
	 */
	public void sortW() {
	Iterator <Entry<String, Double>> it = w.entrySet().iterator();	
		while (it.hasNext())
		{
			Pair a = new Pair(it.next());
			boolean r;
			r = sw.add(a);
			if (r == false)
				System.out.println(a); 
			
			
		}
	
	}
	
	/**
	 * Returns a set of keywords from the set of terms and concept hierarchy given
	 * @param terms - set of terms to be resolved in search of keywords
	 * @param conceptsStemmed - a set of setmmed concepts
	 * @param concepts - a set of full (non-stemmed) concepts
	 * @param conceptsHierarchy - hierarchy of concepts
	 * @return a list of keywords
	 */
	public List<String> getKeywords(SortedSet<Pair> terms, HashMap<String, String> conceptsStemmed, HashMap<String, String> concepts, HashMap<String, String> conceptsHierarchy, int counter)
	{
		
		Iterator<Pair> it = terms.iterator();
		List<String> keywords = new ArrayList<String>();
		System.out.println("Keywords:");
		while(it.hasNext() && counter > 0){
			String term = it.next().getKey();
			if(conceptsStemmed.containsValue(term))
			{
				counter--; //term is going to be a keyword!
				String conceptNum;
				String ancestorNum;
				String keyword;
				for(String i : conceptsStemmed.keySet())
				{
					String con = conceptsStemmed.get(i);
					if(con.equals(term))
					{
						ancestorNum = conceptNum = i;
						con = concepts.get(conceptNum);
						while(!ancestorNum.equals("0")) // going up the hierarchy
						{
							conceptNum = ancestorNum;
							ancestorNum = conceptsHierarchy.get(conceptNum);
						}
						
						keyword = (ancestorNum != "0")?concepts.get(ancestorNum):concepts.get(conceptNum);				
						System.out.print(term + "->" + con + "->" +keyword + ", ");
						if(!(keywords.contains(keyword)))
							keywords.add(keyword);
					}
				}				
			}
			
		}
		return keywords;
	}
	
	public static void main(String[] args)
	{
		int counter = 5;
		
		Clusterer clusterer = new Clusterer();
		System.out.println("[LOG]:Reading documents...");
		for (int i = 0; i<args.length; i++)
		{
			if ((args[i]).equals("-c"))
			{
				counter = Integer.parseInt(args[++i]);		
				continue;
			}
				
			File f = new File(args[i]);
			
			if (f.isFile())
			{
				clusterer.readFile(args[i]);
			}
			else if (f.isDirectory())
			{
				clusterer.readFiles(f);
			}
			else
				System.out.println("ERROR: " + args[i] + " is not a file nor directory! Skipping to the next argument.");
		}
		if (clusterer.getTf().size() != 0)
		{
			ConceptReader concepts = new ConceptReader();
			System.out.println("[LOG]:Reading concepts...");
			concepts.readFile(Clusterer.glossary);
			System.out.println("[LOG]:Creating concept hierarchy...");
			concepts.createHierarchy();
			
			System.out.println("[LOG]:Counting tfiidf and W(j) vectors...");
			clusterer.countTfidf_w();
			System.out.println("[LOG]:Sorting W vectors...");
			clusterer.sortW();
		
			System.out.println("All texts");
			clusterer.getKeywords(clusterer.getSw(), concepts.getConceptsStem(), concepts.getConcepts(), concepts.getConceptHierarchy(), counter);
			for (String i : clusterer.getTfidf().keySet())
			{
				System.out.println("\nDocument " + i);
				clusterer.getKeywords(clusterer.getTfidf().get(i), concepts.getConceptsStem(), concepts.getConcepts(), concepts.getConceptHierarchy(), counter);
				System.out.println("\n\n");
			}
	
			System.out.println("[LOG]:DONE!");
		}
		else
			System.out.println("[LOG]:No file to analyse found. Exiting.");
	}

	


}
