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

        int N = 20;
        String msg = "";
        for (int i = 0; i < N; i += 1) {
            double operationNumber = StdRandom.uniform();
            if (operationNumber < 0.5) {
                // addLast
                bad.addLast(i);
                good.addLast(i);
                msg = msg + "addLast("+i+")\n";
            } else {
                // addFirst
                bad.addFirst(i);
                good.addFirst(i);
                msg = msg + "addFirst("+i+")\n";
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
                if (bad.size()>0 && good.size()>0) {
                    Integer item1 = bad.removeFirst();
                    Integer item2 = good.removeFirst();
                    msg += ("removeFirst()\n");
                    assertEquals(msg, item2, item1);
                }
            } else {
                // removeLast
                if (bad.size()>0 && good.size()>0) {
                    Integer item1 = bad.removeLast();
                    Integer item2 = good.removeLast();
                    msg += ("removeLast()\n");
                    assertEquals(msg, item2, item1);
                }
            }
            msg += ("size()\n");
            assertEquals(msg, good.size(), bad.size());
        }


    }


}
