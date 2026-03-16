package HorasLaborales.demo.Entities.Entries;

import HorasLaborales.demo.Entities.WorkOrders.WorkOrderEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity @Getter @Setter @EqualsAndHashCode
@Table(name = "TBENTRIES")
public class EntryEntity {
    //*** ATRIBUTOS ***\\
    @Id
    @Column(name = "ENTRYID") // Mapea el campo entryId con la columna ENTRYID de la tabla
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_entries")
    @SequenceGenerator(name = "seq_entries", sequenceName = "seq_entries", allocationSize = 1)
    private Long entryId; // Identificador único de la entrada

    @Column(name = "ENTRYDATE") // Mapea el campo entryDate con la columna ENTRYDATE de la tabla
    private LocalDate entryDate; // Fecha/hora de la entrada

    @ManyToOne(fetch = FetchType.LAZY)
    // Relación muchos a uno: varias entradas pueden estar asociadas a una misma orden de trabajo
    @JoinColumn(name = "WORKORDERID", referencedColumnName = "WORKORDERID")
    // Relaciona con la columna WORKORDERID de la tabla de órdenes de trabajo
    private WorkOrderEntity workOrderId; // Referencia a la orden de trabajo asociada a la entrada

    @Override
    public String toString() {
        return "EntryEntity{" +
                "entryId=" + entryId +
                ", entryDate=" + entryDate +
                ", workOrderId=" + workOrderId +
                '}';
    }
}
