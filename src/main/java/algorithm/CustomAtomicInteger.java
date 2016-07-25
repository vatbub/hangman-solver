package algorithm;

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
