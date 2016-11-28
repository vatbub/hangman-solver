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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResultList extends ArrayList<Result> {

	private static final long serialVersionUID = 1114264855287178435L;

	public ResultList() {
		super();
	}

	public ResultList(int arg0) {
		super(arg0);
	}

	public ResultList(Collection<? extends Result> arg0) {
		super(arg0);
	}

	public List<ResultType> getResultTypeList() {
		List<ResultType> res = new ArrayList<ResultType>();
		for (Result result : this) {
			res.add(result.resultType);
		}

		return res;
	}

	public List<String> getBestWords() {
		List<String> res = new ArrayList<String>();
		for (Result result : this) {
			res.add(result.bestWord);
		}

		return res;
	}

	public Result getFirstResultWithBestChar(char chr) {
		for (Result res : this) {
			if (res.resultType == ResultType.letter & res.bestChar == chr) {
				return res;
			}
		}

		// We only arrive here if no match has been found
		return null;
	}

}
