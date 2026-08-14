package HorasLaborales.demo.Entities.WorkOrders;

import HorasLaborales.demo.Entities.Entries.EntryEntity;
import HorasLaborales.demo.Entities.Modules.ModuleEntity;
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
@Table(name = "TBWORKORDERS")
public class WorkOrderEntity {

//*** ATRIBUTOS ***\\

    // ID de la orden de trabajo, clave primaria generada automáticamente
    @Id
    @Column(name = "WORKORDERID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_work_orders")
    @SequenceGenerator(name = "seq_work_orders", sequenceName = "seq_work_orders", allocationSize = 1)
    private Long workOrderId;

    @ManyToOne // Muchas órdenes de trabajo pueden estar asociadas a un mismo vehículo
    @JoinColumn(name = "VEHICLEID", referencedColumnName = "VEHICLEID") // Columna que conecta con la tabla de Vehículos
    private VehicleEntity vehicleId;

    @ManyToOne // Muchas órdenes de trabajo pueden estar asociadas a un mismo módulo académico
    @JoinColumn(name = "MODULEID", referencedColumnName = "MODULEID") // Columna que conecta con la tabla de Módulos
    private ModuleEntity moduleId;

    // Tiempo estimado para la orden de trabajo (puede ser en horas, minutos, etc.)
    @Column(name = "ESTIMATEDTIME", length = 5)
    private String estimatedTime;

    // Imagen asociada a la orden de trabajo (frontal, puede ser URL o base64)
    @Column(name = "WORKORDERIMAGE")
    private String workOrdersImage;

    // Imagen lateral izquierda de la orden de trabajo
    @Column(name = "WORKORDERIMAGELEFT", length = 500)
    private String workOrderImageLeft;

    // Imagen lateral derecha de la orden de trabajo
    @Column(name = "WORKORDERIMAGERIGHT", length = 500)
    private String workOrderImageRight;

    // Imagen trasera de la orden de trabajo
    @Column(name = "WORKORDERIMAGEBACK", length = 500)
    private String workOrderImageBack;

    // Estado de la orden de trabajo (por ejemplo: pendiente, en proceso, finalizada)
    // Cambiado a idStatus y mapeado a la columna IDSTATUS en BD para evitar ORA-00904 si la columna real es IDSTATUS
    @Column(name = "IDSTATUS")
    private Long idStatus;

    // Nuevo campo descripción
    @Column(name = "DESCRIPTION", length = 300)
    private String description;

    //*** ONETOMANYS ***\\

    @OneToMany(mappedBy = "workOrderId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbEntries
    @JsonIgnore
    private List<EntryEntity> entry = new ArrayList<>(); // Lista de entradas asociadas a la orden de trabajo

    @Override
    public String toString() {
        return "WorkOrderEntity{" +
                "workOrderId=" + workOrderId +
                ", vehicleId=" + vehicleId +
                ", moduleId=" + moduleId +
                ", estimatedTime='" + estimatedTime + '\'' +
                ", workOrdersImage='" + workOrdersImage + '\'' +
                ", workOrderImageLeft='" + workOrderImageLeft + '\'' +
                ", workOrderImageRight='" + workOrderImageRight + '\'' +
                ", workOrderImageBack='" + workOrderImageBack + '\'' +
                ", idStatus=" + idStatus +
                ", description='" + description + '\'' +
                ", entry=" + entry +
                '}';
    }

}
