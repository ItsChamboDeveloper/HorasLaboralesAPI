package HorasLaborales.demo.Entities.Parents;

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
@Table(name = "TBPARENTS")
public class ParentEntity {

    //*** ATRIBUTOS ***\\

    // ID del papá/mamá, clave primaria generada automáticamente
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_parents")
    @SequenceGenerator(name = "seq_parents", sequenceName = "seq_parents", allocationSize = 1)
    @Column(name = "PARENTID")
    private Long parentId;

    // Nombres del papá/mamá, no puede ser nulo, máximo 50 caracteres
    @Column(name = "FIRSTNAME", length = 50, nullable = false)
    private String firstName;

    // Apellidos del papá/mamá, no puede ser nulo, máximo 50 caracteres
    @Column(name = "LASTNAME", length = 50, nullable = false)
    private String lastName;

    // Correo con el que el papá/mamá inicia sesión, único y obligatorio
    @Column(name = "EMAIL", length = 255, nullable = false, unique = true)
    private String email;

    // Password encriptado con Argon2 (nunca texto plano), obligatorio
    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;

    // DUI del papá/mamá, único y obligatorio, formato ########-#
    @Column(name = "DUI", length = 10, nullable = false, unique = true)
    private String dui;

    //*** ONETOMANYS ***\\

    @OneToMany(mappedBy = "parentId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbVehicles
    @JsonIgnore // Ignorar en la serialización JSON para evitar referencias circulares
    private List<VehicleEntity> vehicles = new ArrayList<>(); // Vehículos asociados a este papá/mamá

    @Override
    public String toString() {
        return "ParentEntity{" +
                "parentId=" + parentId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dui='" + dui + '\'' +
                '}';
    }
}
