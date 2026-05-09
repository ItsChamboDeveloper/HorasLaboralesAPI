package HorasLaborales.demo.Models.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class InstructorAuthenticationDTO {

    //*** ATRIBUTOS ***\\

    @NotBlank(message = "Para iniciar sesión el correo es requerido.")

    //Validaciones para el correo institucional del instructor
    @Pattern(
            regexp = "^[A-Za-z_]+@ricaldone\\.edu\\.sv$", //  Validación para correos institucionales que pueden contener letras y guion bajo
            message = "Debe ser un correo institucional de instructor válido" // Mensaje de error si el formato no es correcto
    )
    private String email;

    @NotBlank(message = "Para iniciar sesión la contraseña es requerida")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 8, message = ("La contraseña del instructor debe tener al menos 8 caracteres"))
    // Validación para el tamaño mínimo de la contraseña
    @Size(max = 255, message = ("La contraseña del instructor no puede exceder los 255 caracteres"))
    // Validación para el tamaño máximo de la contraseña
    private String password;

    //Validaciones para los nombres del instructor
    @NotBlank(message = "Los nombres del instructor son obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 5, message = "El nombre del instructor debe tener al menos 5 caracteres")
    // Validación para el tamaño máximo del nombre
    @Size(max = 50, message = "El nombre del instructor no puede exceder los 50 caracteres")
    // Validación para el tamaño mínimo del nombre
    private String firstName;

    //Validaciones para los apellidos del instructor
    @NotBlank(message = "Los apellidos del instructor son obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 5, message = "El apellido del instructor debe tener al menos 5 caracteres")
    // Validación para el tamaño máximo del apellido
    @Size(max = 50, message = "El apellido del instructor no puede exceder los 50 caracteres")
    // Validación para el tamaño mínimo del apellido
    private String lastName;

    //Validaciones para el rol del instructor
    @NotBlank(message = "El rol del instructor es obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 5, message = "El rol del instructor debe tener al menos 5 caracteres")
    // Validación para el tamaño máximo del rol
    @Size(max = 15, message = "El rol del instructor no puede exceder los 15 caracteres")
    // Validación para el tamaño mínimo del rol
    private String role;

    //Validaciones para el nivel del instructor
    @NotBlank(message = "El nivel del instructor es obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 5, message = "El nivel del instructor debe tener al menos 5 caracteres")
    // Validación para el tamaño máximo del nivel
    @Size(max = 20, message = "El nivel del instructor no puede exceder los 20 caracteres")
    // Validación para el tamaño mínimo del nivel
    private String Level;

}
