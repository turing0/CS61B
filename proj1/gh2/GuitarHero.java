package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
import deque.ArrayDeque;

public class GuitarHero {
    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);

    public static void main(String[] args) {
        GuitarString stringA = new GuitarString(CONCERT_A);
        GuitarString stringC = new GuitarString(CONCERT_C);
        ArrayDeque<GuitarString> stringArray = new ArrayDeque(37);
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        for (int i = 0; i < 37; i++) {
            stringArray.addLast(new GuitarString(440 * Math.pow(2, (i - 24) / 12.0)));
        }
//        stringArray.printDeque();

        GuitarString targetString = stringArray.get(0);
        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int k = keyboard.indexOf(key);
                if (k >= 0) {
                    targetString = stringArray.get(k);
                    targetString.pluck();
                } else if (key == 'a') {
                    stringA.pluck();
                }

            }
            double sample = stringA.sample();
            for (int i = 0; i < 37; i++) {
                sample += stringArray.get(i).sample();
            }

            StdAudio.play(sample);

            stringA.tic();
            for (int i = 0; i < 37; i++) {
                stringArray.get(i).tic();
            }

        }
    }
}
