package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static final double CONCERT_A = 440.0;

    public static void main(String[] args) {
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        int KEYS_AMOUNT = keyboard.length();
        GuitarString stringA = new GuitarString(CONCERT_A);
        GuitarString[] stringArray = new GuitarString[KEYS_AMOUNT];

        for (int i = 0; i < KEYS_AMOUNT; i++) {
            stringArray[i] = new GuitarString(440 * Math.pow(2, (i - 24) / 12.0));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int k = keyboard.indexOf(key);
                if (k >= 0) {
                    stringArray[k].pluck();
                } else if (key == 'a') {
                    stringA.pluck();
                }

            }
            double sample = stringA.sample();
            for (int i = 0; i < KEYS_AMOUNT; i++) {
                sample += stringArray[i].sample();
            }

            StdAudio.play(sample);

            stringA.tic();
            for (int i = 0; i < KEYS_AMOUNT; i++) {
                stringArray[i].tic();
            }

        }
    }
}
