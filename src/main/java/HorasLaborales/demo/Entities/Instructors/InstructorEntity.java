package HorasLaborales.demo.Entities.Instructors;

import HorasLaborales.demo.Entities.Levels.LevelEntity;
import HorasLaborales.demo.Entities.Roles.RoleEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "TBINSTRUCTORS")
public class InstructorEntity {

    //*** ATRIBUTOS ***\\

    // ID del instructor, clave primaria generada automáticamente
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_instructors")
    @SequenceGenerator(name = "seq_instructors", sequenceName = "seq_instructors", allocationSize = 1)
    @Column(name = "INSTRUCTORID")
    private Long instructorId;

    // Nombres del instructor, no pueden ser nulo y tiene un tamaño máximo de 50 caracteres
    @Column(name = "FIRSTNAME", length = 50, nullable = false)
    private String firstName;

    // Apellidos del instructor, no pueden ser nulo y tiene un tamaño máximo de 50 caracteres
    @Column(name = "LASTNAME", length = 50, nullable = false)
    private String lastName;

    // Correo electrónico institucional del instructor, no puede ser nulo, debe ser único
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    // Contraseña del instructor, no puede ser nulo
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "INSTRUCTORIMAGE")
    private String instructorImage; // Campo para almacenar la imagen del instructor (URL)

    //*** MANYTOONEs ***\\

    @ManyToOne(fetch = FetchType.LAZY) // Muchos instructores pueden tener un mismo año académico
    @JoinColumn(name = "LEVELID", referencedColumnName = "LEVELID") // Columna que conecta con la tabla de tbLevels
    private LevelEntity levelId;

    @ManyToOne(fetch = FetchType.LAZY)// Muchos instructores pueden tener un mismo rol
    @JoinColumn(name = "ROLEID", referencedColumnName = "ROLEID") // Columna que conecta con la tabla de tbRoles
    private RoleEntity roleId;

    @Override
    public String toString() {
        return "InstructorEntity{" +
                "instructorId=" + instructorId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", instructorImage='" + instructorImage + '\'' +
                ", levelId=" + levelId +
                ", roleId=" + roleId +
                '}';
    }

}
