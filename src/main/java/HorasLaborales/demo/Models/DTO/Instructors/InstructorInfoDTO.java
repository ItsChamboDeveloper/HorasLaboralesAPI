package HorasLaborales.demo.Models.DTO.Instructors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @EqualsAndHashCode
public class InstructorInfoDTO {

    private Long instructorId;
    private String instructorName;

}
