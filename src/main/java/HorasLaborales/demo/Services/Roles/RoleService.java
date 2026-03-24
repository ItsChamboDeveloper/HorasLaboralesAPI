package HorasLaborales.demo.Services.Roles;

import HorasLaborales.demo.Entities.Roles.RoleEntity;
import HorasLaborales.demo.Models.DTO.Roles.RoleDTO;
import HorasLaborales.demo.Repositories.Roles.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @Slf4j
public class RoleService {

    @Autowired
    private RoleRepository repo;

    public List<RoleDTO> getAllRoles() {
        List<RoleEntity> entities = repo.findAll();
        System.out.println("Roles encontradas: " + entities.size());
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RoleDTO convertToDTO(RoleEntity entity) {
        RoleDTO dto = new RoleDTO();
        dto.setRolId(entity.getRoleId());
        dto.setRolName(entity.getRoleName());
        return dto;
    }
}


