
import java.util.BitSet;
import java.util.PriorityQueue;
/**
 * Дерево Хаффмана
 */
public class HuffmanTree implements Comparable<HuffmanTree>{
    final int alphabet = 65536;

    private Node root;

    public HuffmanTree(Node root) {
        this.root = root;
    }


    private static class Node {

        private Integer frequency;

        private Character character;

        private Node leftChild;

        private Node rightChild;

        public Node(Integer frequency, Character character) {
            this.frequency = frequency;
            this.character = character;
        }

        public Node(HuffmanTree left, HuffmanTree right) {
            frequency = left.root.frequency + right.root.frequency;
            leftChild = left.root;
            rightChild = right.root;
        }
    }

    public static HuffmanTree buildHuffmanTree(int[] charFrequencies) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<>();
        for (int i = 0; i < charFrequencies.length; i++) {
            if (charFrequencies[i] > 0) {
                trees.offer(new HuffmanTree(new Node(charFrequencies[i], (char)i)));
            }
        }
        while (trees.size() > 1) {
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();
            trees.offer(new HuffmanTree(new Node(a, b)));
        }
        return trees.poll();
    }

    public String decode(BitSet code, int length) {
        StringBuilder result = new StringBuilder();
        Node node = root;
        if(node.character != null){
            for(int i = 0; i < length; i++){
                result.append(node.character);
            }
            return result.toString();
        }
        for (int i = 0; i < length; i++){
            if (!code.get(i)){

                node = node.leftChild;
                if(node.character != null){
                    result.append(node.character);
                    node = root;
                }
                else {
                    continue;
                }
            }
            else{
                node = node.rightChild;
                if(node.character != null){
                    result.append(node.character);
                    node = root;
                }
                else {
                    continue;
                }
            }
        }
        return result.toString();
    }


    public BitSet encode(String text, int[] length) {
        String[] codes = codeTable();
        BitSet res = new BitSet();

        int len = 0;

        int it = 0;
        for(int i = 0; i < text.length(); i++){
            for(int j = 0; j < codes[text.charAt(i)].length(); j++){
                if(codes[text.charAt(i)].charAt(j) == '0'){
                    res.set(it, false);
                    len++;
                    it++;
                }
                else{
                    res.set(it);
                    len++;
                    it++;
                }
            }
        }
        length[0] = len;
        return res;
    }

    private String[] codeTable() {
        String[] codeTable = new String[alphabet];
        codeTable(root, new StringBuilder(), codeTable);
        return codeTable;
    }

    private void codeTable(Node node, StringBuilder code, String[] codeTable) {
        if (node.character != null) {
            if(node == root)
                codeTable[(char)node.character] = "0";
            else
            codeTable[(char)node.character] = code.toString();

            return;
        }
        codeTable(node.leftChild, code.append('0'), codeTable);
        code.deleteCharAt(code.length() - 1);
        codeTable(node.rightChild, code.append('1'), codeTable);
        code.deleteCharAt(code.length() - 1);
    }

    @Override
    public int compareTo(HuffmanTree tree) {
        return root.frequency - tree.root.frequency;
    }

}
