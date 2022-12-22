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

        int N = 1000;
        String msg = "";
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                bad.addLast(randVal);
                good.addLast(randVal);
                msg = msg + "addLast("+randVal+")\n";
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                bad.addFirst(randVal);
                good.addFirst(randVal);
                msg = msg + "addFirst("+randVal+")\n";
            } else if (operationNumber == 2) {
                // size
                int size = bad.size();
                int blsize = good.size();
                msg = msg + "size(): "+size+"\n";
                assertEquals(msg, blsize, size);
//                System.out.println("size: " + size);
            } else if (operationNumber == 3) {
                // removeFirst
                if (bad.size()>0 && good.size()>0) {
//                    msg = msg + "removeFirst(): "+v1+"\n";
                    msg = msg + "removeFirst()\n";
                    assertEquals(msg, good.removeFirst(), bad.removeFirst());
                }
            } else if (operationNumber == 4) {
                // removeLast
                if (bad.size()>0 && good.size()>0) {
//                    msg = msg + "removeLast(): "+v1+"\n";
                    msg = msg + "removeLast()\n";
                    assertEquals(msg, good.removeLast(), bad.removeLast());
                }
            }
        }

    }

}
