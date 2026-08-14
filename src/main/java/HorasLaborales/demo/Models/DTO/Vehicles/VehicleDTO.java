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
    @Size(max = 8, message = "La placa no puede exceder 8 caracteres") // Validación para el tamaño máximo de la placa
    @Pattern(
            regexp = "^[A-Za-z]{1,2}[A-Za-z0-9]{3,6}$",
            message = "La placa debe tener 1 o 2 letras seguidas de 3 a 6 números o letras (Ej: P123456, P452AAA)"
    ) // Validación para el formato de la placa
    private String plateNumber; // Número de placa del vehículo


    @NotBlank(message = "La marca es obligatoria")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 3, message = "La marca debe tener al menos 16 dígitos")
    // Validación para el tamaño mínimo de la contraseña
    @Size(max = 50, message = "La marca no puede exceder 50 caracteres")
    // Validación para el tamaño máximo de la marca
    private String brand; // Marca del vehículo (texto libre, se mantiene por compatibilidad)

    @NotBlank(message = "El modelo es obligatorio")
    // Validación para que el campo no esté vacío o solo contenga espacios en blanco
    @Size(min = 3, message = "El modelo debe tener al menos 16 carácter")
    @Size(max = 50, message = "El modelo no puede exceder 50 caracteres")
    // Validación para el tamaño máximo del modelo
    private String model; // Modelo del vehículo (texto libre, se mantiene por compatibilidad)

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

    @NotBlank(message = "El correo del propietario es obligatorio")
    @Email(message = "El correo del propietario no tiene un formato válido")
    @Size(max = 150, message = "El correo del propietario no puede exceder 150 caracteres")
    // Correo en texto libre; si el dueño crea una cuenta de papá/mamá con este
    // mismo correo, sus vehículos aparecen automáticamente en su seguimiento
    private String ownerEmail;

    @NotBlank(message = "La imagen frontal del vehículo es obligatoria")
    private String vehicleImage; // Campo para almacenar la imagen frontal del vehículo (URL)

    @NotBlank(message = "La imagen lateral izquierda del vehículo es obligatoria")
    private String vehicleImageLeft;

    @NotBlank(message = "La imagen lateral derecha del vehículo es obligatoria")
    private String vehicleImageRight;

    @NotBlank(message = "La imagen trasera del vehículo es obligatoria")
    private String vehicleImageBack;

    @Positive(message = "El ID del estudiante debe ser positivo")
    // Validación para que el ID del estudiante sea positivo
    private Long studentId; // ID del estudiante asociado al vehículo

    private Long maintenanceEXPO; // ID del mantenimiento asociado al vehículo


    private Long idStatus; // Estado del vehículo

    // *** NUEVO: papá/mamá dueño de la cuenta que puede consultar este vehículo ***
    @Positive(message = "El ID del papá/mamá debe ser positivo")
    private Long parentId;

    // *** NUEVO: modelo elegido en el combobox dependiente (marca -> modelo) ***
    @Positive(message = "El ID del modelo debe ser positivo")
    private Long vehicleModelId;

    //Campo adicional
    private String typeName;  // Campo adicional para mostrar el tipó de automóvil, campo como tal no existe en tbVehicles
    private String studentName;  // Campo adicional para mostrar el nombre del estudiante, campo como tal no existe en tbVehicles
    private String studentLastName; // Campo adicional para mostrar el apellido del estudiante, campo como tal no existe en tbVehicles
    private String parentName; // Campo adicional para mostrar el nombre del papá/mamá, campo como tal no existe en tbVehicles
    private String vehicleModelName; // Campo adicional para mostrar el nombre del modelo elegido, campo como tal no existe en tbVehicles
    private String vehicleBrandName; // Campo adicional para mostrar la marca del modelo elegido, campo como tal no existe en tbVehicles


}
