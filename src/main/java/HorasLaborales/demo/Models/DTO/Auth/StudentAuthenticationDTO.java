package HorasLaborales.demo.Models.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class StudentAuthenticationDTO {

    //*** ATRIBUTOS ***\\

    //Validaciones para la tarjeta de identificación del estudiante (Código)
    @NotBlank(message = "La tarjeta de identificación del estudiante es obligatoria")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 8, message = "El código del estudiante debe de ser de 8 caracteres")
    // Validación para el tamaño máximo del código
    @Size(max = 8, message = "El código del estudiante debe de ser de 8 caracteres")
    // Validación para el tamaño mínimo del código
    private String studentCard; // Campo para almacenar la tarjeta de identificación del estudiante (Código)

    //Validaciones para los nombres del estudiante
    @NotBlank(message = "Los nombres del estudiante son obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 5, message = "El nombre del estudiante debe tener al menos 5 caracteres")
    // Validación para el tamaño máximo del nombre
    @Size(max = 50, message = "El nombre del estudiante no puede exceder los 50 caracteres")
    // Validación para el tamaño mínimo del nombre
    private String firstName;

    //Validaciones para los apellidos del estudiante
    @NotBlank(message = "Los apellidos del estudiante son obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 5, message = "El apellido del estudiante debe tener al menos 5 caracteres")
    // Validación para el tamaño máximo del apellido
    @Size(max = 50, message = "El apellido del estudiante no puede exceder los 50 caracteres")
    // Validación para el tamaño mínimo del apellido
    private String lastName;

    //Validaciones para el correo institucional del estudiante
    @Pattern(
            regexp = "^[0-9]{8}@ricaldone\\.edu\\.sv$", // Expresión regular para validar el formato del correo institucional
            message = "Debe ser un correo institucional válido" // Mensaje de error si el formato no es correcto
    )
    @NotBlank // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    //Se omite @Size ya qué Pattern valida el tamaño, en este caso exacto los 27 caracteres del correo institucional.
    private String email;

    //Validaciones para la contraseña del estudiante
    @NotBlank(message = "La contraseña del estudiante es obligatoria")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 8, message = "La contraseña del estudiante debe tener al menos 8 dígitos")
    // Validación para el tamaño mínimo de la contraseña
    @Size(max = 255, message = ("La contraseña del estudiante no puede exceder los 255 dígitos"))
    // Validación para el tamaño máximo de la contraseña
    private String password;

    //Validaciones para el año académico del estudiante
    @Positive(message = "El año académico debe ser positivo")
    // Validación para que el año académico sea positivo
    private Long grade;

}
