package HorasLaborales.demo.Exceptions.Modules;

public class ExceptionModuleNotFound extends RuntimeException {
    public ExceptionModuleNotFound(String message) {
        super(message);
    }
}
