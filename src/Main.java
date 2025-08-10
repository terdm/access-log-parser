import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        System.out.println("Введите первое число:");
        int firstNumber = new Scanner(System.in).nextInt();

        System.out.println("Введите второе число:");
        int secondNumber = new Scanner(System.in).nextInt();

        int summa = firstNumber + secondNumber;
        System.out.println("Сумма: " + summa);

        int substruct = firstNumber - secondNumber;
        System.out.println("Разность: " + substruct);

        int multiplication = firstNumber * secondNumber;
        System.out.println("Произведение: " + multiplication);

        double quotient = (double)firstNumber / secondNumber;
        System.out.println("Частное: " + quotient);
    }
}
