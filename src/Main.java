import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
public class Main {

    public static class LineTooLongException extends RuntimeException {
        public LineTooLongException(String message) {
            super(message);
        }
    }

    public static void processAccessLog(String path) {
        Statistics statistics = new Statistics();
        int totalLines = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Проверка на максимальную длину строки
                if (line.length() > 1024) {
                    throw new LineTooLongException(
                            "Строка #" + (totalLines + 1) + " превышает максимально допустимую длину 1024 символа."
                    );
                }

                totalLines++;

                try {
                    LogEntry entry = new LogEntry(line);
                    statistics.addEntry(entry);


                } catch (Exception e) {
                    System.err.println("Ошибка при обработке строки #" + totalLines + ": " + e.getMessage());
                }
            }

            // Вывод результатов
            System.out.println("Общее количество строк в файле: " + totalLines);
            System.out.println("Успешно обработано записей: " + statistics.getEntryCount());
            System.out.println("Общий трафик: " + statistics.getTotalTraffic() + " bytes");
            System.out.println("Период логов: от " + statistics.getMinTime() + " до " + statistics.getMaxTime());
            System.out.printf("Средний часовой трафик: %.2f bytes/hour\n", statistics.getTrafficRate());
            System.out.println("Существующие траницы " + statistics.getExistingPages().toString());
            System.out.println("Статистика ОС " + statistics.getOperatingSystemStatistics().toString());

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            e.printStackTrace();
        }
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