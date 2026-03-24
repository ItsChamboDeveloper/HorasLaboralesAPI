package HorasLaborales.demo.Models.DTO.Observations;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class ObservationDTO {

    private Long observacionId;
    private Long workOrderId;
    private String observacion;
    private String imageUrl;
    private Long studentId;
    private String studentName; // optional for responses

}
