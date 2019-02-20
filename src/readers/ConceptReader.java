/**
 * OBTC_MED_ANowik.readers.ConceptReader.java
 */
package readers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import stemmers.Stemmer;
import types.Pair;

/**
 * @author Adam Nowik
 *
 */
public class ConceptReader {

	private HashMap<String, String> conceptHierarchy; // <concept, ancestor>
	private SortedSet<Pair> conceptFreqSorted;
	private HashMap<String, String> concepts;
	
	/**
	 * @return the concepts
	 */
	public HashMap<String, String> getConcepts() {
		return concepts;
	}


	private HashMap<String, String> conceptsStem;
	/**
	 * @return the conceptsStem
	 */
	public HashMap<String, String> getConceptsStem() {
		return conceptsStem;
	}


	/**
	 * @return the synonyms
	 */
	public HashMap<String, List<String>> getSynonyms() {
		return synonyms;
	}


	private HashMap<String, List<String>> synonyms;
	
	private HashMap<String, Integer> conceptFreq;
	
	/**
	 * @return the conceptFreq
	 */
	public HashMap<String, Integer> getConceptFreq() {
		return conceptFreq;
	}


	public ConceptReader()
	{
		concepts = new HashMap<String,String>();
		conceptsStem = new HashMap<String, String>();
		synonyms = new HashMap<String, List<String>>();
		conceptFreq = new HashMap<String, Integer>();
		conceptHierarchy = new HashMap<String, String>();
	}
	
	/**
	 * Reads concept Stems and synonyms from glossary file (WordNet Nouns)
	 */
	public void readFile(String filename)
	{
		char[] w = new char[501];
		int j = 0;
		int symbol = 0;
		int state = 0;
		boolean flag = false;
		Stemmer s = new Stemmer();
		String num = "";
		String concept = "";
		String snum = "";
		try {
			FileInputStream in = new FileInputStream(filename);
			try {
				while (true)
				{
					symbol = in.read();
					if (symbol < 0)
						break;
					switch (state) {
						case 0: // beggining of new line
						{
							if(Character.isDigit((char)symbol))
								w[j++] = (char) symbol;
							else
							if(Character.isWhitespace((char)symbol)) // must be first space
							{
								state = 1;
								num = String.valueOf(w, 0, j);
								in.read(new byte[8], 0, 8); // next 8 bits is not important
								j = 0;
								w = new char[501];
							}
						}
						break;
						case 1: // concept body
						{
							if(Character.isLetter((char)symbol) || ((char) symbol == '_') || ((char) symbol == '-'))
								w[j++] = (char) symbol;
							else
							if (Character.isWhitespace((char)symbol)) // end of concept name 
							{
								state = 2; // searching for synonyms
								for (int i = 0; i < j; i++)
									s.add(w[i]);
								concepts.put(num, (String.valueOf(w, 0, j)).replace('_', ' '));
								s.stem();
								concept = s.toString();
								conceptsStem.put(num, concept); 
								j = 0;
								w = new char[501];
								synonyms.put(num, new ArrayList<String>());
								if (conceptFreq.containsKey(num))
									conceptFreq.put(num, conceptFreq.get(num) +1);
								else
									conceptFreq.put(num, 1);
							}
						}
						break;
						case 2:
						{
							while(true)
							{
								symbol = in.read();
								if(((char) symbol == '@') || ((char) symbol == '~'))
								{
									state = 3;
									flag = false;
									break;
								}
								if ((char) symbol == '|')
								{
									state = 4;
									break;
								}
							}
							
							symbol = in.read();
						}
						break;
						case 3:
						{
							while(!(Character.isDigit((char) symbol)) && flag == false)
								symbol = in.read();
							flag = true;
							if(Character.isDigit((char) symbol))
								w[j++] = (char) symbol;
							else
							if(Character.isWhitespace((char)symbol)) // synonym
							{
								state = 2;
								snum = String.valueOf(w, 0, j);
								j = 0;
								w = new char[501];
								synonyms.get(num).add(snum);
								if (conceptFreq.containsKey(snum))
									conceptFreq.put(snum, conceptFreq.get(snum) + 1);
								else
									conceptFreq.put(snum, 1);
							}
						}
						break;
						case 4:
						{
							
							state = 0;
							j = 0;
							w = new char[501];
							while(true)
							{
								if ((char) symbol == '\n' || symbol < 0)
								{
									state = 0;
									break;
								}
								symbol = in.read();
							}
							
						}
						break;
					}
				}
				in.close();
			}
			catch (IOException e) {
				System.out.println("Error while reading "+ filename + ":");
				e.printStackTrace();
			}
			
		}
		catch (FileNotFoundException e) {
			System.out.println("file " + filename + " not found");
	        
	    }
		
	}
	
	public void sortConcepts()
	{
		conceptFreqSorted = new TreeSet<Pair>();
		Iterator <Entry<String, Integer>> it = this.conceptFreq.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, Integer> concept = it.next();
			conceptFreqSorted.add(new Pair(concept.getKey(), concept.getValue()/1.0));
		}
	}
	
	/**
	 * @return the conceptFreqSorted
	 */
	public SortedSet<Pair> getConceptFreqSorted() {
		return conceptFreqSorted;
	}

	public void createHierarchy()
	{
		conceptHierarchy = new HashMap<String, String>();
		this.sortConcepts();
		Iterator<Pair> it =  conceptFreqSorted.iterator();
		while(it.hasNext())
		{
			String concept = it.next().getKey();
			if (!conceptHierarchy.containsKey(concept))
				conceptHierarchy.put(concept, "0");
			Iterator<String> its = synonyms.get(concept).iterator();
			while(its.hasNext())
			{
				String synonym = its.next(); 
				if(!conceptHierarchy.containsKey(synonym))
					conceptHierarchy.put(synonym, concept);
			}
		}
	}
	
	
	/**
	 * @return the conceptHierarchy
	 */
	public HashMap<String, String> getConceptHierarchy() {
		return conceptHierarchy;
	}

}
