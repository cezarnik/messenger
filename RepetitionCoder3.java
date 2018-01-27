/**
 * Created by user on 19.11.2017.
 */
public class RepetitionCoder3 extends Coder {


    byte block;
    byte[] block3;

    RepetitionCoder3() {
        block3 = new byte[3];
    }

    byte[] code(byte[] arr) {
        int len = arr.length;
        byte[] res = new byte[len * 3];
        for (int i = 0; i < res.length; ++i) {
            res[i] = arr[i / 3];
        }
        return res;
    }


    void decodeBlock() {
        block = 0;
        for (int bit = 0; bit < 8; ++bit) {
            int cnt = 0;
            for (int i = 0; i < 3; ++i) {
                if ((block3[i] & (1 << bit)) != 0) {
                    cnt++;
                }
            }
            if (cnt >= 2) {
                block |= (1 << bit);
            }
        }
    }

    byte[] decode(byte[] arr) {
        int len = arr.length;
        byte[] res = new byte[len / 3];
        for (int i = 0; i < len; i = i + 3) {
            for (int e = 0; e < 3; ++e) {
                block3[e] = arr[i + e];
            }
            decodeBlock();
            res[i / 3] = block;
        }
        return res;
    }
}
