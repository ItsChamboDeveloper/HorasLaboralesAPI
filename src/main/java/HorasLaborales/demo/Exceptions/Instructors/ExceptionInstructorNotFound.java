package HorasLaborales.demo.Exceptions.Instructors;

public class ExceptionInstructorNotFound extends RuntimeException {
    public ExceptionInstructorNotFound(String message) {
        super(message);
    }
}
