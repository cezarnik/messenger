import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * Created by user on 31.10.2017.
 */
public abstract class Coder {
     abstract  byte[] code(byte[]  arr) throws Exception;

     abstract byte[] decode(byte[] arr) throws Exception;

     void check(byte[] arr)  throws Exception{
         for(int i=0;i<arr.length;++i){

             if(arr[i]!=0 && arr[i]!=1){
                 System.out.println(i);
                 throw new Exception("not boolean value");
             }
         }
     }
//
//     String toString(byte[] data){
//        byte[] new_data;
//        if(data.length%2 == 1) {
//            new_data = new byte[data.length + 1];
//            for(int i = 0; i < data.length; i++){
//                new_data[i] = data[i];
//            }
//        }
//        else {
//            new_data = data;
//        }
//
//        char[] chars = new char[new_data.length/2];
//        byte[] a_char = new byte[2];
//        for(int i = 0; i < chars.length; i++){
//            for(int j = 0; j < 2; j++){
//                a_char[j] = new_data[i*2+j];
//            }
//            ByteBuffer bb = ByteBuffer.wrap(a_char);
//            chars[i] = bb.getChar();
//        }
//
//        return String.valueOf(chars);
//    }
//
//     byte[] toBitArray(byte[] data, int length){
//        BitSet bitSet = BitSet.valueOf(data);
//        byte[] result = new byte[length];
//        for(int i = 0; i < length; i++){
//            if(bitSet.get(i))
//                result[i] = 1;
//            else
//                result[i] = 0;
//        }
//        return result;
//    }
     byte[] toBitArray(byte[] arr){

        BitSet bitSet = BitSet.valueOf(arr);
        byte[] result = new byte[arr.length*8];

        for(int i = 0; i < result.length; i++){

            if(bitSet.get(i))
                result[i] = 1;
            else
                result[i] = 0;
        }
        return result;
    }

     byte[] compressToBytes(byte[] arr){
        BitSet bitSet = new BitSet();
        for(int i = 0; i < arr.length; i++){
            if(arr[i] == 1)
                bitSet.set(i);
            else if(arr[i] == 0)
                bitSet.set(i, false);

        }
        byte[] data =  bitSet.toByteArray();
        return data;
    }

}
