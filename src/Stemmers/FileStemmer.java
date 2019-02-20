/**
 * OBTC_MED_ANowik.Stemmers.FileStemmer.java
 */
package stemmers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * File stemmer for reading files and stemming them using the Porter Stemming Algorithm
 * @see Stemmer
 * @author Adam Nowik
 * 
 */
public class FileStemmer {

	/**
	 * @param args
	 */
	private String fileName;
	private HashMap<String, Integer> words;

	/**
	 * Default constructor
	 */
	public FileStemmer() {
		fileName = null;
	}

	/**
	 * One argument constructor
	 */
	public FileStemmer(String aFileName) {
		fileName = aFileName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the words
	 */
	public HashMap<String, Integer> getWords() {
		return words;
	}

	public void stem() {
		words = new HashMap<String, Integer>(); //initializing words collection
		char[] w = new char[501];
		Stemmer s = new Stemmer();
		try {
			FileInputStream in = new FileInputStream(fileName);
			try {
				while (true) {
					int ch = in.read();
					if (Character.isLetter((char) ch)) {
						int j = 0;
						while (true) {
							ch = Character.toLowerCase((char) ch);
							w[j] = (char) ch;
							if (j < 500)
								j++;
							ch = in.read();
							if (!Character.isLetter((char) ch)) {
								for (int c = 0; c < j; c++)
									s.add(w[c]);

								s.stem();
								this.addWord(s.toString());
								break;
							}
						}
					}
					if (ch < 0)
						break;
					
				}
			} catch (IOException e) {
				System.out.println("error reading " + fileName);
			}
		} catch (FileNotFoundException e) {
			System.out.println("file " + fileName + " not found");
			fileName = null;
		}

	}

	private void addWord(String aWord){
		if ((aWord.trim()).length() > 1) {
			if (words.containsKey(aWord))
				words.put(aWord, (words.get(aWord) + 1));	
			else
				words.put(aWord, 1);
		}
			
	}
	
}
