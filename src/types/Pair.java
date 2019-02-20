/**
 * OBTC_MED_ANowik.types.Pair.java
 */
package types;

import java.util.Map.Entry;

/**S
 * Container for a pair of String and Double elements.
 * Might be used in sorted generic structures. 
 * @author Adam Nowik
 *
 */
public class Pair implements Comparable<Pair> {
	private String key;
	private Double value;
	
	public Pair() {
		key = null;
		value = null;
	}
	
	public Pair(String key, Double value) {
		this.key = key;
		this.value = value;
	}
	
	public Pair(Entry<String, Double> entry) {
		this.key = entry.getKey();
		this.value = entry.getValue();
	}

	@Override
	public int compareTo(Pair o) {
		if (this.value < o.value)
			return 1;
		if (this.value >= o.value)
			return -1;
		return 0;
	}


	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}


	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}


	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
	}
	
	public boolean equals(Pair o) {
		if(this.key == o.getKey())
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "{" + this.key + ":" + this.value + "}";
	}
}
