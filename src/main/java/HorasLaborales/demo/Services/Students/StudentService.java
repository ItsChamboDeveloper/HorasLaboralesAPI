package HorasLaborales.demo.Services.Students;

import HorasLaborales.demo.Config.Crypto.Argon2Password;
import HorasLaborales.demo.Entities.Grades.GradeEntity;
import HorasLaborales.demo.Entities.Roles.RoleEntity;
import HorasLaborales.demo.Entities.Students.StudentEntity;
import HorasLaborales.demo.Exceptions.Grades.ExceptionGradeNotFound;
import HorasLaborales.demo.Exceptions.Roles.ExceptionRoleNotFound;
import HorasLaborales.demo.Exceptions.Students.ExceptionStudentDuplicated;
import HorasLaborales.demo.Exceptions.Students.ExceptionStudentNotFound;
import HorasLaborales.demo.Models.DTO.Students.StudentDTO;
import HorasLaborales.demo.Repositories.Grades.GradeRepository;
import HorasLaborales.demo.Repositories.Roles.RoleRepository;
import HorasLaborales.demo.Repositories.Students.StudentsRepository;
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
public class StudentService {

    @Autowired
    private GradeRepository gradeRepository; // Repositorio que accede a los años académicos de los estudiantes
    @Autowired
    private StudentsRepository studentsRepository; // Repositorio que accede a la base de datos
    @Autowired
    private Argon2Password argon2; // Servicio de encriptación de contraseñas
    @Autowired
    private RoleRepository roleRepository; // Inyectado para validar/obtener roles

    //*** MÉTODO PARA OBTENER TODOS LOS ESTUDIANTES ***\\

    /**
     * Obtiene todos los estudiantes paginados y los convierte a DTO.
     *
     * @param page Número de página a consultar.
     * @param size Tamaño de la página (cantidad de elementos por página).
     * @return Página de usuarios en formato DTO.
     */
    public Page<StudentDTO> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentEntity> studentEntityPage = studentsRepository.findAll(pageable);

        return studentEntityPage.map(this::ConvertToDTO);
    }

    //*** MÉTODO PARA OBTENER UN ESTUDIANTE POR SU ID ***\\
    /**
     * Obtiene un estudiante por su ID y lo convierte a DTO.
     *
     * @param id ID del estudiante a buscar.
     * @return Objeto StudentDTO si se encuentra, null si no existe.
     */
    public StudentDTO getStudentById(Long id) {
        Optional<StudentEntity> studentOptional = studentsRepository.findById(id);
        return studentOptional
                .map(this::ConvertToDTO)
                .orElse(null);
    }

    //*** MÉTODO PARA OBTENER UN ESTUDIANTE POR SU CÓDIGO ***\\
    /**
     * Obtiene un estudiante por su código y lo convierte a DTO.
     * @param studentCard Código del estudiante a buscar.
     * @return Objeto StudentDTO si se encuentra, null si no existe.
     */
    public StudentDTO getStudentByStudentCard(String studentCard) {
        Optional<StudentEntity> studentOptional = studentsRepository.findByStudentCard(studentCard);
        return studentOptional
                .map(this::ConvertToDTO)
                .orElse(null);
    }

    //*** MÉTODO PARA CREAR UN NUEVO ESTUDIANTE ***\\

    /**
     * Crea un nuevo usuario a partir de un DTO validado.
     *
     * @param json Objeto StudentDTO con los datos del estudiante.
     * @return Objeto StudentDTO del estudiante creado.
     * @throws ExceptionStudentDuplicated si el correo o el código ya existen.
     */
    public StudentDTO createStudent(@Valid StudentDTO json) {

        if (studentsRepository.existsByEmail(json.getEmail()) &&
                studentsRepository.existsByStudentCard(json.getStudentCard())) {
            throw new ExceptionStudentDuplicated("El correo del estudiante y el código ya están registrados en la base de datos");
        }

        if (studentsRepository.existsByEmail(json.getEmail())) {
            throw new ExceptionStudentDuplicated("El correo del estudiante ya está registrado en la base de datos");
        }
        if (studentsRepository.existsByStudentCard(json.getStudentCard())) {
            throw new ExceptionStudentDuplicated("El código del estudiante ya está registrado en la base de datos");
        }

        StudentEntity objEntity = ConvertToEntity(json);
        StudentEntity saveStudent = studentsRepository.save(objEntity);

        return ConvertToDTO(saveStudent);
    }

    //*** MÉTODO PARA ACTUALIZAR UN ESTUDIANTE EXISTENTE ***\\

    /**
     * Actualiza los datos de un estudiante existente.
     *
     * @param id   ID del estudiante a actualizar.
     * @param json Objeto StudentDTO con los nuevos datos.
     * @return Objeto StudentDTO actualizado.
     * @throws ExceptionStudentNotFound   si el estudiante no existe.
     * @throws ExceptionStudentDuplicated si el correo o el código ya están registrados.
     * @throws ExceptionRoleNotFound      si el año académico proporcionado no existe.
     */
    public StudentDTO updateStudent(@Valid Long id, StudentDTO json) {
        // Se verifica la existencia
        StudentEntity exist = studentsRepository.findById(id).orElseThrow(() ->
                new ExceptionStudentNotFound("Estudiante no encontrado"));

        // Validar correo si cambia
        if (!exist.getEmail().equals(json.getEmail()) && !exist.getStudentCard().equals(json.getStudentCard()) &&
                studentsRepository.existsByEmail(json.getEmail())
                && studentsRepository.existsByStudentCard(json.getStudentCard())) {
            throw new ExceptionStudentDuplicated("El correo del estudiante y el código ya están registrados en la base de datos");
        }

        // Validar correo si cambia
        if (!exist.getEmail().equals(json.getEmail()) &&
                studentsRepository.existsByEmail(json.getEmail())) {
            throw new ExceptionStudentDuplicated("El correo del estudiante ya está registrado en la base de datos");
        }

        // Validar código si cambia
        if (!exist.getStudentCard().equals(json.getStudentCard()) &&
                studentsRepository.existsByStudentCard(json.getStudentCard())) {
            throw new ExceptionStudentDuplicated("El código del estudiante ya está registrado en la base de datos");
        }

        // Actualizar valores
        exist.setStudentCard(json.getStudentCard());
        exist.setFirstName(json.getFirstName());
        exist.setLastName(json.getLastName());
        exist.setEmail(json.getEmail());

        if (json.getPassword() != null && !json.getPassword().isEmpty()) {
            exist.setPassword(argon2.EncryptPassword(json.getPassword()));
        }

        if (json.getGradeId() != null) {
            GradeEntity gradeEntity = gradeRepository.findById(json.getGradeId())
                    .orElseThrow(() -> new ExceptionGradeNotFound("ID del año académico del estudiante no encontrado"));
            exist.setGradeId(gradeEntity);
        }

        // Manejo del roleId: si se proporciona, validar existencia y asignar
        if (json.getRoleId() != null) {
            RoleEntity roleEntity = roleRepository.findById(json.getRoleId())
                    .orElseThrow(() -> new ExceptionRoleNotFound("ID del rol del estudiante no encontrado"));
            exist.setRoleId(roleEntity);
        }

        StudentEntity studentUpdated = studentsRepository.save(exist);
        return ConvertToDTO(studentUpdated);
    }

    //*** MÉTODO PARA ELIMINAR UN USUARIO ***\\

    /**
     * Elimina un usuario si existe en la base de datos.
     *
     * @param id ID del usuario a eliminar.
     * @return true si se eliminó exitosamente, false si no se encontró.
     */
    public boolean deleteStudent(Long id) {
        StudentEntity exist = studentsRepository.findById(id).orElse(null);
        // Verifica si el usuario existe antes de eliminar
        if (exist != null) {
            studentsRepository.deleteById(id);
            return true;
        } else {
            log.error("Estudiante no encontrado");
            return false;
        }
    }

    //*** MÉTODO PARA RESETEAR LA CONTRASEÑA DE UN USUARIO ***\\

    /**
     *
     * @param id ID del usuario cuya contraseña se va a resetear.
     * @return true si la contraseña se reseteó exitosamente, false si el usuario no fue encontrado. False si el usuario no fue encontrado.
     * @throws ExceptionStudentNotFound si el usuario no existe.
     */
    public boolean resetStudentPassword(@Valid Long id) {
        StudentEntity existing = studentsRepository.findById(id).orElseThrow(() -> new ExceptionStudentNotFound("Estudiante no encontrado"));
        if (existing != null) {
            String newPassword = PasswordGenerator.generateSecurePassword(12);
            existing.setPassword(argon2.EncryptPassword(newPassword));
            StudentEntity studentUpdated = studentsRepository.save(existing);
            return true;
        }
        return false;
    }

    public boolean changeStudentPassword(Long id, String oldPassword, String newPassword) {
        StudentEntity student = studentsRepository.findById(id).orElse(null);
        if (student == null) return false;

        if (!argon2.VerifyPassword(student.getPassword(), oldPassword)) {
            return false;
        }

        student.setPassword(argon2.EncryptPassword(newPassword));
        studentsRepository.save(student);
        return true;
    }

//*** MÉTODOS COMPLEMENTARIOS***\\


    /**
     * Verifica si un estudiante ya existe en la base de datos por su correo institucional.
     *
     * @param email Correo institucional del estudiante.
     * @return true si el correo ya existe, false si no.
     */
    public boolean verifyStudentExist(String email, String studentCard) {
        return studentsRepository.existsByEmail(email) || studentsRepository.existsByStudentCard(studentCard);
    }

    /**
     * Convierte una entidad de usuario (StudentEntity) a un DTO (StudentDTO).
     *
     * @param studentEntity Entidad del usuario a convertir.
     * @return Objeto StudentDTO con los datos convertidos.
     */
    private StudentDTO ConvertToDTO(StudentEntity studentEntity) {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId(studentEntity.getStudentId());
        dto.setStudentCard(studentEntity.getStudentCard());
        dto.setFirstName(studentEntity.getFirstName());
        dto.setLastName(studentEntity.getLastName());
        dto.setEmail(studentEntity.getEmail());
        dto.setPassword(studentEntity.getPassword());

        // Asigna el nombre y ID del año académico si el estudiante tiene uno asociado
        if (studentEntity.getGradeId() != null) {
            dto.setGradeGroup(studentEntity.getGradeId().getGradeGroup());
            dto.setGradeId(studentEntity.getGradeId().getGradeId());
        }

        // Asignar roleId si existe
        if (studentEntity.getRoleId() != null) {
            dto.setRoleId(studentEntity.getRoleId().getRoleId());
        }

        return dto;
    }

    /**
     * Convierte un objeto DTO (StudentDTO) en una entidad de usuario (StudentEntity).
     *
     * @param json Objeto StudentDTO con los datos del usuario.
     * @return Objeto StudentEntity con los datos listos para guardar en la base de datos.
     * @throws ExceptionRoleNotFound si el ID del rol no existe en la base.
     */
    private StudentEntity ConvertToEntity(@Valid StudentDTO json) {
        Argon2Password objHash = new Argon2Password();
        StudentEntity entity = new StudentEntity();
        entity.setStudentCard(json.getStudentCard());
        entity.setFirstName(json.getFirstName());
        entity.setLastName(json.getLastName());
        entity.setEmail(json.getEmail());
        entity.setPassword(argon2.EncryptPassword(json.getPassword()));

        // Asigna el año académico si se proporciona un ID de año académico
        if (json.getGradeId() != null) {
            GradeEntity gradeEntity = gradeRepository.findById(json.getGradeId())
                    .orElseThrow(() -> new ExceptionGradeNotFound("ID del año académico del estudiante no encontrado"));
            entity.setGradeId(gradeEntity);
        }

        // Asignar role si se proporcionó roleId (valida existencia)
        if (json.getRoleId() != null) {
            RoleEntity roleEntity = roleRepository.findById(json.getRoleId())
                    .orElseThrow(() -> new ExceptionRoleNotFound("ID del rol del estudiante no encontrado"));
            entity.setRoleId(roleEntity);
        }

        return entity;
    }


}
