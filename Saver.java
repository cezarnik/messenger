import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Saver {
    static boolean save(String data, String path) {
        Path _path = Paths.get(path);
        try {
            Files.write(_path, Translator.makeBytes(data));
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
