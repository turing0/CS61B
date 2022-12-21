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
//                System.out.printf("addLast(%d)\n", randVal);
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                bad.addFirst(randVal);
                good.addFirst(randVal);
                msg = msg + "addFirst("+randVal+")\n";
//                System.out.printf("addFirst(%d)\n", randVal);
            } else if (operationNumber == 2) {
                // size
                int size = bad.size();
                int blsize = good.size();
                msg = msg + "size(): "+size+"\n";
                assertEquals(msg, blsize, size);
//                System.out.println("size: " + size);
            }
            else if (operationNumber == 3) {
                // removeFirst
                if (bad.size()>0 && good.size()>0) {
                    Integer v1=-1, v2=-1;
                    v1 = bad.removeFirst();
                    msg = msg + "removeFirst(): "+v1+"\n";
//                    System.out.printf("removeFirst(): %d\n", v1);
                    v2 = good.removeFirst();
                    assertEquals(msg, v2, v1);
                }
            }
            else if (operationNumber == 4) {
                // removeLast
                if (bad.size()>0 && good.size()>0) {
                    Integer v1=-1, v2=-1;
                    v1 = bad.removeLast();
                    msg = msg + "removeLast(): "+v1+"\n";
//                    System.out.printf("removeLast(): %d\n", v1);
                    v2 = good.removeLast();
                    assertEquals(msg, v2, v1);
                }
            }
        }


    }


}
