package HorasLaborales.demo.Entities.Grades;

import HorasLaborales.demo.Entities.Levels.LevelEntity;
import HorasLaborales.demo.Entities.Students.StudentEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter @EqualsAndHashCode
@Table(name = "TBGRADES")
public class GradeEntity {

//*** ATRIBUTOS ***\\

    @Id // Indica que este campo es la clave primaria de la entidad
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_grades")
    @SequenceGenerator(name = "seq_grades", sequenceName = "seq_grades", allocationSize = 1)
    @Column(name = "GRADEID") // Mapea el campo id con la columna GRADEID de la tabla
    private Long gradeId; // Identificador único del grado

    @ManyToOne // Relación muchos a uno: varios grados pueden estar asociados a un mismo nivel académico
    @JoinColumn(name = "LEVELID", referencedColumnName = "LEVELID") // Columna que conecta con la tabla de tbLevels
    private LevelEntity levelId; // Referencia al nivel académico asociado al grado

    @Column(name = "GRADEGROUP", nullable = false) // Mapea el campo gradeGroup con la columna GRADEGROUP de la tabla, no puede ser nulo
    private Long gradeGroup; // Número de grupo dentro del grado

    //*** ONETOMANYS ***\\

    @OneToMany(mappedBy = "gradeId", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relación OneToMany con tbStudents
    @JsonIgnore
    private List<StudentEntity> student = new ArrayList<>(); // Grado asociado al estudiante

    @Override
    public String toString() {
        return "GradeEntity{" +
                "gradeId=" + gradeId +
                ", levelId=" + levelId +
                ", gradeGroup=" + gradeGroup +
                ", student=" + student +
                '}';
    }

}
