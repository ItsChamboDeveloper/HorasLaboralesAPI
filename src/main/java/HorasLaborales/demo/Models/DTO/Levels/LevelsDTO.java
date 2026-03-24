package HorasLaborales.demo.Models.DTO.Levels;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class LevelsDTO {

    @Positive(message = "El ID debe ser un número positivo")
    private Long id;

    @NotBlank(message = "El nombre del nivel no puede estar vacío")
    private String levelName;

}
