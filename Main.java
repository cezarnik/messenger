import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        Path path = Paths.get("a.png");
        byte[] array = Files.readAllBytes(path);
        int init = array.length;
        String res = Translator.makeString(array);
        init = res.length() * 8;
        ShannonFanoCompressor shannonFanoCompressor = new ShannonFanoCompressor();
        array = shannonFanoCompressor.compress(res);
        String r = shannonFanoCompressor.decompress(array);
    }
}
