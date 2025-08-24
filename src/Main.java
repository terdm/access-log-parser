import java.io.File;
import java.util.Scanner;

public class Main{

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
        }
    }
}
