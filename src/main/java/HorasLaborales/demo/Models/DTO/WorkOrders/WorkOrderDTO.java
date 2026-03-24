package HorasLaborales.demo.Models.DTO.WorkOrders;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class WorkOrderDTO {

    //*** ATRIBUTOS ***\\

    @Positive(message = "El ID de la orden de trabajo debe ser positivo") // El ID es generado por la base de datos y debe ser positivo
    private Long workOrderId;

    @Positive(message = "El ID del vehículo debe ser positivo") // Referencia al vehículo asociado a la orden de trabajo
    private Long vehicleId;

    @Positive(message = "El ID del módulo debe ser positivo") // Referencia al módulo académico relacionado con la orden
    private Long moduleId;

    @NotBlank(message = "El tiempo estimado es obligatorio") // El tiempo estimado no puede estar en blanco
    private String estimatedTime;

    @NotNull(message = "La imagen de la orden de trabajo es obligatoria") // La imagen de la orden es obligatoria (puede ser URL o base64)
    private String workOrderImage;

    @NotNull(message = "El estado es obligatorio") // El estado de la orden de trabajo no puede ser nulo
    private Long idStatus;

    //Campo adicional
    private String vehiclePlateNumber; // Campo adicional para mostrar el número de placa del vehículo, campo como tal no existe en tbWorkOrders
    private String moduleName; // Campo adicional para mostrar el nombre del módulo, campo como tal no existe en tbWorkOrders

    // Campos añadidos para respuesta expandida
    private String vehicleBrand;
    private String vehicleModel;
    private Integer vehicleYear; // No existe en la entidad actual; quedará nulo si no está disponible
    private String statusName;
    private String moduleCode;

    // Nuevo campo descripción (opcional)
    private String description;

    // JsonSetters tolerantes: aceptan números o strings y convierten a String internamente
    @JsonSetter("estimatedTime")
    public void setEstimatedTimeFromJson(Object value) {
        this.estimatedTime = value == null ? null : String.valueOf(value);
    }

    @JsonSetter("description")
    public void setDescriptionFromJson(Object value) {
        this.description = value == null ? null : String.valueOf(value);
    }

}
