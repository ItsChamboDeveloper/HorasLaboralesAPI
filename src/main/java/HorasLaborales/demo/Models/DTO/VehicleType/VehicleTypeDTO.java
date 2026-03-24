package HorasLaborales.demo.Models.DTO.VehicleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class VehicleTypeDTO {

    @Positive
    private long typeId;
    @NotBlank(message = "El nombre del del tipo de vehiculo es obligatorio")
    private String typeName;

}
