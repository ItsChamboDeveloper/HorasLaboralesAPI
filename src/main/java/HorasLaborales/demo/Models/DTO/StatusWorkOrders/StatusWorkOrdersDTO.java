package HorasLaborales.demo.Models.DTO.StatusWorkOrders;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class StatusWorkOrdersDTO {

    //** ATRIBUTOS ** //

    @Positive //EL ID del status debe ser positivo, generado por la base de datos
    private Number idStatus;

    //Validacion para el StatusName
    @NotBlank(message= "Se requiere colocar el estado de la orden")
    //validación para el tamaño mínimo del mensaje
    @Size(min = 50, message = "Se requiere almenos 50 carácteres como mínimo")
    private String statusName;

}
