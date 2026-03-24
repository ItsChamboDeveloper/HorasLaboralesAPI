package HorasLaborales.demo.Controllers.WorkOrders;

import HorasLaborales.demo.Exceptions.Modules.ExceptionModuleNotFound;
import HorasLaborales.demo.Exceptions.Vehicles.ExceptionVehicleNotFound;
import HorasLaborales.demo.Exceptions.WorkOrders.ExceptionWorkOrdernotfound;
import HorasLaborales.demo.Models.DTO.WorkOrders.WorkOrderDTO;
import HorasLaborales.demo.Models.DTO.WorkOrders.WorkOrderStatusDTO;
import HorasLaborales.demo.Services.WorkOrders.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/workOrders")
@CrossOrigin("*")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    /***
     *
     * @param page Aqui definimos una variable page de tipo entero
     * @param size Definimos una variable size de tipo entero
     * @return en la parte del FrontEnd devolvera la vista en forma de paginación
     */
    @GetMapping("/getAllWorkOrders")
    public ResponseEntity<Page<WorkOrderDTO>> getAllWorkOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        if (size <= 0 || size > 50){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 50"
            ));
            return ResponseEntity.ok(null);
        }
        Page<WorkOrderDTO> WorkOrders = workOrderService.getAllWorkOrders(page, size);
        if (WorkOrders == null){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "No hay Ordenes de trabajo registradas"
            ));
        }
        return ResponseEntity.ok(WorkOrders);
    }

    /***
     *
     * @param json json sera nuestra variable que literalmente sera una herramienta que nos permitira ocuparla como parametro
     * @return el return puede variar, ya sea devolvemos si la insercion no pudo completarse,
     * el segundo caso es que exitosamente pudo crearse
     * y finalmente como tercer caso, un error que no pudo crearse la orden de trabajo
     */
    @PostMapping("/newWorkOrder")
    public ResponseEntity<Map<String, Object>> insertWorkOrder(@Valid @RequestBody WorkOrderDTO json){
        try{
            WorkOrderDTO response = workOrderService.insert(json);
            if (response == null){
                return ResponseEntity.badRequest().body(Map.of(
                        "Error", "Inserción incorrecta",
                        "Estatus", "Inserción incorrecta",
                        "Descripción", "Verifique que los campos esten correctamente ingresados"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "Estado", "Completado",
                    "data", response
            ));
        }catch (ExceptionVehicleNotFound | ExceptionModuleNotFound e){
            // Si falta el vehículo o módulo referenciado en la petición, devolver 404 con detalle claro
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "Not Found",
                    "message", e.getMessage()
            ));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Error al registrar nueva orden de trabajo",
                            "detail", e.getMessage()
                    ));
        }
    }

    @PutMapping("/updateWorkOrder/{id}")
    public ResponseEntity<?> modifyWorkOrder(
            @PathVariable Long id,
            @Valid @RequestBody WorkOrderDTO WorkOrders,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errores.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }

        try{
            WorkOrderDTO workOrderUpdated = workOrderService.update(id, WorkOrders);
            return ResponseEntity.ok(workOrderUpdated);
        }
        catch (ExceptionWorkOrdernotfound e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteWorkOrder/{id}")
    public ResponseEntity<Map<String, Object>> DeleteWorkOrder(@PathVariable Long id) {
        try {
            // Intenta eliminar Orden de trabajo por medio del obj workOrderService
            // Si el metodo deleteModule devuelve false, no se ha encontrado ninguna orden de trabajo
            if (!workOrderService.delete(id)) {
                // Retorna un error de 404 NO HA SIDO ENCONTRADO
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        // Agrega un header personalizado
                        .header("X-Mensaje-Error", "WorkOrderNotFound")
                        // Cuerpo de la respuesta con detalles del error
                        .body(Map.of(
                                "error", "Not found",  // Tipo de error
                                "mensaje", "La orden de trabajo no ha sido encontrada",  // Mensaje descriptivo
                                "timestamp", Instant.now().toString()  // Marca de tiempo del error
                        ));
            }

            // Si la eliminación fue exitosa, entonces retorna success 200 (OK): PROCESO COMPLETADO.
            return ResponseEntity.ok().body(Map.of(
                    "status", "Proceso completado",  // Estado de la operación
                    "message", "Orden de trabajo eliminada exitosamente"  // Mensaje de éxito
            ));

        } catch (Exception e) {
            // Si ocurre cualquier error inesperado, retorna 500 (Internal Server Error)
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "Error",  // Indicador de error
                    "message", "Error al eliminar la orden de trabajo",  // Mensaje general
                    "detail", e.getMessage()  // Detalles tecnicos del error (DEBUGGIN)
            ));
        }
    }

    // Endpoint para estado 2 (En progreso)
    @GetMapping("/getWorkOrdersByStudentIdAndStatus3/{studentId}")
    public ResponseEntity<?> getWorkOrdersByStudentIdAndStatus2(@PathVariable Long studentId) {
        Map<String, Object> result = workOrderService.getWorkOrdersByStudentIdAndStatus(studentId, 3L);
        return ResponseEntity.ok(result);
    }

    // Endpoint para estado 3 (Completado)
    @GetMapping("/getWorkOrdersByStudentIdAndStatus4/{studentId}")
    public ResponseEntity<?> getWorkOrdersByStudentIdAndStatus3(@PathVariable Long studentId) {
        Map<String, Object> result = workOrderService.getWorkOrdersByStudentIdAndStatus(studentId, 4L);
        return ResponseEntity.ok(result);
    }

    // Obtener todas las órdenes de un estudiante (sin filtrar por estado)
    @GetMapping("/getWorkOrdersByStudentId/{studentId}")
    public ResponseEntity<?> getWorkOrdersByStudentId(@PathVariable Long studentId) {
        Map<String, Object> result = workOrderService.getWorkOrdersByStudentId(studentId);
        return ResponseEntity.ok(result);
    }

    // Nuevo endpoints para obtener órdenes por estado fijo 1 y 2
    @GetMapping("/getWorkOrdersByStatus1")
    public ResponseEntity<?> getWorkOrdersByStatus1() {
        Map<String, Object> result = workOrderService.getWorkOrdersByStatus1();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getWorkOrdersByStatus2")
    public ResponseEntity<?> getWorkOrdersByStatus2() {
        Map<String, Object> result = workOrderService.getWorkOrdersByStatus2();
        return ResponseEntity.ok(result);
    }

    // Nuevo endpoint para actualizar estado: PUT /api/workOrders/{workOrderId}/status
    @PutMapping("/{workOrderId}/status")
    public ResponseEntity<Map<String, Object>> updateWorkOrderStatus(
            @PathVariable Long workOrderId,
            @Valid @RequestBody WorkOrderStatusDTO body
    ) {
        try {
            WorkOrderDTO updated = workOrderService.updateWorkOrderStatus(workOrderId, body.getIdStatus());

            Map<String, Object> workOrderMap = Map.of(
                    "workOrderId", updated.getWorkOrderId(),
                    "idStatus", updated.getIdStatus()
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Estado actualizado correctamente",
                    "workOrder", workOrderMap
            ));

        } catch (ExceptionWorkOrdernotfound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error al actualizar estado",
                    "detail", e.getMessage()
            ));
        }
    }

    // Obtener órdenes por número de placa del vehículo
    @GetMapping("/getWorkOrdersByPlate/{plateNumber}")
    public ResponseEntity<?> getWorkOrdersByPlate(@PathVariable String plateNumber) {
        try {
            Map<String, Object> result = workOrderService.getWorkOrdersByPlate(plateNumber);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }


}
