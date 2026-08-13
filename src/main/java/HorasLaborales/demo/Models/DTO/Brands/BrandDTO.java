package HorasLaborales.demo.Models.DTO.Brands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class BrandDTO {

    @Positive
    private Long brandId;

    @NotBlank(message = "El nombre de la marca es obligatorio")
    private String brandName;

}
