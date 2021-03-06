package search;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		if(docFile == null){
			throw new FileNotFoundException();
		}
		HashMap<String,Occurrence> map = new HashMap<String,Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		
		while(sc.hasNext()){
			String next = sc.next();
			if(getKeyWord(next)!=null){
				next = getKeyWord(next);
				if(map.containsKey(next)){
					Occurrence occur = map.get(next);
					occur.frequency++;
					
				}
				else{
					Occurrence occur = new Occurrence(docFile,1);
					map.put(next, occur);
				}
			}
		}
		return map;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		for(Map.Entry<String, Occurrence> key: kws.entrySet()){
			ArrayList<Occurrence>occur = new ArrayList<Occurrence>();
			String keyStr = key.getKey();
			if(keywordsIndex.containsKey(keyStr)){
				occur = keywordsIndex.get(keyStr);
				occur.add(kws.get(keyStr));
				insertLastOccurrence(occur);
				
				
			}
			else{
				occur.add(kws.get(keyStr));
				
			}
			keywordsIndex.put(keyStr, occur);
		}
		
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		if(word == null){
			return null;
		}
		if (containsPunc(word)){
			return null;
		}
		
		String nuWord = word.toLowerCase();
		for(int i = 0; i <word.length(); i ++){
			char c = word.charAt(i);
			if(!Character.isLetter(c)){
				nuWord = word.substring(0, i).toLowerCase();
				break;
			}
		}
		if(noiseWords.containsValue(nuWord)){
			return null;
		}
		else if(nuWord.length() <= 0){
			
			return null;
		}
		else{
			return nuWord;
		}
	}
	private boolean containsPunc(String word){
		boolean punk = false;
		int i;char c;
		
		for( i = 0; i <word.length(); i++){
			c = word.charAt(i);
			if(!Character.isLetter(c)){
				
				punk = true;
				break;
			}
		}
		if(punk){
			for(int x = i+1; x <word.length(); x++ ){
				c = word.charAt(x);
				if(Character.isLetter(c)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		ArrayList<Integer> arrMid  = new ArrayList<Integer>();
		if(occs.size() < 2){
			return null;
		}
		int L = 0;
		int R = occs.size()-2;
		int last = occs.get(occs.size()-1).frequency;
		while(L<=R){
			int mid = (L+R)/2;
			arrMid.add(mid);
			if(occs.get(mid).frequency > last){
				L = mid+1;
			}
			else if(occs.get(mid).frequency < last){
				R = mid-1;
			}
			else if(last == occs.get(mid).frequency){
				break;
			}
		}
		
		Occurrence tmp = occs.remove(occs.size()-1);
		int midFreq = occs.get(arrMid.get(arrMid.size()-1)).frequency;
		if(last>=midFreq){
			occs.add(arrMid.get(arrMid.size()-1), tmp);
		}
		else if (last < midFreq){
			occs.add(arrMid.get(arrMid.size()-1) + 1, tmp);
		}
		return arrMid;
		
		
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		ArrayList<String>top5 = new ArrayList<String>();
		ArrayList<Occurrence>masterList = new ArrayList<Occurrence>();
		ArrayList<Occurrence>list1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence>list2 = new ArrayList<Occurrence>();
		HashMap<String,Occurrence>mapu = new HashMap<String,Occurrence>();
		for(int i = 0; i <keywordsIndex.get(kw1).size();i++){
			Occurrence occKw1 = keywordsIndex.get(kw1).get(i);
			list1.add(occKw1);
			mapu.put(occKw1.document, occKw1);
		}
		
		for(int i = 0; i <keywordsIndex.get(kw2).size(); i++){
			Occurrence occKw2 = keywordsIndex.get(kw2).get(i);
			String doc = occKw2.document;
			if(mapu.containsKey(doc)){
				if(occKw2.frequency > mapu.get(doc).frequency){
					list1.remove(mapu.get(doc));
					list2.add(occKw2);
					mapu.put(doc, occKw2);
				}
			}
			else{
				list2.add(occKw2);
				mapu.put(doc, occKw2);
			}
		
			
		}
		
		list1.addAll(list2);
		masterList.addAll(list1);
		
		top5 = sorter(masterList, mapu);
		while(top5.size()>5){
			top5.remove(5);
		}
		return top5;
		
		
	}
	private ArrayList<String>sorter(ArrayList<Occurrence> list, HashMap<String, Occurrence> map){
		ArrayList<String>sorted = new ArrayList<String>();
		
		if(list.size() == 0){
			return null;
		}
		if(list.size() == 1){
			sorted.add(list.get(0).document);
			return sorted;
		}
		for(int i = 0; i < list.size(); i++){
			String doc = list.get(i).document;
			int value = list.get(i).frequency;
			sorted.add(doc);
			
			int count = 0;
			while(value<= map.get(sorted.get(count)).frequency && count < sorted.size()-1){
				count++;
			}
			sorted.add(count, sorted.remove(sorted.size()-1));
			
			
		
		}
		return sorted;	
	}
}