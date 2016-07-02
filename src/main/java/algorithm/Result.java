package algorithm;

import languages.Language;

/**
 * Represents a result of {@link HangmanSolver#solve}
 * 
 * @author frede
 * 
 */
public class Result {
	/**
	 * The current state of the game (win, loose, still running)
	 * 
	 * @see GameState
	 */
	GameState gameState;
	/**
	 * The next guess.
	 */
	public String result;
	/**
	 * The word in the dictionary that is the closest word to the current word
	 * sequence. This is not necessarily the proposed next guess, but this is
	 * most probably the word that the user is looking for.
	 */
	public String bestWord;
	/**
	 * The correlation currentSequence - {@link #bestWord} computed with
	 * {@link languages.TabFile#stringCorrelation}
	 */
	public double bestWordScore;
	
	/**
	 * The language of the solution
	 */
	public Language lang;
}
