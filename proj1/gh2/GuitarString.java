package gh2;

import deque.ArrayDeque;
import deque.Deque;

//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private final Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        int capacity = Math.toIntExact(Math.round(SR / frequency));
        buffer = new ArrayDeque<Double>();
        for (int i = 0; i < capacity; i++) {
            buffer.addLast(0.0);
        }
    }

    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        int cap = buffer.size();
        for (int i = 0; i < cap; i++) {
            buffer.removeLast();
        }
        buffer.printDeque();
        for (int i = 0; i < cap; i++) {
            double r = Math.random() - 0.5;
            buffer.addLast(r);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double v1 = buffer.removeFirst();
        double v2 = buffer.get(0);
        buffer.addLast((v1 + v2) / 2 * DECAY);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return 2 * buffer.get(0);
    }
}
