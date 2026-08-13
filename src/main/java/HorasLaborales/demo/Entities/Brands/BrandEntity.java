package HorasLaborales.demo.Entities.Brands;

import HorasLaborales.demo.Entities.VehicleModels.VehicleModelEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "TBBRANDS")
public class BrandEntity {

    //*** ATRIBUTOS ***\\

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_brands")
    @SequenceGenerator(name = "seq_brands", sequenceName = "seq_brands", allocationSize = 1)
    @Column(name = "BRANDID")
    private Long brandId;

    // Nombre de la marca (ej. Toyota, Honda), único y obligatorio
    @Column(name = "BRANDNAME", length = 50, nullable = false, unique = true)
    private String brandName;

    //*** ONETOMANYS ***\\

    @OneToMany(mappedBy = "brandId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbModels
    @JsonIgnore
    private List<VehicleModelEntity> models = new ArrayList<>(); // Modelos que pertenecen a esta marca

    @Override
    public String toString() {
        return "BrandEntity{" +
                "brandId=" + brandId +
                ", brandName='" + brandName + '\'' +
                '}';
    }
}
