package HorasLaborales.demo.Services.Entries;

import HorasLaborales.demo.Entities.Entries.EntryEntity;
import HorasLaborales.demo.Exceptions.WorkOrders.ExceptionWorkOrdernotRegistred;
import HorasLaborales.demo.Models.DTO.Entries.EntryDTO;
import HorasLaborales.demo.Repositories.Entries.EntryRepository;
import HorasLaborales.demo.Repositories.WorkOrders.WorkOrderRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j @Service @CrossOrigin("*")
public class EntryService {
    @Autowired
    private EntryRepository entryRepository;
    @Autowired
    private WorkOrderRepository workOrderRepository;

    /**
     * Obtiene todas las entradas (Entry) paginadas y las convierte a DTO.
     *
     * @param page Número de página a consultar.
     * @param size Cantidad de elementos por página.
     * @return Página de entradas en formato EntryDTO.
     */
    public Page<EntryDTO> getAllEntries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntryEntity> pageEntity = entryRepository.findAll(pageable);
        return pageEntity.map(this::ConvertToDTO);
    }

    /**
     * Crea una nueva entrada (Entry) a partir de un DTO validado.
     *
     * @param json Objeto EntryDTO con los datos de la entrada.
     * @return Objeto EntryDTO de la entrada creada.
     * @throws IllegalArgumentException si los datos de entrada son nulos.
     * @throws ExceptionWorkOrdernotRegistred si ocurre un error al registrar la entrada.
     */
    public EntryDTO createEntry(@Valid EntryDTO json) {
        if (json == null) {
            throw new IllegalArgumentException("Los datos del ingreso no pueden ser nulos");
        }
        try {
            EntryEntity objData = ConvertToEntity(json);
            EntryEntity entrieSaved = entryRepository.save(objData);
            return ConvertToDTO(entrieSaved);
        } catch (Exception e) {
            log.error("Error al registrar la entrada" + e.getMessage());
            throw new ExceptionWorkOrdernotRegistred("La entrada no pudo ser registrada");
        }
    }

    // ----------- CONVERTIR A DTO --------------- ///

    /**
     * Convierte una entidad EntryEntity a un DTO EntryDTO.
     *
     * @param entryEntity Entidad de entrada a convertir.
     * @return Objeto EntryDTO con los datos convertidos.
     */
    private EntryDTO ConvertToDTO(EntryEntity entryEntity) {
        EntryDTO dto = new EntryDTO();
        dto.setEntryId(entryEntity.getEntryId());
        dto.setEntryDate(entryEntity.getEntryDate());
        dto.setWorkOrderId(entryEntity.getWorkOrderId().getWorkOrderId());
        return dto;
    }

    // ----------- CONVERTIR A ENTITY --------------- ///

    /**
     * Convierte un objeto DTO (EntryDTO) en una entidad EntryEntity lista para persistir.
     * Asocia la orden de trabajo correspondiente y asigna los datos básicos de la entrada.
     *
     * @param json Objeto EntryDTO con los datos de la entrada.
     * @return EntryEntity con los datos preparados para guardar en la base de datos.
     * @throws ExceptionWorkOrdernotRegistred si el ID de la orden de trabajo no existe.
     */
    private EntryEntity ConvertToEntity(@Valid EntryDTO json) {
        EntryEntity entity = new EntryEntity();
        entity.setEntryId(json.getEntryId());
        entity.setEntryDate(json.getEntryDate());
        entity.setWorkOrderId(workOrderRepository.findById(json.getWorkOrderId())
                .orElseThrow(() -> new ExceptionWorkOrdernotRegistred("ID de la orden de trabajo no encontrado")));
        return entity;
    }
}
