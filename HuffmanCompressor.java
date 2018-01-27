import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;

/**
 * Created by user on 03.11.2017.
 */
public class HuffmanCompressor extends Compressor{
    final int alphabet = 65536;

    private byte[] toBitArray(byte[] data, int length){
        BitSet bitSet = BitSet.valueOf(data);
        byte[] result = new byte[length];
        for(int i = 0; i < length; i++){
            if(bitSet.get(i))
                result[i] = 1;
            else
                result[i] = 0;
        }
        return result;
    }

   private byte[] integersToBytes(int[] data) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(data);

        return byteBuffer.array();
    }

    @Override
   public byte[] compress(String text) throws Exception {

       int[] charFrequencies = new int[alphabet];
       // считываем символы и считаем их частоту
       for (char c : text.toCharArray()) {
           charFrequencies[c]++;
       }

       // строим дерево
       HuffmanTree tree = HuffmanTree.buildHuffmanTree(charFrequencies);
       System.out.printf("size before compression = %d%n", text.length() * 8);
        int[] arr = new int[1];
        BitSet bitSet = tree.encode(text, arr);
       System.out.println("size after compression = " + arr[0]);

        byte[] msg = bitSet.toByteArray();

        byte[] byte_length = integersToBytes(arr);
        int count = 0;
        for(int i = 0; i < charFrequencies.length; i++){
            if(charFrequencies[i] != 0)
                count++;
        }
       int[] arr1 = new int[1];
       arr1[0] = count;
       byte[] byte_count = integersToBytes(arr1);
        byte[] used_chars = new byte[count*6];
       int counter = 0;
       for(char c = 0; c < charFrequencies.length; c++){
           if(charFrequencies[c] != 0){
               char[] ch = new char[1];
               ch[0] = c;
               ByteBuffer byteBuffer = ByteBuffer.allocate(2);
               byteBuffer.asCharBuffer().put(ch);
               for(int j = 0; j < 2; j++){
                   used_chars[6*counter+j] = byteBuffer.array()[j];
               }
               int[] integer = new int[1];
               integer[0] = charFrequencies[c];
               ByteBuffer byteBuffer1 = ByteBuffer.allocate(4);
               byteBuffer1.asIntBuffer().put(integer);
               byte[] bbufferArray = byteBuffer1.array();
               for(int j = 0; j < 4; j++){
                   used_chars[6*counter+2+j] = bbufferArray[j];
               }
               counter++;
               if(counter >= count)
                   break;
           }
       }

       byte[] full_message = new byte[byte_count.length + used_chars.length + byte_length.length + msg.length];
       for(int i = 0; i < byte_count.length; i++){
           full_message[i] = byte_count[i];
       }
       for(int i = 0; i < used_chars.length; i++){
           full_message[i+byte_count.length] = used_chars[i];
       }
       for(int i = 0; i < byte_length.length; i++){
           full_message[i+ byte_count.length + used_chars.length ] = byte_length[i];
       }
       for(int i = 0; i < msg.length; i++){
           full_message[i + byte_count.length + used_chars.length + byte_length.length] = msg[i];
       }
       return full_message;

    }

    @Override
    public String decompress(byte[] byte_text) throws Exception {
        int[] charFrequencies = new int[alphabet];
        int length = 0;
        int count = 0;
        byte[] chars_count = new byte[4];
        for(int j = 0; j < 4; j ++){
            chars_count[j] = byte_text[j];
        }
        count+=4;
        ByteBuffer byteBuffer = ByteBuffer.wrap(chars_count);
        int chars = byteBuffer.getInt();

        for(int i = 0; i < chars*6; i+=6){
            byte[] character = new byte[2];
            for(int j = 0; j < 2; j++){
                character[j] = byte_text[count + j];
            }
            count+=2;
            ByteBuffer cb = ByteBuffer.wrap(character);
            byte[] integer = new byte[4];
            for(int j = 0; j < 4; j ++){
                integer[j] = byte_text[count + j];
            }
            count+=4;
            ByteBuffer bb = ByteBuffer.wrap(integer);
            charFrequencies[cb.getChar()] = bb.getInt();
        }
        byte[] integer = new byte[4];
        for(int i = 0; i < 4; i++){
            integer[i] = byte_text[i+count];
        }
        count+=4;
        ByteBuffer bb = ByteBuffer.wrap(integer);
        length = bb.getInt();

        byte[] msg = new byte[byte_text.length - count ];
        for(int i = 0; i < msg.length; i++){
            msg[i] = byte_text[i+count];
        }
        count+=msg.length;

        BitSet bitSet = BitSet.valueOf(msg);

        HuffmanTree decode_tree = HuffmanTree.buildHuffmanTree(charFrequencies);
        return decode_tree.decode(bitSet, length);
    }
}
