// Собственный класс исключения для строк длиннее 1024 символов
class LineTooLongException extends RuntimeException {
    public LineTooLongException(String message) {
        super(message);
    }
}
