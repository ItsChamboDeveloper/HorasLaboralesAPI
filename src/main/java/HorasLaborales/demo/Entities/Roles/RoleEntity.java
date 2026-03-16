package HorasLaborales.demo.Entities.Roles;

import HorasLaborales.demo.Entities.Instructors.InstructorEntity;
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
@Table(name = "TBROLES")
public class RoleEntity {

    //*** ATRIBUTOS ***\\

    // ID del rol, clave primaria generada automáticamente
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_roles")
    @SequenceGenerator(name = "seq_roles", sequenceName = "seq_roles", allocationSize = 1)
    @Column(name = "ROLEID")
    private Long roleId;

    // Nombre del rol, no puede ser nulo y tiene un tamaño máximo de 50 caracteres
    @Column(name = "ROLENAME", length = 50, nullable = false)
    private String roleName;

    @OneToMany(mappedBy = "roleId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbInstructor
    @JsonIgnore // Ignorar en la serialización JSON para evitar referencias circulares
    private List<InstructorEntity> instructor = new ArrayList<>(); // Lista de órdenes de trabajo asociadas al Instructor

    @Override
    public String toString() {
        return "RoleEntity{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", instructor=" + instructor +
                '}';
    }
}
