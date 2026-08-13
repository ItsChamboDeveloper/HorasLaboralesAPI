package HorasLaborales.demo.Services.Brands;

import HorasLaborales.demo.Entities.Brands.BrandEntity;
import HorasLaborales.demo.Models.DTO.Brands.BrandDTO;
import HorasLaborales.demo.Repositories.Brands.BrandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @Slf4j
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    // Llena el primer combobox (Marca)
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BrandDTO convertToDTO(BrandEntity entity) {
        BrandDTO dto = new BrandDTO();
        dto.setBrandId(entity.getBrandId());
        dto.setBrandName(entity.getBrandName());
        return dto;
    }

}
