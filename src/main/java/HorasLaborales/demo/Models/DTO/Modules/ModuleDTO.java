package HorasLaborales.demo.Models.DTO.Modules;

import HorasLaborales.demo.Models.DTO.Instructors.InstructorInfoDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@EqualsAndHashCode @ToString
public class ModuleDTO {

    //*** ATRIBUTOS ***\\

    @Positive // El ID del módulo debe ser positivo, generado por la base de datos
    private Long moduleId;

    // Validaciones para el nombre del módulo
    @NotBlank(message = "El nombre del módulo no puede estar vacío")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 5, message = "El nombre del módulo debe tener al menos 5 caracteres")
    // Validación para el tamaño máximo del nombre
    @Size(max = 100, message = "El nombre del módulo no puede exceder los 50 caracteres")
    // Validación para el tamaño mínimo del nombre
    private String moduleName;

    // Nuevo campo: Código corto del módulo
    @NotBlank(message = "El código del módulo no puede estar vacío")
    @Size(max = 20, message = "El código del módulo no puede exceder los 20 caracteres")
    private String moduleCode;

    @NotNull(message = "El ID del nivel es obligatorio")
    @Positive(message = "El ID del nivel debe ser positivo")
    private Long levelId;

    //Campo adicional
    private String levelName;  // Campo adicional para mostrar el nombre del año académico, campo como tal no existe en tbModules

    @NotNull(message = "El ID del instructor es obligatorio")
    @Positive(message = "El ID del instructor debe ser positivo")
    private Long instructorId;

    // Para GET: detalles del instructor
    private InstructorInfoDTO instructor;

}
