package HorasLaborales.demo.Services.VehicleType;

import HorasLaborales.demo.Entities.VehiclesTypes.VehicleTypeEntity;
import HorasLaborales.demo.Models.DTO.VehicleType.VehicleTypeDTO;
import HorasLaborales.demo.Repositories.VehicleType.VehicleTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @Slf4j
public class VehicleTypeService {

    @Autowired
    private VehicleTypeRepository repo;


//    public List<VehicleTypeDTO> getAllRoles() {
//        List<VehicleTypeEntity> entities = repo.findAll();
//        return entities.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }

    public List<VehicleTypeDTO> getAllVehicleTypes() {
        List<VehicleTypeEntity> entities = repo.findAll();
        System.out.println("ENTIDADES ENCONTRADAS: " + entities.size());
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
//
//    public Page<VehicleTypeDTO> getAllRoles(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<VehicleTypeEntity> entities = repo.findAll(pageable);
//        return entities.map(this::convertToDTO);
//    }
//
//
//    public VehicleTypeDTO insert(@Valid VehicleTypeDTO dto) {
//        if (dto == null) {
//            throw new IllegalArgumentException("El tipo de vehículo no puede ser nulo");
//        }
//        VehicleTypeEntity entity = convertToEntity(dto);
//        VehicleTypeEntity saved = repo.save(entity);
//        return convertToDTO(saved);
//    }
//
//
//    public VehicleTypeDTO update(Long id, @Valid VehicleTypeDTO dto) {
//        if (dto == null) {
//            throw new IllegalArgumentException("El tipo de vehículo no puede ser nulo");
//        }
//
//        VehicleTypeEntity entity = repo.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Tipo de vehículo no encontrado"));
//
//        entity.setName(dto.getName());
//
//        VehicleTypeEntity updated = repo.save(entity);
//        return convertToDTO(updated);
//    }
//
//
//    public boolean delete(Long id) {
//        if (repo.existsById(id)) {
//            repo.deleteById(id);
//            return true;
//        }
//        return false;
//    }

    private VehicleTypeDTO convertToDTO(VehicleTypeEntity entity) {
        VehicleTypeDTO dto = new VehicleTypeDTO();
        dto.setTypeId(entity.getTypeId());
        dto.setTypeName(entity.getTypeName());
        return dto;
    }
//
//    private VehicleTypeEntity convertToEntity(@Valid VehicleTypeDTO dto) {
//        VehicleTypeEntity entity = new VehicleTypeEntity();
//        entity.setName(dto.getName());
//        return entity;
//    }

}
