package HorasLaborales.demo.Models.DTO.Vehicles;

import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class VehicleDTO {

    //*** ATRIBUTOS ***\\

    @Positive
    private Long vehicleId; // Identificador único del vehículo, generado por la base de datos

    @NotBlank(message = "El número de placa es obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(max = 10, message = "La placa no puede exceder 10 caracteres") // Validación para el tamaño máximo de la placa
    @Pattern(
            regexp = "^[A-Z]{1,3}[0-9]{3,6}$",
            message = "Formato de placa inválido (ej: ABC1234)"
    ) // Validación para el formato de la placa
    private String plateNumber; // Número de placa del vehículo


    @NotBlank(message = "La marca es obligatoria")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 3, message = "La marca debe tener al menos 16 dígitos")
    // Validación para el tamaño mínimo de la contraseña
    @Size(max = 50, message = "La marca no puede exceder 50 caracteres")
    // Validación para el tamaño máximo de la marca
    private String brand; // Marca del vehículo

    @NotBlank(message = "El modelo es obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 3, message = "El modelo debe tener al menos 16 carácter")
    @Size(max = 50, message = "El modelo no puede exceder 50 caracteres")
    // Validación para el tamaño máximo del modelo
    private String model; // Modelo del vehículo

    @NotNull(message = "El tipo de vehículo es obligatorio") // Validación para que el campo no sea nulo
    private Long typeId; // Tipo de vehículo (ej: automóvil, motocicleta, etc.)

    @Size(min = 4, message = "El color debe tener al menos 8 dígitos")
    // Validación para el tamaño mínimo de la contraseña
    @Size(max = 30, message = "El color no puede exceder 30 caracteres")
    // Validación para el tamaño máximo del color
    private String color; // Color del vehículo

    @NotBlank(message = "El número de tarjeta de circulación es obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 20, message = "El número de tarjeta debe tener al menos 8 dígitos")
    // Validación para el tamaño mínimo de la contraseña
    @Size(max = 20, message = "El número de tarjeta no puede exceder 20 caracteres")
    // Validación para el tamaño máximo de la tarjeta de circulación
    private String circulationCardNumber; // Número de tarjeta de circulación del vehículo

    @NotBlank(message = "El nombre del propietario es obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 5, message = "El nombre del propietario debe tener al menos 5 caracteres")
    // Validación para el tamaño mínimo del nombre del propietario
    @Size(max = 100, message = "El nombre del propietario no puede exceder 100 caracteres")
    // Validación para el tamaño máximo del nombre del propietario
    private String ownerName; // Nombre del propietario del vehículo

    @NotBlank(message = "El DUI del propietario es obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 10, max = 10, message = "El DUI debe tener exactamente 10 caracteres")
    // Validación para que el DUI tenga exactamente 10 caracteres
    @Pattern(regexp = "^[0-9]{8}-[0-9]$", message = "El formato del DUI es inválido (ej: 12345678-9)")
    // Validación para el formato del DUI
    private String ownerDui; // DUI del propietario del vehículo

    @NotBlank(message = "El teléfono del propietario es obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 7, max = 10, message = "El teléfono debe tener entre 8 y 10 caracteres")
    // Validación para que el teléfono tenga entre 8 y 10 caracteres
    private String ownerPhone; // Teléfono del propietario del vehículo

    @NotBlank(message = "La imagen del vehículo es obligatoria")
    private String vehicleImage; // Campo para almacenar la imagen del vehículo (URL)

    @Positive(message = "El ID del estudiante debe ser positivo")
    // Validación para que el ID del estudiante sea positivo
    private Long studentId; // ID del estudiante asociado al vehículo

    private Long maintenanceEXPO; // ID del mantenimiento asociado al vehículo


    private Long idStatus; // Estado del vehículo

    //Campo adicional
    private String typeName;  // Campo adicional para mostrar el tipó de automóvil, campo como tal no existe en tbVehicles
    private String studentName;  // Campo adicional para mostrar el nombre del estudiante, campo como tal no existe en tbVehicles
    private String studentLastName; // Campo adicional para mostrar el apellido del estudiante, campo como tal no existe en tbVehicles


}
