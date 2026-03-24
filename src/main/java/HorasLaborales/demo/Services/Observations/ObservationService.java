package HorasLaborales.demo.Services.Observations;

import HorasLaborales.demo.Models.DTO.Observations.ObservationDTO;

import java.util.List;

public interface ObservationService {
    ObservationDTO createObservation(ObservationDTO dto) throws Exception;
    List<ObservationDTO> getObservationsByWorkOrderId(Long workOrderId);
}
