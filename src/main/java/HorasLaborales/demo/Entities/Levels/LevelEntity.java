package HorasLaborales.demo.Entities.Levels;

import HorasLaborales.demo.Entities.Grades.GradeEntity;
import HorasLaborales.demo.Entities.Instructors.InstructorEntity;
import HorasLaborales.demo.Entities.Modules.ModuleEntity;
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
@Table(name = "TBLEVELS")
public class LevelEntity {

    //*** ATRIBUTOS ***\\

    // ID del nivel, clave primaria generada automáticamente
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_levels")
    @SequenceGenerator(name = "seq_levels", sequenceName = "seq_levels", allocationSize = 1)
    @Column(name = "LEVELID")
    private Long levelId;

    // Nombre del nivel académico, no puede ser nulo
    @Column(name = "LEVELNAME", nullable = false)
    private String levelName;

    //*** ONETOMANYS ***\\

    @OneToMany(mappedBy = "levelId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbGrades
    private List<GradeEntity> grade = new ArrayList<>(); // Lista de grados asociados al nivel

    @OneToMany(mappedBy = "levelId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbInstructors
    private List<InstructorEntity> instructor = new ArrayList<>(); // Lista de instructores asociados al nivel

    @OneToMany(mappedBy = "levelId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbModules
    private List<ModuleEntity> module = new ArrayList<>(); // Lista de módulos asociados al nivel

    @Override
    public String toString() {
        return "LevelEntity{" +
                "levelId=" + levelId +
                ", levelName='" + levelName + '\'' +
                ", grade=" + grade +
                ", instructor=" + instructor +
                ", module=" + module +
                '}';
    }

}
