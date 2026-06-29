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

    @Positive(message = "El ID de la orden de trabajo debe ser positivo")
    private Long workOrderId;

    @Positive(message = "El ID del vehículo debe ser positivo")
    private Long vehicleId;

    @Positive(message = "El ID del módulo debe ser positivo")
    private Long moduleId;

    @NotBlank(message = "El tiempo estimado es obligatorio")
    private String estimatedTime;

    @NotNull(message = "La imagen de la orden de trabajo es obligatoria")
    private String workOrderImage;

    @NotNull(message = "El estado es obligatorio")
    private Long idStatus;

    private String vehiclePlateNumber;
    private String moduleName;
    private String vehicleBrand;
    private String vehicleModel;
    private Integer vehicleYear;
    private String statusName;
    private String moduleCode;
    private String description;

    // NUEVOS campos para seguimiento
    private String studentName;
    private String studentLastName;
    private String ownerName;

    @JsonSetter("estimatedTime")
    public void setEstimatedTimeFromJson(Object value) {
        this.estimatedTime = value == null ? null : String.valueOf(value);
    }

    @JsonSetter("description")
    public void setDescriptionFromJson(Object value) {
        this.description = value == null ? null : String.valueOf(value);
    }

    @JsonSetter("studentName")
    public void setStudentNameFromJson(Object value) {
        this.studentName = value == null ? null : String.valueOf(value);
    }

    @JsonSetter("studentLastName")
    public void setStudentLastNameFromJson(Object value) {
        this.studentLastName = value == null ? null : String.valueOf(value);
    }

    @JsonSetter("ownerName")
    public void setOwnerNameFromJson(Object value) {
        this.ownerName = value == null ? null : String.valueOf(value);
    }
}