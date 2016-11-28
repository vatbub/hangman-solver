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
