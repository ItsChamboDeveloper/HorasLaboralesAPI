package HorasLaborales.demo.Services.VehicleModels;

import HorasLaborales.demo.Entities.VehicleModels.VehicleModelEntity;
import HorasLaborales.demo.Models.DTO.VehicleModels.VehicleModelDTO;
import HorasLaborales.demo.Repositories.VehicleModels.VehicleModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @Slf4j
public class VehicleModelService {

    @Autowired
    private VehicleModelRepository vehicleModelRepository;

    // Por si en algún listado general hace falta ver todos los modelos
    public List<VehicleModelDTO> getAllModels() {
        return vehicleModelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Esto es lo que usa el combobox dependiente: modelos de UNA marca
    public List<VehicleModelDTO> getModelsByBrandId(Long brandId) {
        return vehicleModelRepository.findByBrandId_BrandId(brandId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private VehicleModelDTO convertToDTO(VehicleModelEntity entity) {
        VehicleModelDTO dto = new VehicleModelDTO();
        dto.setModelId(entity.getModelId());
        dto.setModelName(entity.getModelName());
        if (entity.getBrandId() != null) {
            dto.setBrandId(entity.getBrandId().getBrandId());
            dto.setBrandName(entity.getBrandId().getBrandName());
        }
        return dto;
    }

}
