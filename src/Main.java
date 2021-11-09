import java.io.BufferedReader;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception{
        Calculator calculator = new Calculator();
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();

        try {
            double result = calculator.evaluate(input);
            System.out.println("Результат: " + Double.toString(result));
        } catch (Exception ex){
            System.out.println("Случилась ошибка: " + ex.getMessage());
        }
    }

}
