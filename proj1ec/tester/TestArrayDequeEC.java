package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.In;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.algs4.StdRandom;


public class TestArrayDequeEC {

    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> bad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> good = new ArrayDequeSolution<>();

        int N = 10;
        String msg = "";
        for (int i = 0; i < N; i += 1) {
            double  operationNumber = StdRandom.uniform();
            if (operationNumber < 0.5) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                bad.addLast(randVal);
                good.addLast(randVal);
                msg = msg + "addLast("+randVal+")\n";
            } else {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                bad.addFirst(randVal);
                good.addFirst(randVal);
                msg = msg + "addFirst("+randVal+")\n";
            }
        }
        int size = bad.size();
        int blsize = good.size();
        msg = msg + "size(): "+size+"\n";
        assertEquals(msg, blsize, size);

        for (int i = 0; i < N; i += 1) {
            double operationNumber = StdRandom.uniform();
            if (operationNumber < 0.5) {
                // removeFirst
                Integer v1=bad.removeFirst();
                Integer v2=good.removeFirst();
                msg = msg + "removeFirst(): "+v1+"\n";
                assertEquals(msg, v2, v1);
            }
            else {
                // removeLast
                Integer v1=bad.removeLast();
                Integer v2=good.removeLast();
                msg = msg + "removeLast(): "+v1+"\n";
                assertEquals(msg, v2, v1);
            }
            msg += ("size()\n");
            assertEquals(msg, good.size(), bad.size());
        }


    }

}
