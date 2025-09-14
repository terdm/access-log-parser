import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {



    public static void processAccessLog(String path) {
        int totalLines = 0;
        int yandexBotCount = 0;
        int googleBotCount = 0;

        try (FileReader fileReader = new FileReader(path);
             BufferedReader reader = new BufferedReader(fileReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Проверка на максимальную длину строки
                if (line.length() > 1024) {
                    throw new LineTooLongException(
                            "Строка #" + (totalLines + 1) + " превышает максимально допустимую длину 1024 символа. " +
                                    "Длина строки: " + line.length() + " символов."
                    );
                }

                totalLines++;

                // Обработка User-Agent
                String userAgent = extractUserAgent(line);

                if (userAgent != null) {
                    String botName = extractBotNameFromUserAgent(userAgent);
                    if ("Googlebot".equals(botName)) {
                        googleBotCount++;
                    } else if ("YandexBot".equals(botName)) {
                        yandexBotCount++;
                    }
                }


            }

            // Вывод результатов
            System.out.println("Общее количество строк в файле: " + totalLines);
            System.out.println("Запросов от YandexBot: " + yandexBotCount);
            System.out.println("Запросов от Googlebot: " + googleBotCount);

            // Вывод долей
            if (totalLines > 0) {
                double yandexBotShare = (double) yandexBotCount / totalLines * 100;
                double googleBotShare = (double) googleBotCount / totalLines * 100;

                System.out.printf("Доля запросов от YandexBot: %.2f%%\n", yandexBotShare);
                System.out.printf("Доля запросов от Googlebot: %.2f%%\n", googleBotShare);
            }

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод для извлечения User-Agent из строки лога
    private static String extractUserAgent(String logLine) {
        // Разделяем строку по пробелам
        String[] parts = logLine.split(" ");

        // User-Agent
        // Ищем часть, которая начинается с кавычки и содержит информацию о браузере
        for (int i = parts.length - 1; i >= 0; i--) {
            if (parts[i].startsWith("\"") && parts[i].length() > 1) {
                // Объединяем оставшиеся части для получения полного User-Agent
                StringBuilder userAgentBuilder = new StringBuilder();
                for (int j = i; j < parts.length; j++) {
                    userAgentBuilder.append(parts[j]).append(" ");
                }
                String userAgent = userAgentBuilder.toString().trim();

                // Удаляем окружающие кавычки
                if (userAgent.startsWith("\"") && userAgent.endsWith("\"")) {
                    userAgent = userAgent.substring(1, userAgent.length() - 1);
                }
                return userAgent;
            }
        }
        return null;
    }

    // Метод для извлечения имени бота из User-Agent
    private static String extractBotNameFromUserAgent(String userAgent) {
        // Сначала пытаемся найти по стандартному шаблону (первые скобки)
        try {
            Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
            Matcher matcher = pattern.matcher(userAgent);

            if (matcher.find()) {
                String firstBrackets = matcher.group(1);
                String[] parts = firstBrackets.split(";");

                if (parts.length >= 2) {
                    String fragment = parts[1].trim();
                    int slashIndex = fragment.indexOf('/');
                    if (slashIndex != -1) {
                        String botName = fragment.substring(0, slashIndex).trim();

                        // Проверяем только нужных ботов
                        if ("Googlebot".equals(botName) || "YandexBot".equals(botName)) {
                            return botName;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Продолжаем поиск другими методами
        }

        // Резервный поиск по всей строке
        if (userAgent.contains("YandexBot") || userAgent.toLowerCase().contains("yandexbot")) {
            return "YandexBot";
        }
        if (userAgent.contains("Googlebot") || userAgent.toLowerCase().contains("googlebot")) {
            return "Googlebot";
        }

        return null;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int correctFileCount = 0;

        while (true) {
            System.out.println("Введите путь к файлу:");
            String filePath = scanner.nextLine();

            // Проверяем существование файла
            File file = new File(filePath);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists || isDirectory) {
                System.out.println("Путь введён ошибочно, указывает на папку или на несуществующий файл: " + filePath);
                continue;
            }

            // Если файл существует и это действительно файл
            System.out.println("Путь указан верно");
            correctFileCount++;
            System.out.println("Это файл номер " + correctFileCount);

            try {
                processAccessLog(filePath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }
}