package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> al = new AListNoResizing<Integer>();
        BuggyAList<Integer> bl = new BuggyAList<Integer>();
        al.addLast(4);
        al.addLast(5);
        al.addLast(6);
        bl.addLast(4);
        bl.addLast(5);
        bl.addLast(6);

        assertEquals(al.removeLast(), bl.removeLast());
        assertEquals(al.removeLast(), bl.removeLast());
        assertEquals(al.removeLast(), bl.removeLast());

    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> bl = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                bl.addLast(randVal);
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int blsize = bl.size();
                assertEquals(size, blsize);
//                System.out.println("size: " + size);
            }
            else if (operationNumber == 2) {
                // getLast
                int v1=-1, v2=-1;
                if (L.size()>0) {
                    v1 = L.getLast();
                }
                if (bl.size()>0) {
                    v2 = bl.getLast();
                }
                if (v1>=0) {
                    assertEquals(v1, v2);
                }

            }
            else if (operationNumber == 3) {
                // removeLast
                int v1=-1, v2=-1;
                if (L.size()>0) {
                    v1 = L.removeLast();
                }
                if (bl.size()>0) {
                    v2 = bl.removeLast();
                }
                assertEquals(v1, v2);
            }
        }


    }

}
