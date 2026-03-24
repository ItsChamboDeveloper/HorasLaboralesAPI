package HorasLaborales.demo.Services.Levels;

import HorasLaborales.demo.Entities.Levels.LevelEntity;
import HorasLaborales.demo.Models.DTO.Levels.LevelsDTO;
import HorasLaborales.demo.Repositories.Levels.LevelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @Slf4j
public class LevelService {

    @Autowired
    private LevelRepository repo;

    // Obtener todos los niveles
    public List<LevelsDTO> getAllLevels() {
        return repo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

//    // Insertar un nivel
//    public LevelsDTO insert(@Valid LevelsDTO dto) {
//        try {
//            LevelEntity entity = convertToEntity(dto);
//            LevelEntity saved = repo.save(entity);
//            return convertToDTO(saved);
//        } catch (Exception e) {
//            log.error("Error al registrar el nivel: " + e.getMessage());
//            throw new ExceptionLevelDontRegister("No se pudo registrar el nivel");
//        }
//    }
//
//    // Actualizar un nivel
//    public LevelDTO update(Long id, @Valid LevelDTO dto) {
//        LevelEntity level = repo.findById(id)
//                .orElseThrow(() -> new ExceptionLevelNotFound("Nivel no encontrado con ID: " + id));
//
//        level.setLevelName(dto.getLevelName());
//
//        try {
//            LevelEntity updated = repo.save(level);
//            return convertToDTO(updated);
//        } catch (Exception e) {
//            log.error("Error al actualizar el nivel: " + e.getMessage());
//            throw new ExceptionLevelDontRegister("No se pudo actualizar el nivel");
//        }
//    }
//
//    // Eliminar un nivel
//    public boolean delete(Long id) {
//        LevelEntity level = repo.findById(id).orElse(null);
//        if (level != null) {
//            repo.deleteById(id);
//            return true;
//        }
//        return false;
//    }

    // Métodos privados de conversión
    private LevelsDTO convertToDTO(LevelEntity entity) {
        LevelsDTO dto = new LevelsDTO();
        dto.setId(entity.getLevelId());
        dto.setLevelName(entity.getLevelName());
        return dto;
    }

//    private LevelEntity convertToEntity(LevelDTO dto) {
//        LevelEntity entity = new LevelEntity();
//        entity.setLevelName(dto.getLevelName());
//        return entity;
//    }

}
