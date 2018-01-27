import java.util.ArrayList;

public class LZCompressor {

    public byte[] compress(String in) throws Exception {

        byte[] arr = in.getBytes();
        String input = "";

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            str.append(String.valueOf((char)arr[i]));
        }
        input = str.toString();

        ArrayList<Byte> result = new ArrayList<>();;

        ArrayList<String> dictionary = new ArrayList<>();

        int index = 0;

        dictionary.add(null);

        StringBuilder currentBuilder;
        StringBuilder previousBuilder;
        StringBuilder ci = new StringBuilder();
        while (index < input.length()) {
            if (dictionary.size() >= 256) {
                dictionary = new ArrayList<>();
                dictionary.add(null);
                for (int i = 0; i < result.size(); i++) {
                    ci.append(String.valueOf((char)(byte)result.get(i)));
                }
                result = new ArrayList<>();
            }

            currentBuilder = new StringBuilder();
            previousBuilder = new StringBuilder();
            currentBuilder.append(String.valueOf(input.charAt(index)));
            if (dictionary.contains(currentBuilder.toString())) {
                for (int i = index; i < input.length(); i++) {
                    if (dictionary.contains(currentBuilder.toString())) {
                        if (i + 1 < input.length()) {
                            currentBuilder.append(String.valueOf(input.charAt(i+1)));
                        }
                    } else break;
                }
                int j = index;
                for (int i = 0; i < currentBuilder.toString().length() - 1; i++) {
                    previousBuilder.append(String.valueOf(input.charAt(j)));
                    j += 1;
                }
                if (!previousBuilder.toString().equals("")) {
                    result.add((byte)(dictionary.indexOf(previousBuilder.toString())));
                    result.add((byte)(currentBuilder.toString().charAt(currentBuilder.toString().length()-1)));
                } else {
                    result.add((byte) 0);
                    result.add((byte)(currentBuilder.toString().charAt(currentBuilder.toString().length()-1)));
                }
                index = index + currentBuilder.toString().length();
                dictionary.add(currentBuilder.toString());
            } else {
                result.add((byte) 0);
                result.add((byte)(currentBuilder.toString().charAt(currentBuilder.toString().length()-1)));
                dictionary.add(currentBuilder.toString());
                index++;
            }
        }
        for (int i = 0; i < result.size(); i++) {
            ci.append(String.valueOf((char)(byte)result.get(i)));
        }
        return ci.toString().getBytes();
    }

    public byte[] decompress(String in) throws Exception {

        byte[] arr = new byte[in.length()];
        for (int i = 0; i < in.length(); i++) {
            arr[i] = (byte)in.charAt(i);
        }

        ArrayList<String> dict = new ArrayList<>();

        String newLetter;

        ArrayList<Byte> r2 = new ArrayList<>();

        StringBuilder ci = new StringBuilder();
        for (int i = 0; i < arr.length; i += 2) {
            if (dict.size() >= 256) {
                String last = dict.get(255);
                dict = new ArrayList<>();
                dict.add(last);

                for (int k = 0; k < r2.size(); k++) {
                    ci.append(String.valueOf((char)(byte)r2.get(k)));
                }
                r2 = new ArrayList<>();
            }
            newLetter = String.valueOf((char)(arr[i+1]));
            if ((int)(arr[i]) == 0) {
                dict.add(newLetter);
                r2.add((byte)(newLetter.charAt(0)));
            } else {
                int index = (int)(arr[i]);
                if (index < 0) index += 256;
                dict.add(dict.get(index-1) + newLetter);
                for (int m = 0; m < dict.get(index-1).length(); m++) {
                    r2.add((byte)dict.get(index-1).charAt(m));
                }
                r2.add((byte)(newLetter.charAt(0)));
            }
        }
        for (int k = 0; k < r2.size(); k++) {
            ci.append(String.valueOf((char)(byte)r2.get(k)));
        }
        byte[] arr2 = new byte[ci.length()];
        for (int i = 0; i < ci.length(); i++) {
            arr2[i] = (byte)ci.charAt(i);
        }
        return arr2;
    }
}