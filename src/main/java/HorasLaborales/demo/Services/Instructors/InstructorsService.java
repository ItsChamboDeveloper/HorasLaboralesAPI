package HorasLaborales.demo.Services.Instructors;

import HorasLaborales.demo.Config.Crypto.Argon2Password;
import HorasLaborales.demo.Entities.Instructors.InstructorEntity;
import HorasLaborales.demo.Entities.Levels.LevelEntity;
import HorasLaborales.demo.Entities.Roles.RoleEntity;
import HorasLaborales.demo.Exceptions.Instructors.ExceptionEmailInstructorDuplicated;
import HorasLaborales.demo.Exceptions.Instructors.ExceptionInstructorNotFound;
import HorasLaborales.demo.Exceptions.Levels.ExceptionLevelNotFound;
import HorasLaborales.demo.Exceptions.Roles.ExceptionRoleNotFound;
import HorasLaborales.demo.Models.DTO.Instructors.InstructorDTO;
import HorasLaborales.demo.Repositories.Instructors.InstructorRepository;
import HorasLaborales.demo.Repositories.Levels.LevelRepository;
import HorasLaborales.demo.Repositories.Roles.RoleRepository;
import HorasLaborales.demo.Utils.PasswordGenerator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j @Service
public class InstructorsService {

    @Autowired
    private LevelRepository levelRepository; // Repositorio que accede a los años académicos de los instructores
    @Autowired
    private RoleRepository roleRepository; // Repositorio que accede a los roles
    @Autowired
    private InstructorRepository instructorRepository; // Repositorio que accede a la base de datos de instructores
    @Autowired
    private Argon2Password argon2; // Servicio de encriptación de contraseñas

    //*** MÉTODO PARA OBTENER TODOS LOS INSTRUCTORES ***\\

    /**
     * Obtiene todos los instructores paginados y los convierte a DTO.
     *
     * @param page Número de página a consultar.
     * @param size Tamaño de la página (cantidad de elementos por página).
     * @return Página de instructores en formato DTO.
     */
    public Page<InstructorDTO> getAllInstructors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InstructorEntity> instructorEntityPage = instructorRepository.findAll(pageable);

        return instructorEntityPage.map(this::convertToDTO);
    }

    //*** MÉTODO PARA OBTENER UN INSTRUCTOR POR ID O CORREO ***\\
    /**
     * Obtiene un instructor por su ID y lo convierte a DTO.
     *
     * @param id ID del instructor a buscar.
     * @return Objeto InstructorDTO si se encuentra, null si no existe.
     */
    public InstructorDTO getInstructorById(Long id) {
        Optional<InstructorEntity> instructorOptional = instructorRepository.findById(id);
        return instructorOptional.map(this::convertToDTO).orElse(null);
    }

    /**
     * Obtiene un instructor por su correo electrónico y lo convierte a DTO.
     *
     * @param email Correo electrónico del instructor a buscar.
     * @return Objeto InstructorDTO si se encuentra, null si no existe.
     */
    public InstructorDTO getInstructorByEmail(String email) {
        Optional<InstructorEntity> instructorOptional = instructorRepository.findByEmail(email);
        return instructorOptional.map(this::convertToDTO).orElse(null);
    }

    //*** MÉTODO PARA CREAR UN NUEVO INSTRUCTOR ***\\

    /**
     * Crea un nuevo instructor a partir de un DTO validado.
     *
     * @param json Objeto InstructorDTO con los datos del instructor.
     * @return Objeto InstructorDTO del instructor creado.
     * @throws ExceptionEmailInstructorDuplicated si el correo ya existe.
     */
    public InstructorDTO createInstructor(@Valid InstructorDTO json) {
        if (verifyInstructorExist(json.getEmail())) {
            throw new ExceptionEmailInstructorDuplicated("El correo del instructor ya está registrado en la base de datos");
        }
        InstructorEntity objEntity = convertToEntity(json);
        InstructorEntity saveInstructor = instructorRepository.save(objEntity);

        return convertToDTO(saveInstructor);
    }

    //*** MÉTODO PARA ACTUALIZAR UN INSTRUCTOR EXISTENTE ***\\

    /**
     * Actualiza los datos de un instructor existente.
     *
     * @param id   ID del instructor a actualizar.
     * @param json Objeto InstructorDTO con los nuevos datos.
     * @return Objeto InstructorDTO actualizado.
     * @throws ExceptionInstructorNotFound       si el instructor no existe.
     * @throws ExceptionEmailInstructorDuplicated si el nuevo correo ya está registrado.
     * @throws ExceptionRoleNotFound             si el año académico proporcionado no existe.
     */
    public InstructorDTO updateInstructor(@Valid Long id, InstructorDTO json) {

        //Se verifica la existencia
        InstructorEntity instructorExist = instructorRepository.findById(id).orElseThrow(() ->
                new ExceptionInstructorNotFound("Instructor no encontrado"));
        if (!instructorExist.getEmail().equals(json.getEmail())) {
            if (verifyInstructorExist(json.getEmail())) {
                throw new ExceptionEmailInstructorDuplicated("El correo del instructor que se quiere registrar ya existe en la base de datos");
            }
        }
        //Actualizar valores
        instructorExist.setFirstName(json.getFirstName());
        instructorExist.setLastName(json.getLastName());
        instructorExist.setEmail(json.getEmail());

        // Solo actualiza la contraseña si se proporciona una nueva
        if (json.getPassword() != null && !json.getPassword().isEmpty()) {
            instructorExist.setPassword(argon2.EncryptPassword(json.getPassword()));
        }

        // Actualizar el año académico si se proporciona un nuevo ID de nivel
        if (json.getLevelId() != null) {
            LevelEntity levelEntity = levelRepository.findById(json.getLevelId())
                    .orElseThrow(() -> new ExceptionLevelNotFound("ID del año académico del instructor no encontrado"));
            instructorExist.setLevelId(levelEntity);
        }

        // Actualizar el rol si se proporciona un nuevo ID de rol
        if (json.getRoleId() != null) {
            RoleEntity roleEntity = roleRepository.findById(json.getRoleId())
                    .orElseThrow(() -> new ExceptionRoleNotFound("ID del rol del instructor no encontrado"));
            instructorExist.setRoleId(roleEntity);
        }

        instructorExist.setInstructorImage(json.getInstructorImage());
        InstructorEntity instructorUpdated = instructorRepository.save(instructorExist);
        return convertToDTO(instructorUpdated);
    }

    //*** MÉTODO PARA ELIMINAR UN INSTRUCTOR ***\\

    /**
     * Elimina un instructor si existe en la base de datos.
     *
     * @param id ID del instructor a eliminar.
     * @return true si se eliminó exitosamente, false si no se encontró.
     */
    public boolean deleteInstructor(Long id) {
        InstructorEntity exist = instructorRepository.findById(id).orElse(null);
        // Verifica si el instructor existe antes de eliminar
        if (exist != null) {
            instructorRepository.deleteById(id);
            return true;
        } else {
            log.error("Instructor no encontrado");
            return false;
        }
    }

    //*** MÉTODO PARA RESETEAR LA CONTRASEÑA DE UN INSTRUCTOR ***\\

    /**
     *
     * @param id ID del instructor cuya contraseña se va a resetear.
     * @return true si la contraseña se reseteó exitosamente, false si el instructor no fue encontrado.
     * @throws ExceptionInstructorNotFound si el instructor no existe.
     */
    public boolean resetInstructorPassword(@Valid Long id) {
        InstructorEntity instructorExisting = instructorRepository.findById(id).orElseThrow(() -> new ExceptionInstructorNotFound("Instructor no encontrado"));
        if (instructorExisting != null) {
            String newPassword = PasswordGenerator.generateSecurePassword(12);
            instructorExisting.setPassword(argon2.EncryptPassword(newPassword));
            InstructorEntity instructorUpdated = instructorRepository.save(instructorExisting);
            return true;
        }
        return false;
    }

    //*** MÉTODOS COMPLEMENTARIOS***\\

    /**
     * Verifica si un instructor ya existe en la base de datos por su correo institucional.
     *
     * @param email Correo institucional del instructor.
     * @return true si el correo ya existe, false si no.
     */
    public boolean verifyInstructorExist(String email) {

        return instructorRepository.existsByEmail(email);
    }

    /**
     * Convierte una entidad de instructor (InstructorEntity) a un DTO (InstructorDTO).
     *
     * @param instructorEntity Entidad del instructor a convertir.
     * @return Objeto InstructorDTO con los datos convertidos.
     */
    private InstructorDTO convertToDTO(InstructorEntity instructorEntity) {
        InstructorDTO dto = new InstructorDTO();
        dto.setInstructorId(instructorEntity.getInstructorId());
        dto.setFirstName(instructorEntity.getFirstName());
        dto.setLastName(instructorEntity.getLastName());
        dto.setEmail(instructorEntity.getEmail());
        dto.setPassword(instructorEntity.getPassword());

        // Asigna el nombre y ID del año académico si el instructor tiene uno asociado
        if (instructorEntity.getLevelId() != null) {
            dto.setLevelName(instructorEntity.getLevelId().getLevelName());
            dto.setLevelId(instructorEntity.getLevelId().getLevelId());
        }

        // Asigna el nombre y ID del rol si el instructor tiene uno asociado
        if (instructorEntity.getRoleId() != null) {
            dto.setRoleName(instructorEntity.getRoleId().getRoleName());
            dto.setRoleId(instructorEntity.getRoleId().getRoleId());
        }

        dto.setInstructorImage(instructorEntity.getInstructorImage());

        return dto;
    }

    /**
     * Convierte un objeto DTO (InstructorDTO) en una entidad de instructor (InstructorEntity).
     *
     * @param json Objeto InstructorDTO con los datos del instructor.
     * @return Objeto InstructorEntity con los datos listos para guardar en la base de datos.
     * @throws ExceptionRoleNotFound si el ID del rol no existe en la base.
     */
    private InstructorEntity convertToEntity(@Valid InstructorDTO json) {
        Argon2Password objHash = new Argon2Password();
        InstructorEntity entity = new InstructorEntity();
        entity.setFirstName(json.getFirstName());
        entity.setLastName(json.getLastName());
        entity.setEmail(json.getEmail());
        entity.setPassword(argon2.EncryptPassword(json.getPassword()));

        // Asigna el año académico si se proporciona un ID de año académico
        if (json.getLevelId() != null) {
            LevelEntity levelEntity = levelRepository.findById(json.getLevelId())
                    .orElseThrow(() -> new ExceptionLevelNotFound("ID del año académico del instructor no encontrado"));
            entity.setLevelId(levelEntity);
        }

        // Asigna el rol si se proporciona un ID de rol
        if (json.getRoleId() != null) {
            RoleEntity roleEntity = roleRepository.findById(json.getRoleId())
                    .orElseThrow(() -> new ExceptionRoleNotFound("ID del rol del instructor no encontrado"));
            entity.setRoleId(roleEntity);
        }

        entity.setInstructorImage(json.getInstructorImage());

        return entity;
    }

}
