import javafx.util.Pair;

import java.util.*;

public class ShannonFanoCompressor extends Compressor {

    public byte[] compress(String s) {
        int cnt[] = new int[100000];
        ArrayList<Pair<Character, Integer>> occur = new ArrayList<>();
        for (int i = 0; i < s.length(); ++i) {
            int ind = (int) s.charAt(i);
            cnt[ind]++;
        }
        for (int i = 0; i < 100000; ++i) {
            if (cnt[i] != 0) {
                occur.add(new Pair<>((char) i, cnt[i]));
            }
        }
        occur.sort(Comparator.comparingInt(Pair::getValue));


        int wholeSize = occur.size();

        int sizeOfComponent[] = new int[wholeSize];
        sizeOfComponent[0] = wholeSize;

        String codes[] = new String[wholeSize];

        for (int i = 0; i < wholeSize; ++i)
            codes[i] = "";

        int pref[] = new int[wholeSize + 1];


        double entropy = 0;
        for (int i = 0; i < wholeSize; ++i) {
            double probability = occur.get(i).getValue() * 1.0 / s.length();
            entropy += probability * Math.log(1 / probability);
        }

        int averageLength = (int) Math.round(Math.ceil(entropy));
        if (wholeSize == 1) {
            codes[0] = "0";
        }

        int it = 0;
        while (true) {
            boolean did_something = false;
            for (int i = 0; i < wholeSize; ) {

                int currentSize = sizeOfComponent[i];

                if (currentSize == 1) { //we can not split
                    ++i;
                } else {//split with minimum absolute difference
                    did_something = true;
                    int left = i;
                    int right = i + currentSize - 1;

                    for (int j = left; j <= right; ++j)
                        pref[j + 1 - left] = pref[j - left] + occur.get(j).getValue();
                    int minDiff = Integer.MAX_VALUE;
                    int bestIndex = -1;  //size of the first comp

                    for (int j = 1; j < currentSize; ++j) {
                        int firstSize = pref[j];
                        int secondSize = pref[currentSize] - pref[j];

                        if (Math.abs(firstSize - secondSize) < minDiff) {
                            minDiff = Math.abs(firstSize - secondSize);
                            bestIndex = j;
                        }
                    }

                    for (int j = 1; j <= bestIndex; ++j) {
                        int realIndex = j - 1 + left;
                        codes[realIndex] += '0';
                    }
                    for (int j = bestIndex + 1; j <= currentSize; ++j) {
                        int realIndex = j - 1 + left;
                        codes[realIndex] += '1';
                    }
                    sizeOfComponent[i] = bestIndex;
                    sizeOfComponent[i + bestIndex] = currentSize - bestIndex;
                    i += currentSize;//go to the next component, if exists
                }

            }
            if (!did_something)
                break;
        }
        int mapping[] = new int[100000];//to map chars to indexes of codes array
        for (int i = 0; i < wholeSize; ++i)
            mapping[(int) occur.get(i).getKey()] = i;
        String temp = "";
        String result = "";
        result += s.length();
        result += "!";
        result += wholeSize;
        result += "!";

        for (int i = 0; i < wholeSize; ++i) {
            result += occur.get(i).getKey();
            result += codes[i] + "!";

        }

        char codedSequence[] = new char[averageLength * s.length() + 200000];
        int iterator = 0;
        for (int i = 0; i < s.length(); ++i) {
            temp += codes[mapping[(int) s.charAt(i)]];
            while (temp.length() >= 8) {
                char start = 0;

                for (int j = 0; j < 8; ++j)
                    start += (temp.charAt(j) - '0') << (7 - j);


                codedSequence[iterator++] = start;

                temp = temp.substring(8);
            }
        }
        for (int i = 0; i < 10; ++i)
            codedSequence[iterator++] = '!';
        for (int i = 0; i < temp.length(); ++i)
            codedSequence[iterator++] = temp.charAt(i);

        char realSeq[] = new char[iterator];
        for (int i = 0; i < iterator; ++i)
            realSeq[i] = codedSequence[i];
        String code = new String(realSeq);
        result += code;


        return Translator.makeBytes(result);

    }

    char charSeg[];

    boolean condition(int pos) {
        if (pos + 10 >= charSeg.length) {
            return false;
        }
        boolean ten = true;
        for (int i = pos; i < pos + 10; ++i) {
            if (charSeg[i] != '!')
                ten = false;
        }
        if (!ten)
            return true;
        if (ten && charSeg[pos + 10] == '!')
            return true;
        else {
            return false;
        }

    }

    public String decompress(byte[] input) {
        String decoded = Translator.makeString(input);
        String temp = "";
        int i = 0;
        while (decoded.charAt(i) != '!')
            temp += decoded.charAt(i++);
        int length = Integer.parseInt(temp);
        temp = "";
        ++i;
        while (decoded.charAt(i) != '!')
            temp += decoded.charAt(i++);
        int wholeSize = Integer.parseInt(temp);
        ++i;
        TreeMap<String, Character> treeMap = new TreeMap<>();
        int iterations = 0;
        while (iterations < wholeSize) {
            char letter = decoded.charAt(i);
            ++i;
            String curCode = "";
            while (decoded.charAt(i) != '!')
                curCode += decoded.charAt(i++);
            treeMap.put(curCode, letter);
            ++i;
            ++iterations;
        }


        char resultSeq[] = new char[length + 100];
        System.out.println(length);
        int iterator = 0;
        String prevNabor = "";

        charSeg = decoded.toCharArray();

        char charCode[] = new char[8];

        while (condition(i)) {

            int curInt = (int) charSeg[i];

            for (int j = 0; j <= 7; ++j)
                charCode[7 - j] = (char) (((curInt >> j) & 1) + '0');
            String code = new String(charCode);

            prevNabor += code;

            while (true) {
                boolean didSomething = false;
                String candidate = "";

                for (int j = 0; j < prevNabor.length(); ++j) {
                    candidate += prevNabor.charAt(j);

                    if (treeMap.containsKey(candidate)) {
                        char chtemp = treeMap.get(candidate);
                        if (iterator == length) {
                            System.out.println(candidate);
                            System.out.println(prevNabor);
                        }
                        resultSeq[iterator++] = chtemp;
                        if (j == prevNabor.length() - 1)
                            prevNabor = "";
                        else
                            prevNabor = prevNabor.substring(j + 1);

                        didSomething = true;
                        break;
                    }
                }
                if (!didSomething)
                    break;
            }
            ++i;
        }
        i += 10;
        String z = "";
        while (i < charSeg.length) {
            z += charSeg[i];
            ++i;
        }
        String remainder = z;//something not divisible by 16

        prevNabor += remainder;

        while (true) {
            boolean didSomething = false;
            String candidat = "";
            for (int j = 0; j < prevNabor.length(); ++j) {
                candidat += prevNabor.charAt(j);
                if (treeMap.containsKey(candidat)) {
                    resultSeq[iterator++] = treeMap.get(candidat);
                    if (j == prevNabor.length() - 1)
                        prevNabor = "";
                    else
                        prevNabor = prevNabor.substring(j + 1);
                    didSomething = true;
                    break;
                }
            }
            if (!didSomething)
                break;
        }
        char naebal[] = new char[iterator];
        for (int j = 0; j < iterator; ++j)
            naebal[j] = resultSeq[j];
        String kolyaResult = new String(naebal);
        return kolyaResult;
    }
}
