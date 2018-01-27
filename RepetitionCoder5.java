/**
 * Created by user on 19.11.2017.
 */
public class RepetitionCoder5 extends Coder {


    byte block;
    byte[] block5;

    RepetitionCoder5() {
        block5 = new byte[5];
    }

    byte[] code(byte[] arr) {
        int len = arr.length;
        byte[] res = new byte[len * 5];
        for (int i = 0; i < res.length; ++i) {
            res[i] = arr[i / 5];
        }
        return res;
    }


    void decodeBlock() {
        block = 0;
        for (int bit = 0; bit < 8; ++bit) {
            int cnt = 0;
            for (int i = 0; i < 5; ++i) {
                if ((block5[i] & (1 << bit)) != 0) {
                    cnt++;
                }
            }
            if (cnt >= 3) {
                block |= (1 << bit);
            }
        }
    }

    byte[] decode(byte[] arr) {
        int len = arr.length;
        byte[] res = new byte[len / 5];
        for (int i = 0; i < len; i = i + 5) {
            for (int e = 0; e < 5; ++e) {
                block5[e] = arr[i + e];
            }
            decodeBlock();
            res[i / 5] = block;
        }
        return res;
    }
}
