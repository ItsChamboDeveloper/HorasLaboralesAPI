package HorasLaborales.demo.Services.Grades;

import HorasLaborales.demo.Entities.Grades.GradeEntity;
import HorasLaborales.demo.Models.DTO.Grades.GradeDTO;
import HorasLaborales.demo.Repositories.Grades.GradeRepository;
import HorasLaborales.demo.Repositories.Levels.LevelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j @Service
public class GradeService {

    @Autowired
    private GradeRepository repo;

    @Autowired
    private LevelRepository levelRepo;

    // Obtener todos los grados
    public List<GradeDTO> getAllGrades() {
        return repo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors
                        .toList());
    }

//    // Insertar un grado
//    public GradeDTO insert(@Valid GradeDTO dto) {
//        LevelEntity level = levelRepo.findById(dto.getLevelId())
//                .orElseThrow(() -> new ExceptionLevelNotFound("Nivel no encontrado con ID: " + dto.getLevelId()));
//
//        try {
//            GradeEntity entity = convertToEntity(dto, level);
//            GradeEntity saved = repo.save(entity);
//            return convertToDTO(saved);
//        } catch (Exception e) {
//            log.error("Error al registrar el grado: " + e.getMessage());
//            throw new ExceptionGradeDontRegister("No se pudo registrar el grado");
//        }
//    }
//
//    // Actualizar un grado
//    public GradeDTO update(Long id, @Valid GradeDTO dto) {
//        GradeEntity grade = repo.findById(id)
//                .orElseThrow(() -> new ExceptionGradeNotFound("Grado no encontrado con ID: " + id));
//
//        LevelEntity level = levelRepo.findById(dto.getLevelId())
//                .orElseThrow(() -> new ExceptionLevelNotFound("Nivel no encontrado con ID: " + dto.getLevelId()));
//
//        grade.setGroupName(dto.getGroupName());
//        grade.setLevel(level);
//
//        try {
//            GradeEntity updated = repo.save(grade);
//            return convertToDTO(updated);
//        } catch (Exception e) {
//            log.error("Error al actualizar el grado: " + e.getMessage());
//            throw new ExceptionGradeDontRegister("No se pudo actualizar el grado");
//        }
//    }
//
//    // Eliminar un grado
//    public boolean delete(Long id) {
//        GradeEntity grade = repo.findById(id).orElse(null);
//        if (grade != null) {
//            repo.deleteById(id);
//            return true;
//        }
//        return false;
//    }

    // Métodos privados de conversión
    private GradeDTO convertToDTO(GradeEntity entity) {
        GradeDTO dto = new GradeDTO();
        dto.setGradeId(entity.getGradeId());
        dto.setGradeGroup(entity.getGradeGroup());
        dto.setLevelId(entity.getLevelId().getLevelId());
        return dto;
    }

//    private GradeEntity convertToEntity(GradeDTO dto, LevelEntity level) {
//        GradeEntity entity = new GradeEntity();
//        entity.setGroupName(dto.getGroupName());
//        entity.setLevel(level);
//        return entity;
//    }

}
