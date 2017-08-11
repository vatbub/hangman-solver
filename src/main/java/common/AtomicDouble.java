package common;

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
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;

/**
 * An implementation of an AtomicDouble.
 *
 * @author frede
 * @see AtomicInteger
 */
@SuppressWarnings("serial")
public class AtomicDouble extends Number {

    private final AtomicLong bits;

    /**
     * Creates a new {@code AtomicDouble} with initial value {@code 0}.
     */
    public AtomicDouble() {
        this(0d);
    }

    /**
     * Creates a new {@code AtomicDouble} with the given initial value.
     *
     * @param initialValue The initial value.
     */
    public AtomicDouble(double initialValue) {
        bits = new AtomicLong(doubleToLongBits(initialValue));
    }

    /**
     * Automatically sets the value to the given updated value if the current
     * value == the expected value.
     *
     * @param expect The expected value.
     * @param update The new value.
     * @return {@code true} if successful. False return indicates that the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(double expect, double update) {
        return bits.compareAndSet(doubleToLongBits(expect), doubleToLongBits(update));
    }

    /**
     * Sets to the given value.
     *
     * @param newValue The new value.
     */
    public final void set(double newValue) {
        bits.set(doubleToLongBits(newValue));
    }

    /**
     * Gets the current value.
     *
     * @return The current value.
     */
    public final double get() {
        return longBitsToDouble(bits.get());
    }

    /**
     * Returns the value of this {@code AtoomicDouble} as a {@code double}.
     *
     * @return The numeric value represented by this object after conversion to type {@code double}.
     */
    public double doubleValue() {
        return get();
    }

    /**
     * Returns the {@link String} representation of the current value.
     *
     * @return The {@link String} representation of the current value.
     */
    public String toString() {
        return Double.toString(get());
    }

    /**
     * Automatically sets to the given value and returns the old value.
     *
     * @param newValue The new value.
     * @return The previous value.
     */
    public final double getAndSet(double newValue) {
        return longBitsToDouble(bits.getAndSet(doubleToLongBits(newValue)));
    }

    /**
     * Atomically sets the value to the given updated value if the current value
     * == the expected value.
     * <p>
     * <a href=
     * "http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/package-summary.html#weakCompareAndSet">
     * May fail spuriously and does not provide ordering guarantees</a>, so is
     * only rarely an appropriate alternative to compareAndSet.
     *
     * @param expect The expected value.
     * @param update The new Value.
     * @return {@code true} if successful.
     */
    public final boolean weakCompareAndSet(double expect, double update) {
        return bits.weakCompareAndSet(doubleToLongBits(expect), doubleToLongBits(update));
    }

    /**
     * Returns the value of this {@code AtomicDouble} as a {@code float} after a primitive conversion.
     */
    public float floatValue() {
        return (float) get();
    }

    /**
     * Returns the value of this {@code AtomicDouble} as an {@code int} after a primitive conversion.
     */
    public int intValue() {
        return (int) get();
    }

    /**
     * Returns the value of this {@code AtomicDouble} as a {@code long} after a primitive conversion.
     */
    public long longValue() {
        return (long) get();
    }

}
