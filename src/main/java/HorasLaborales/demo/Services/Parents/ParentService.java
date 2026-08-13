package HorasLaborales.demo.Services.Parents;

import HorasLaborales.demo.Config.Crypto.Argon2Password;
import HorasLaborales.demo.Entities.Parents.ParentEntity;
import HorasLaborales.demo.Exceptions.Parents.ExceptionParentDuplicated;
import HorasLaborales.demo.Exceptions.Parents.ExceptionParentNotFound;
import HorasLaborales.demo.Models.DTO.Parents.ParentDTO;
import HorasLaborales.demo.Repositories.Parents.ParentRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service @Slf4j
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private Argon2Password argon2; // Servicio de encriptación de contraseñas

    //*** MÉTODO PARA OBTENER TODOS LOS PAPÁS/MAMÁS ***\\
    public Page<ParentDTO> getAllParents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return parentRepository.findAll(pageable).map(this::convertToDTO);
    }

    //*** MÉTODO PARA OBTENER UN PAPÁ/MAMÁ POR SU ID ***\\
    public ParentDTO getParentById(Long id) {
        Optional<ParentEntity> parentOptional = parentRepository.findById(id);
        return parentOptional.map(this::convertToDTO).orElse(null);
    }

    //*** MÉTODO PARA CREAR (REGISTRAR) UN NUEVO PAPÁ/MAMÁ ***\\
    // Normalmente lo usa Coordinación/Animador para dar de alta la cuenta.
    public ParentDTO createParent(@Valid ParentDTO json) {
        if (parentRepository.existsByEmail(json.getEmail())) {
            throw new ExceptionParentDuplicated("El correo ya está registrado en el sistema");
        }
        if (parentRepository.existsByDui(json.getDui())) {
            throw new ExceptionParentDuplicated("El DUI ya está registrado en el sistema");
        }

        ParentEntity entity = convertToEntity(json);
        ParentEntity saved = parentRepository.save(entity);
        return convertToDTO(saved);
    }

    //*** MÉTODO PARA ACTUALIZAR UN PAPÁ/MAMÁ EXISTENTE ***\\
    public ParentDTO updateParent(Long id, @Valid ParentDTO json) {
        ParentEntity exist = parentRepository.findById(id)
                .orElseThrow(() -> new ExceptionParentNotFound("Papá/mamá no encontrado"));

        if (!exist.getEmail().equals(json.getEmail()) && parentRepository.existsByEmail(json.getEmail())) {
            throw new ExceptionParentDuplicated("El correo ya está registrado en el sistema");
        }
        if (!exist.getDui().equals(json.getDui()) && parentRepository.existsByDui(json.getDui())) {
            throw new ExceptionParentDuplicated("El DUI ya está registrado en el sistema");
        }

        exist.setFirstName(json.getFirstName());
        exist.setLastName(json.getLastName());
        exist.setEmail(json.getEmail());
        exist.setDui(json.getDui());

        // Solo cambia el password si mandaron uno nuevo
        if (json.getPassword() != null && !json.getPassword().isBlank()) {
            exist.setPassword(argon2.EncryptPassword(json.getPassword()));
        }

        ParentEntity updated = parentRepository.save(exist);
        return convertToDTO(updated);
    }

    //*** MÉTODO PARA ELIMINAR UN PAPÁ/MAMÁ ***\\
    public boolean deleteParent(Long id) {
        ParentEntity exist = parentRepository.findById(id).orElse(null);
        if (exist != null) {
            parentRepository.deleteById(id);
            return true;
        }
        log.error("Papá/mamá no encontrado");
        return false;
    }

    //*** MÉTODOS COMPLEMENTARIOS ***\\

    public boolean verifyParentExist(String email, String dui) {
        return parentRepository.existsByEmail(email) || parentRepository.existsByDui(dui);
    }

    private ParentDTO convertToDTO(ParentEntity entity) {
        ParentDTO dto = new ParentDTO();
        dto.setParentId(entity.getParentId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setDui(entity.getDui());
        // A propósito NO se regresa el hash del password en las respuestas del API,
        // aunque otros DTOs del proyecto (StudentDTO, InstructorDTO) sí lo hacen.
        return dto;
    }

    private ParentEntity convertToEntity(@Valid ParentDTO json) {
        ParentEntity entity = new ParentEntity();
        entity.setFirstName(json.getFirstName());
        entity.setLastName(json.getLastName());
        entity.setEmail(json.getEmail());
        entity.setDui(json.getDui());
        entity.setPassword(argon2.EncryptPassword(json.getPassword()));
        return entity;
    }

}
