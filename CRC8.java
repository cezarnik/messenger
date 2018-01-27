import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class CRC8 extends Coder {
    private static final int poly = 0x0D5;
    private int crc = 0;

    public int getValue() {
        return crc;
    }

    public void reset() {
        crc = 0;
    }


    @Override
    byte[] code(byte[] data) throws Exception {
        reset();
        int offset = 0;
        int len = data.length;
        for (int i = 0; i < len; i++) {
            crc ^= data[offset+i];
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x80) != 0) {
                    crc = ((crc << 1) ^ poly);
                } else {
                    crc <<= 1;
                }
            }

        }

        byte[] result = new byte[data.length + 4];
        for(int i = 0; i < data.length; i++){
            result[i] = data[i];
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(getValue());
        for(int i = 0; i < 4; i++){
            result[data.length + i] = byteBuffer.array()[i];
        }
        return result;
    }

    @Override
    byte[] decode(byte[] data) throws Exception {
        reset();
        byte[] result = new byte[data.length - 4];
        for(int i = 0; i < data.length - 4; i++){
            result[i] = data[i];
        }
        int offset = 0;
        int len = result.length;
        for (int i = 0; i < len; i++) {
            crc ^= result[offset+i];
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x80) != 0) {
                    crc = ((crc << 1) ^ poly);
                } else {
                    crc <<= 1;
                }
            }

        }
        byte[] integer = new byte[4];
        for(int j = 0; j < 4; j ++){
            integer[j] = data[data.length - 4 + j];
        }
        ByteBuffer bb = ByteBuffer.wrap(integer);
        int checkSum = bb.getInt();
        if(checkSum != getValue()){
            throw new Exception("An error has occurred");
        }
        else{
            System.out.println("Checksum is ok");
        }
        return result;
    }
}
