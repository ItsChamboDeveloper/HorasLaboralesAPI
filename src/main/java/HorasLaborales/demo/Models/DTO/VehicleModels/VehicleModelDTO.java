package HorasLaborales.demo.Models.DTO.VehicleModels;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class VehicleModelDTO {

    @Positive
    private Long modelId;

    @NotNull(message = "La marca es obligatoria")
    @Positive(message = "El ID de la marca debe ser positivo")
    private Long brandId;

    @NotBlank(message = "El nombre del modelo es obligatorio")
    private String modelName;

    // Campo adicional para mostrar el nombre de la marca; no existe como tal en TBMODELS
    private String brandName;

}
