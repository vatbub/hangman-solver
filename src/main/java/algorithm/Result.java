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
	public GameState gameState;
	/**
	 * The next guess.
	 */
	public String result;

	public ResultType resultType;

	public char bestChar;

	public double bestCharScore;

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

	public void convertToLetterResult() {
		this.result = Character.toString(this.bestChar);
		this.resultType = ResultType.letter;
	}

	public void convertToWordResult() {
		this.result = this.bestWord;
		this.resultType = ResultType.word;
	}
	
	/**
	 * Combines multiple results to a phrase
	 * @param resultsToCombine The {@code Result}s to be combined
	 * @return A {@link ResultType}{@code .phrase}-result.
	 */
	public static Result generatePhraseResult(ResultList resultsToCombine){
		Result res = new Result();
		
		res.bestWord = String.join(" ", resultsToCombine.getBestWords());
		res.convertToWordResult();
		
		res.resultType = ResultType.phrase;
		res.lang = resultsToCombine.get(0).lang;
		
		return res;
	}
	
	@Override
	public String toString(){
		return this.result;
	}
}
