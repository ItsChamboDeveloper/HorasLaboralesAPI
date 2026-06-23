package HorasLaborales.demo.Models.DTO.WorkOrders;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class WorkOrderStatusDTO {

    @NotNull(message = "El idStatus es obligatorio")
    @Positive(message = "El idStatus debe ser un número positivo")
    private Long idStatus;

    // Mensaje personalizado opcional para incluir en el correo de notificación
    private String motivo;

}
