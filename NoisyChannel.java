/**
 * Created by user on 20.11.2017.
 */
public class NoisyChannel {
    double val;

    NoisyChannel(double fault) {
        val = fault;
    }

    void goThrough(byte[] arr) {
        int cnt = 0;
        for (int i = 0; i < arr.length; ++i) {
            for (int bit = 0; bit < 8; ++bit) {
                if (Math.random() < val) {
                    arr[i] ^= (1 << bit);
                    cnt++;
                }
            }
        }
    }
}
