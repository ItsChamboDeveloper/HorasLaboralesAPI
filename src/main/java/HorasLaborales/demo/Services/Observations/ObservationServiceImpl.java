package HorasLaborales.demo.Services.Observations;

import HorasLaborales.demo.Entities.Observation.ObservationEntity;
import HorasLaborales.demo.Entities.Students.StudentEntity;
import HorasLaborales.demo.Entities.WorkOrders.WorkOrderEntity;
import HorasLaborales.demo.Models.DTO.Observations.ObservationDTO;
import HorasLaborales.demo.Repositories.Observations.ObservationRepository;
import HorasLaborales.demo.Repositories.Students.StudentsRepository;
import HorasLaborales.demo.Repositories.WorkOrders.WorkOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Service
public class ObservationServiceImpl implements ObservationService{

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private StudentsRepository studentsRepository;

    @Override
    public ObservationDTO createObservation(ObservationDTO dto) throws Exception {
        if (dto == null) throw new IllegalArgumentException("Payload nulo");

        Optional<WorkOrderEntity> woOpt = workOrderRepository.findById(dto.getWorkOrderId());
        if (woOpt.isEmpty()) throw new Exception("WorkOrder no encontrada");

        Optional<StudentEntity> stOpt = studentsRepository.findById(dto.getStudentId());
        if (stOpt.isEmpty()) throw new Exception("Student no encontrado");

        ObservationEntity entity = new ObservationEntity();
        entity.setWorkOrderId(woOpt.get());
        entity.setObservacion(dto.getObservacion());
        entity.setImageUrl(dto.getImageUrl());
        entity.setStudentId(stOpt.get());

        ObservationEntity saved = observationRepository.save(entity);

        ObservationDTO out = new ObservationDTO();
        out.setObservacionId(saved.getObservacionId());
        out.setWorkOrderId(saved.getWorkOrderId().getWorkOrderId());
        out.setObservacion(saved.getObservacion());
        out.setImageUrl(saved.getImageUrl());
        out.setStudentId(saved.getStudentId().getStudentId());
        out.setStudentName(saved.getStudentId().getFirstName() + " " + saved.getStudentId().getLastName());

        return out;
    }

    @Override
    public List<ObservationDTO> getObservationsByWorkOrderId(Long workOrderId) {
        List<ObservationEntity> list = observationRepository.findByWorkOrderId_WorkOrderId(workOrderId);
        return list.stream().map(o -> {
            ObservationDTO dto = new ObservationDTO();
            dto.setObservacionId(o.getObservacionId());
            dto.setWorkOrderId(o.getWorkOrderId() != null ? o.getWorkOrderId().getWorkOrderId() : null);
            dto.setObservacion(o.getObservacion());
            dto.setImageUrl(o.getImageUrl());
            if (o.getStudentId() != null) {
                dto.setStudentId(o.getStudentId().getStudentId());
                dto.setStudentName(o.getStudentId().getFirstName() + " " + o.getStudentId().getLastName());
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
