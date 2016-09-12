package algorithm;

/**
 * A class that contains a {@link String} and a count how often users used this
 * word already. This allows the algorithm to prioritize words.
 * 
 * @author frede
 *
 */
public class Word {

	private String word;
	private int count;

	public Word() {
		this("");
	}

	public Word(String word) {
		this(word, 0);
	}

	public Word(String word, int count) {
		this.setWord(word);
		this.setCount(count);
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Word) {
			return (this.getWord().equals(((Word) o).getWord()));
		} else {
			return false;
		}
	}

}
