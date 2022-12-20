package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> nlist = new AList<Integer>();
        AList timelist = new AList();
        nlist.addLast(1000);
        nlist.addLast(2000);
        nlist.addLast(4000);
        nlist.addLast(8000);
        nlist.addLast(16000);
        nlist.addLast(32000);
        nlist.addLast(64000);
        nlist.addLast(128000);

        for(int k=0;k<nlist.size();k++) {
            AList<Integer> alist = new AList<>();
            Stopwatch sw = new Stopwatch();
            for(int i=0;i<nlist.get(k);i++) {
                alist.addLast(1);
            }
            double timeInSeconds = sw.elapsedTime();
            timelist.addLast(timeInSeconds);

        }

        printTimingTable(nlist, timelist, nlist);
    }
}
