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


import java.util.concurrent.atomic.AtomicInteger;

public class CustomAtomicInteger extends AtomicInteger implements Comparable<CustomAtomicInteger> {

	private static final long serialVersionUID = 382336370292367355L;

	public CustomAtomicInteger() {
		super();
	}

	public CustomAtomicInteger(int initialValue) {
		super(initialValue);
	}

	@Override
	public int compareTo(CustomAtomicInteger arg0) {
		if (arg0 == null) {
			throw new NullPointerException();
		}

		int myVal = this.get();
		int argVal = arg0.get();

		if (myVal < argVal) {
			return -1;
		} else if (myVal == argVal) {
			return 0;
		} else {
			// implies myVal>argVal
			return 1;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if ((o instanceof CustomAtomicInteger || o instanceof AtomicInteger)
				&& (((CustomAtomicInteger) o).get() == this.get())) {
			return true;
		} else {
			return false;
		}
	}

}
