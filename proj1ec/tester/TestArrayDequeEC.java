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

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 7);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                bad.addLast(randVal);
                good.addLast(randVal);
                System.out.printf("addLast(%d)\n", randVal);
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                bad.addFirst(randVal);
                good.addFirst(randVal);
                System.out.printf("addFirst(%d)\n", randVal);
            } else if (operationNumber == 2) {
                // size
                int size = bad.size();
                int blsize = good.size();
                assertEquals(size, blsize);
//                System.out.println("size: " + size);
            }
            else if (operationNumber == 3) {
                // removeFirst
                int v1=-1, v2=-1;
                if (bad.size()>0) {
                    v1 = bad.removeFirst();
                    System.out.printf("removeFirst(): %d\n", v1);
                }
                if (good.size()>0) {
                    v2 = good.removeFirst();
                }
                if (v1>=0) {
                    assertEquals(v1, v2);
                }

            }
            else if (operationNumber == 4) {
                // removeLast
                int v1=-1, v2=-1;
                if (bad.size()>0) {
                    v1 = bad.removeLast();
                    System.out.printf("removeLast(): %d\n", v1);
                }
                if (good.size()>0) {
                    v2 = good.removeLast();
                }
                if (v1>=0) {
                    assertEquals(v1, v2);
                }
            }
        }


    }


}
