CREATE TABLE tbl_cargos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,

    -- Campos de auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) DEFAULT 'SYSTEM',
    actualizado_por VARCHAR(100) DEFAULT 'SYSTEM',
    eliminado_por VARCHAR(100) NULL,

    -- Campo activo
    activo BOOLEAN DEFAULT TRUE NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar algunos cargos de ejemplo
INSERT INTO tbl_cargos (nombre, descripcion, creado_por) VALUES
('ADMINISTRADOR', 'Gestiona el sistema y usuarios', 'SYSTEM'),
('SUPERVISOR', 'Supervisa las operaciones diarias', 'SYSTEM'),
('CONDUCTOR', 'Encargado de la conducción de vehículos', 'SYSTEM');
