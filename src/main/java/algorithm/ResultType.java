package algorithm;

/**
 * Specifies the type of {@link Result} of
 * {@link HangmanSolver#solve(String, languages.Language)}.<br>
 * <b><i>word:</i></b> The result is an entire word.<br>
 * <b><i>letter:</i></b> The result only consists of a single letter.<br>
 * <b><i>phrase:</i></b> The result is an entire phrase.
 * 
 * @author frede
 *
 */
public enum ResultType {
	letter, word, phrase
}
