import java.io.File;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main{
    public static void processAccessLog(String path) {
        int totalLines = 0;
        int minLength = Integer.MAX_VALUE;
        int maxLength = 0;

        try (FileReader fileReader = new FileReader(path);
             BufferedReader reader = new BufferedReader(fileReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                int length = line.length();

                // Проверка на максимальную длину строки
                if (length > 1024) {
                    throw new LineTooLongException(
                            "Строка #" + (totalLines + 1) + " превышает максимально допустимую длину 1024 символа. " +
                                    "Длина строки: " + length + " символов."
                    );
                }

                // Обновление статистики
                totalLines++;
                if (length < minLength) {
                    minLength = length;
                }
                if (length > maxLength) {
                    maxLength = length;
                }
            }

            // Вывод результатов
            System.out.println("Общее количество строк в файле: " + totalLines);
            System.out.println("Длина самой короткой строки: " + (minLength == Integer.MAX_VALUE ? 0 : minLength));
            System.out.println("Длина самой длинной строки: " + maxLength);

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

            // Проверяем, является ли путь папкой
            boolean isDirectory = file.isDirectory();

            // Проверяем условия
            if (!fileExists || isDirectory) {
                System.out.println("Путь введён ошибочно, указывает на папку или на несуществующий файл: " + filePath);
                continue; // Продолжаем цикл
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
