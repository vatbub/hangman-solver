package algorithm;

/*-
 * #%L
 * Hangman Solver
 * %%
 * Copyright (C) 2016 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import languages.Language;

/**
 * Represents a result of {@link HangmanSolver#solve}
 *
 * @author frede
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

    /**
     * Combines multiple results to a phrase
     *
     * @param resultsToCombine The {@code Result}s to be combined
     * @return A {@link ResultType}{@code .phrase}-result.
     */
    public static Result generatePhraseResult(ResultList resultsToCombine) {
        Result res = new Result();

        res.bestWord = String.join(" ", resultsToCombine.getBestWords());
        res.convertToWordResult();

        res.resultType = ResultType.phrase;
        res.lang = resultsToCombine.get(0).lang;

        return res;
    }

    public void convertToLetterResult() {
        this.result = Character.toString(this.bestChar);
        this.resultType = ResultType.letter;
    }

    public void convertToWordResult() {
        this.result = this.bestWord;
        this.resultType = ResultType.word;
    }

    @Override
    public String toString() {
        return this.result;
    }
}
