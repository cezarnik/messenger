public class Translator {
    public static String makeString(byte[] arr) {
        char[] temp = new char[arr.length];
        for (int i = 0; i < temp.length; ++i) {
            temp[i] = (char) arr[i];

        }
        String res = new String(temp);
        res = res.replace('\n', (char) 128);
        res = res.replace('\r', (char) 129);
        return res;
    }

    public static byte[] makeBytes(String s) {
        s = s.replace((char) 129, '\r');
        s = s.replace((char) 128, '\n');
        byte[] temp = new byte[s.length()];
        for (int i = 0; i < temp.length; ++i) {
            temp[i] = (byte) s.charAt(i);
        }
        return temp;
    }
}
