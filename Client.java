import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;

public class Client {
    private static JTextField textField;
    private static JTextField fieldNickname;
    private static JTextArea textArea;
    private static JLabel file;
    private static JLabel compression;
    private static JLabel coding;
    static PrintWriter bw;

    public static void main(String[] args) {
        try {
            JFrame window = new JFrame("MESSENGER");
            textField = new JTextField(30);
            fieldNickname = new JTextField(30);
            JButton button = new JButton("Send");
            Sender sender = new Sender();
            button.addActionListener(sender);
            textArea = new JTextArea(20, 30);
            TitledBorder chat = new TitledBorder("Chat");
            textArea.setBorder(chat);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            JPanel panel = new JPanel();
            TitledBorder nick = new TitledBorder("Username");
            fieldNickname.setBorder(nick);
            panel.add(fieldNickname);
            panel.add(scrollPane);
            TitledBorder input = new TitledBorder("Message");
            textField.setBorder(input);
            panel.add(textField);
            panel.add(button);
            JButton sendFile = new JButton("Choose file");
            panel.add(sendFile);
            TitledBorder files = new TitledBorder("Chosen file");


            JLabel label = new JLabel("                   Compression:");
            label.setVisible(true);
            panel.add(label);
            JRadioButton huffman = new JRadioButton("Huffman");
            JRadioButton LZ78 = new JRadioButton("LZ78");
            JRadioButton SF = new JRadioButton("Shannon-Fano");

            compression = new JLabel();
            compression.setVisible(true);
            ButtonGroup compressions = new ButtonGroup();
            compressions.add(huffman);
            compressions.add(LZ78);
            compressions.add(SF);
            panel.add(huffman);
            huffman.addActionListener(e -> compression.setText("Huffman"));
            panel.add(LZ78);
            LZ78.addActionListener(e -> compression.setText("LZ78"));
            panel.add(SF);
            SF.addActionListener(e -> compression.setText("Shannon-Fano"));

            JLabel jLabel = new JLabel("                                     Coding:");

            jLabel.setVisible(true);
            panel.add(jLabel);
            JRadioButton hamming = new JRadioButton("Hamming");
            JRadioButton rp3 = new JRadioButton("Repetition 3");
            JRadioButton rp5 = new JRadioButton("Repetition 5");

            JRadioButton crc8 = new JRadioButton("CRC8");

            coding = new JLabel();
            coding.setVisible(false);
            ButtonGroup codings = new ButtonGroup();
            codings.add(hamming);
            codings.add(rp3);
            codings.add(rp5);
            panel.add(hamming);
            hamming.addActionListener(e -> {
                if (hamming.isSelected() && crc8.isSelected()) {
                    coding.setText("Hamming,CRC8");
                } else if (hamming.isSelected()) {
                    coding.setText("Hamming");
                }
            });
            panel.add(rp3);
            rp3.addActionListener(e -> {
                if (crc8.isSelected() && rp3.isSelected()) {
                    coding.setText("Repetition 3,CRC8");
                } else if (rp3.isSelected()) {
                    coding.setText("Repetition 3");
                }
            });
            panel.add(rp5);
            rp5.addActionListener(e -> {
                if (crc8.isSelected() && rp5.isSelected()) {
                    coding.setText("Repetition 5,CRC8");
                } else if (rp5.isSelected()) {
                    coding.setText("Repetition 5");
                }
            });

            panel.add(crc8);
            crc8.setSelected(true);
            coding.setText("CRC8");
            crc8.addActionListener(e -> {
                if (!crc8.isSelected()) {
                    coding.setText(coding.getText().replace("CRC8", ""));
                    coding.setText(coding.getText().replace(",", ""));
                } else if (!coding.getText().contains("CRC8")) {
                    coding.setText(coding.getText() + ",CRC8");
                }
            });

            file = new JLabel();
            file.setVisible(false);
            sendFile.addActionListener(e -> {
                file.setVisible(true);
                JFileChooser fileopen = new JFileChooser();
                file.setBorder(files);
                panel.add(file);
                int ret = fileopen.showDialog(panel, "Open file");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File uploadedFile = fileopen.getSelectedFile();
                    file.setText(uploadedFile.getAbsolutePath());
                }
            });

            window.getContentPane().add(panel);
            window.setResizable(false);
            window.setLocationRelativeTo(null);
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            window.setSize(550, 650);
            window.setVisible(true);

            Socket socket = new Socket("10.240.20.71", 7777);
            init(socket);
            Thread thread = new Thread(new ClientListener(socket));
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void init(Socket socket) throws IOException {
        bw = new PrintWriter(socket.getOutputStream());
    }

    static void printMsg(String s) {
        textArea.append(s + "\n");
    }

    static class Sender implements ActionListener {
        private LZCompressor lzCompressor = new LZCompressor();
        private HuffmanCompressor huffmanCompressor = new HuffmanCompressor();
        private ShannonFanoCompressor shannonFanoCompressor = new ShannonFanoCompressor();
        private BigHammingCoder hammingCoder = new BigHammingCoder();
        private RepetitionCoder3 repetitionCoder3 = new RepetitionCoder3();
        private RepetitionCoder5 repetitionCoder5 = new RepetitionCoder5();
        private CRC8 crc8 = new CRC8();

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String file = Client.file.getText();
                Client.file.setVisible(false);
                String msg = textField.getText();
                String user = fieldNickname.getText();
                textField.setText("");
                Client.file.setText("");
                if (!Objects.equals(file, "")) {
                    Path path = Paths.get(file);

                    byte[] arr = Files.readAllBytes(path);
                    String[] parts = file.split("\\.");

                    int len = parts.length;
                    String extension = parts[len - 1];
                    String res = Translator.makeString(arr);

                    res = compress(res);
                    res = code(res);

                    bw.write(user + ":" + coding.getText() + ":" + compression.getText() + ":FILE:" + extension.length() + extension + "#" + res + "\n");
                    bw.flush();
                } else {
                    msg = compress(msg);
                    msg = code(msg);
                    bw.write(user + ":" + coding.getText() + ":" + compression.getText() + ":MSG:" + msg + "\n");

                    bw.flush();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        private String code(String message) {
            String coding = Client.coding.getText();
            if (coding.contains("CRC8")) {
                try {
                    System.out.println("Coding... " + coding);
                    message = Translator.makeString(crc8.code(Translator.makeBytes(message)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coding = coding.replace(",CRC8", "");
            }

            switch (coding) {
                case "Repetition 3":
                    try {
                        System.out.println("Coding... " + coding);
                        byte[] bytes = repetitionCoder3.code(message.getBytes());
                        return Base64.getEncoder().encodeToString(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "Repetition 5":
                    try {
                        System.out.println("Coding... " + coding);
                        byte[] bytes = repetitionCoder5.code(message.getBytes());
                        return Base64.getEncoder().encodeToString(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "Hamming":
                    try {
                        System.out.println("Coding... " + coding);
                        byte[] bytes = hammingCoder.code(message.getBytes());
                        return Base64.getEncoder().encodeToString(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return message;
        }

        private String compress(String message) {
            String compression = Client.compression.getText();
            switch (compression) {
                case "Huffman":
                    try {
                        System.out.println("Compression... " + compression);
                        byte[] bytes = huffmanCompressor.compress(message);
                        return Base64.getEncoder().encodeToString(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "LZ78":
                    try {
                        System.out.println("Compression... " + compression);
                        byte[] bytes = lzCompressor.compress(message);
                        return Base64.getEncoder().encodeToString(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "Shannon-Fano": {
                    System.out.println("Compression... " + compression);
                    byte[] bytes = shannonFanoCompressor.compress(message);
                    return Base64.getEncoder().encodeToString(bytes);
                }
            }
            return message;
        }
    }

    static class ClientListener implements Runnable {
        private LZCompressor lzCompressor = new LZCompressor();
        private HuffmanCompressor huffmanCompressor = new HuffmanCompressor();
        private ShannonFanoCompressor shannonFanoCompressor = new ShannonFanoCompressor();
        private RepetitionCoder3 repetitionCoder3 = new RepetitionCoder3();
        private RepetitionCoder5 repetitionCoder5 = new RepetitionCoder5();
        private BigHammingCoder hammingCoder = new BigHammingCoder();
        private CRC8 crc8 = new CRC8();
        BufferedReader br;

        ClientListener(Socket socket) throws IOException {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            System.out.println("I am ClientListener thread. Hi!");
            String msg;
            try {
                while ((msg = br.readLine()) != null) {
                    int i = 0;
                    while (msg.charAt(i) != ':') i++;
                    String user = msg.substring(0, i);
                    msg = msg.substring(i + 1);
                    i = 0;
                    while (msg.charAt(i) != ':') i++;
                    String code = msg.substring(0, i);
                    msg = msg.substring(i + 1);
                    i = 0;
                    while (msg.charAt(i) != ':') i++;
                    String compress = msg.substring(0, i);
                    msg = msg.substring(i + 1);
                    if (!msg.substring(0, 4).equals("MSG:")) {
                        i = 0;
                        while (msg.charAt(i) != '#') i++;
                        String temp = msg.substring(i + 1);
                        msg = msg.substring(0, i) + decompress(compress, decode(code, temp));
                    } else {
                        msg = decode(code, msg.substring(4));
                        msg = decompress(compress, msg);
                    }
                    if (msg.length() >= 5 && msg.substring(0, 5).equals("FILE:")) {
                        msg = msg.substring(5);
                        int extLen = msg.charAt(0) - '0';
                        String ext = msg.substring(1, extLen + 1);
                        printMsg("User " + user + " sent " + ext + "-file.");
                        msg = msg.substring(extLen + 1);
                        System.out.println("Listener listens: " + msg.length() + "bytes");
                        Saver.save(msg, "result." + ext);
                    } else {
                        printMsg(user + ": " + msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String decode(String code, String message) {
            boolean flag = false;
            if (code.contains("CRC8")) {
                code = code.replace(",CRC8", "");
                flag = true;
            }
            switch (code) {
                case "Repetition 3":
                    try {
                        System.out.println("Decoding... " + code);
                        message = new String(repetitionCoder3.decode(Base64.getDecoder().decode(message)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "Repetition 5":
                    try {
                        System.out.println("Decoding... " + code);
                        message = new String(repetitionCoder5.decode(Base64.getDecoder().decode(message)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "Hamming":
                    try {
                        System.out.println("Decoding... " + code);
                        message = new String(hammingCoder.decode(Base64.getDecoder().decode(message)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            if (flag) {
                try {
                    System.out.println("Decoding... CRC8");
                    message = Translator.makeString(crc8.decode(Translator.makeBytes(message)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return message;
        }

        private String decompress(String compression, String message) {
            switch (compression) {
                case "Huffman":
                    try {
                        System.out.println("Decompression... " + compression);
                        return huffmanCompressor.decompress(Base64.getDecoder().decode(message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "LZ78":
                    try {
                        System.out.println("Decompression... " + compression);
                        return new String(lzCompressor.decompress(new String(Base64.getDecoder().decode(message))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "Shannon-Fano": {
                    System.out.println("Decompression... " + compression);
                    return shannonFanoCompressor.decompress(Base64.getDecoder().decode(message));
                }
            }
            return message;
        }
    }
}


