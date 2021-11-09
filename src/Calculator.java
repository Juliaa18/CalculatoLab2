

import java.util.List;
import java.util.Stack;
public class Calculator {

    private static final char DOT = '.';
    private static final List<Character> DIGITS = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    private static final List<Character> OPERATIONS = List.of('*', '+', '-', '/', '^', '(', ')');

    public double evaluate(String expression) {
        expression = normalizeExpression(expression);

        Stack<Character> operations = new Stack<>();
        Stack<Double> operands = new Stack<>();

        var positionInfo = new PositionInfo();

        int i = 0;
        while (i < expression.length()) {
            char sym = expression.charAt(i);

            if (isDigit(sym)) {
                double value = getNumber(expression, i, positionInfo);
                operands.push(value);
            } else if (isOperation(sym)) {
                char operation = getOperation(expression, i, positionInfo);
                processOperation(operation, operands, operations);
            } else {
                throw new InvalidExpressionException("Неожиданный символ '" + sym + "'");
            }

            i = positionInfo.getEndPosition();
        }


        if (operands.size() > 1 || operations.size() > 0) {
            throw new InvalidExpressionException("Выражение не является валидным");
        }

        return operands.peek();
    }


    public void processOperation(char operation, Stack<Double> operands, Stack<Character> operations) {
        if (operation == ')'){
            while (!operations.empty() && operations.peek() != '('){
                popOperation(operands, operations);
            }

            if (operations.empty()){
                throw new InvalidExpressionException("Ожидался символ '('");
            }

            operations.pop();
        } else {
            while (canPop(operation, operations)) {
                popOperation(operands, operations);
            }

            operations.push(operation);
        }
    }

    public boolean canPop(char operation, Stack<Character> operations){
        if (operations.isEmpty())
            return false;

        int firstPriority = getPriority(operation);
        int secondPriority = getPriority(operations.peek());

        return secondPriority >= 0 && firstPriority >= secondPriority;
    }

    private int getPriority(char operation) {
        switch (operation) {
            case '(':
                return -1;
            case '*': case '/': case '^':
                return 1;
            case '+': case '-':
                return 2;
            default:
                throw new RuntimeException("Неизвестная операция");
        }
    }

    public void popOperation(Stack<Double> operands, Stack<Character> operations){
        if (operands.size() < 2) {
            throw new InvalidExpressionException("Выражение не является валидным");
        }

        double b = operands.pop();
        double a = operands.pop();

        switch (operations.pop()){
            case '+':
                operands.push(a + b);
                break;
            case '-':
                operands.push(a - b);
                break;
            case '*':
                operands.push(a * b);
                break;
            case '/':
                operands.push(a / b);
                break;
            case '^':
                operands.push(Math.pow(a, b));
                break;
        }
    }

    private double getNumber(String expression, int startPosition, PositionInfo positionInfo) {
        int endPosition = startPosition;
        int dotCount = 0;

        char sym = expression.charAt(endPosition);
        while (isDigit(sym) || DOT == sym && ++dotCount == 1)  {
            sym = expression.charAt(++endPosition);
        }

        positionInfo.setEndPosition(endPosition);
        return Double.parseDouble(expression.substring(startPosition, endPosition));
    }

    private char getOperation(String expression, int startPosition, PositionInfo positionInfo) {
        positionInfo.setEndPosition(startPosition + 1);
        return expression.charAt(startPosition);
    }

    private boolean isDigit(char sym) {
        return DIGITS.contains(sym);
    }

    private boolean isOperation(char sym) {
        return OPERATIONS.contains(sym);
    }

    private String normalizeExpression(String expression) {
        return "(" + expression.replaceAll("\\s+","") + ")";
    }

    private static class PositionInfo {

        private int endPosition;

        public int getEndPosition() {
            return endPosition;
        }

        public void setEndPosition(int endPosition) {
            this.endPosition = endPosition;
        }
    }
}
