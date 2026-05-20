package HorasLaborales.demo.Models.DTO.Instructors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class InstructorDTO {

    //*** ATRIBUTOS ***\\

    @Positive  //El ID es positivo ya qué es generado por la base de datos.
    private Long instructorId;

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

    //Validaciones para el correo institucional del instructor
    /** Solo por mientras arreglo STMP
    @Pattern(
            regexp = "^[A-Za-z_]+@ricaldone\\.edu\\.sv$", //  Validación para correos institucionales que pueden contener letras y guion bajo
            message = "Debe ser un correo institucional de instructor válido" // Mensaje de error si el formato no es correcto
    )**/
    @NotBlank // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    private String email;

    //Validaciones para la contraseña del instructor
    @NotBlank(message = "La contraseña del instructor es obligatoria")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 8, message = ("La contraseña del instructor debe tener al menos 8 caracteres"))
    // Validación para el tamaño mínimo de la contraseña
    @Size(max = 255, message = ("La contraseña del instructor no puede exceder los 255 caracteres"))
    // Validación para el tamaño máximo de la contraseña
    private String password;

    //Validaciones para el año académico del instructor
    @Positive(message = "El ID del año académico debe ser positivo")
    // Validación para que el ID del año académico sea positivo
    private Long levelId;

    //Validaciones para el rol del instructor
    @Positive(message = "El ID del rol debe ser positivo")
    // Validación para que el ID del rol sea positivo
    private Long roleId;

    //Validaciones para la imagen del instructor
    @NotBlank(message = "La imagen del instructor es obligatoria")
    private String instructorImage; // Campo para almacenar la imagen del instructor (URL)

    //Campo adicional
    private String levelName;  // Campo adicional para mostrar el nombre del año académico, campo como tal no existe en tbInstructors
    private String roleName;  // Campo adicional para mostrar el nombre del rol, campo como tal no existe en tbInstructors


}
