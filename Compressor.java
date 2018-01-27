/**
 * Created by user on 03.11.2017.
 */
public abstract class Compressor {
    public abstract byte[] compress(String msg) throws Exception;

    public abstract String decompress(byte[] msg) throws Exception;
}
