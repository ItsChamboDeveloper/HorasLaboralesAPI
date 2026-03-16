package HorasLaborales.demo.Entities.VehiclesTypes;

import HorasLaborales.demo.Entities.Vehicles.VehicleEntity;
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
@Table(name = "TBVEHICLETYPES")
public class VehicleTypeEntity {

    //*** ATRIBUTOS ***\\

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_vehicle_types")
    @SequenceGenerator(name = "seq_vehicle_types", sequenceName = "seq_vehicle_types", allocationSize = 1)
    @Column(name = "TYPEID")
    private Long typeId;

    // Nombre del tipo de vehículo, obligatorio y máximo 50 caracteres
    @Column(name = "TYPENAME", length = 50, nullable = false)
    private String typeName;

    @OneToMany(mappedBy = "typeId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbVehicles
    @JsonIgnore
    private List<VehicleEntity> vehicleType = new ArrayList<>();

    @Override
    public String toString() {
        return "VehicleTypeEntity{" +
                "typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", vehicleType=" + vehicleType +
                '}';
    }

}
