package HorasLaborales.demo.Models.DTO.Parents;

import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class ParentDTO {

    //*** ATRIBUTOS ***\\

    @Positive
    private Long parentId; // Identificador único, generado por la base de datos

    @NotBlank(message = "El nombre del papá/mamá es obligatorio")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido del papá/mamá es obligatorio")
    @Size(min = 3, message = "El apellido debe tener al menos 3 caracteres")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private String lastName;

    // Correo con el que inicia sesión (también se usa para el login)
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    @Size(max = 255, message = "El correo no puede exceder 255 caracteres")
    private String email;

    // Password en texto plano SOLO en la petición de entrada; el backend
    // lo encripta con Argon2 antes de guardarlo. Nunca se debe regresar
    // en las respuestas del API.
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Size(max = 255, message = "La contraseña no puede exceder 255 caracteres")
    private String password;

    @NotBlank(message = "El DUI es obligatorio")
    @Size(min = 10, max = 10, message = "El DUI debe tener exactamente 10 caracteres")
    @Pattern(regexp = "^[0-9]{8}-[0-9]$", message = "El formato del DUI es inválido (ej: 12345678-9)")
    private String dui;

}
