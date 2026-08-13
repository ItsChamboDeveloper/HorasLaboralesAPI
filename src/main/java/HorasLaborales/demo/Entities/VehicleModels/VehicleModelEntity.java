package HorasLaborales.demo.Entities.VehicleModels;

import HorasLaborales.demo.Entities.Brands.BrandEntity;
import HorasLaborales.demo.Entities.Vehicles.VehicleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Nota de nombres: la tabla se llama TBMODELS, pero la clase se llama
 * VehicleModelEntity (no "ModelEntity") a propósito, para no confundirla
 * con el paquete Models.DTO que ya existe en el proyecto.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "TBMODELS")
public class VehicleModelEntity {

    //*** ATRIBUTOS ***\\

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_models")
    @SequenceGenerator(name = "seq_models", sequenceName = "seq_models", allocationSize = 1)
    @Column(name = "MODELID")
    private Long modelId;

    @ManyToOne(fetch = FetchType.LAZY) // Muchos modelos pueden pertenecer a una misma marca
    @JoinColumn(name = "BRANDID", referencedColumnName = "BRANDID") // Columna que conecta con la tabla de tbBrands
    private BrandEntity brandId;

    // Nombre del modelo (ej. Civic, Corolla), obligatorio
    @Column(name = "MODELNAME", length = 50, nullable = false)
    private String modelName;

    //*** ONETOMANYS ***\\

    @OneToMany(mappedBy = "vehicleModelId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbVehicles
    @JsonIgnore
    private List<VehicleEntity> vehicles = new ArrayList<>();

    @Override
    public String toString() {
        return "VehicleModelEntity{" +
                "modelId=" + modelId +
                ", brandId=" + brandId +
                ", modelName='" + modelName + '\'' +
                '}';
    }
}
