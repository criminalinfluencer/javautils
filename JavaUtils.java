import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.net.http.*;
import java.net.URI;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.util.regex.*;
import javax.swing.JOptionPane;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JavaUtils {

    private static final ReentrantLock lock = new ReentrantLock();

    static {
        System.out.print("\033[H\033[2J"); // Clear console
        System.out.flush();
    }

    public static class Colors {
        public static void info(String msg) {
            lock.lock();
            try {
                System.out.println("\033[34m#\033[0m " + msg);
            } finally {
                lock.unlock();
            }
        }

        public static void correct(String msg) {
            lock.lock();
            try {
                System.out.println("\033[32m+\033[0m " + msg);
            } finally {
                lock.unlock();
            }
        }

        public static void error(String msg) {
            lock.lock();
            try {
                System.out.println("\033[31m-\033[0m " + msg);
            } finally {
                lock.unlock();
            }
        }

        public static void warning(String msg) {
            lock.lock();
            try {
                System.out.println("\033[33m!\033[0m " + msg);
            } finally {
                lock.unlock();
            }
        }
    }

    public static class SessionConfig {
        public static HttpClient getSession(boolean proxy, boolean random) {
            HttpClient.Builder builder = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS);
            if (proxy) {
                builder.proxy(ProxySelector.of(new InetSocketAddress("proxy.proxy-cheap.com", 31112)));
            }
            return builder.build();
        }
    }

    public static class RandomString {
        public static String randAtoZ(int len) {
            return generateRandomString("abcdefghijklmnopqrstuvwxyz", len);
        }

        public static String rand0to9(int len) {
            return generateRandomString("0123456789", len);
        }

        public static String randStr(int len) {
            return generateRandomString("abcdefghijklmnopqrstuvwxyz1234567890", len);
        }

        private static String generateRandomString(String charset, int len) {
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < len; i++) {
                sb.append(charset.charAt(random.nextInt(charset.length())));
            }
            return sb.toString();
        }
    }

    public static int getFileLineCount(String file) {
        try {
            return Files.readAllLines(Paths.get(file + ".txt")).size();
        } catch (IOException e) {
            return 0;
        }
    }

    public static String getBinScheme(String bin) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://bin-checker.net/api/" + bin))
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.body()).path("scheme").asText();
        } catch (Exception e) {
            Colors.info("Error getting BIN scheme.");
            Colors.error(e.getMessage());
            return "Unknown";
        }
    }

    public static class Save {
        public static String getFirstLine(String filename) {
            lock.lock();
            try {
                return Files.lines(Paths.get(filename + ".txt")).findFirst().orElse("");
            } finally {
                lock.unlock();
            }
        }

        public static void removeFirstLine(String filename) {
            lock.lock();
            try {
                List<String> lines = Files.lines(Paths.get(filename + ".txt")).collect(Collectors.toList());
                if (!lines.isEmpty()) {
                    lines.remove(0);
                    Files.write(Paths.get(filename + ".txt"), lines, StandardCharsets.UTF_8);
                }
            } finally {
                lock.unlock();
            }
        }

        public static List<String> getFilePaths(String directory) {
            lock.lock();
            try {
                return Files.walk(Paths.get(directory))
                            .filter(Files::isRegularFile)
                            .map(Path::toString)
                            .collect(Collectors.toList());
            } finally {
                lock.unlock();
            }
        }

        public static void saveToFile(String filename, String textToSave, String place) {
            lock.lock();
            try {
                Path filePath = Paths.get(place + filename + ".txt");
                Files.write(filePath, textToSave.getBytes(StandardCharsets.UTF_8), 
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("script running.");
    }
}
