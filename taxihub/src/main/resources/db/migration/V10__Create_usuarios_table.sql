CREATE TABLE tbl_usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(100)  NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    contrasena_temporal BOOLEAN DEFAULT FALSE NOT NULL,

    empleado_id BIGINT NULL,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) NULL,
    actualizado_por VARCHAR(100) NULL,
    eliminado_por VARCHAR(100) NULL,

    activo BOOLEAN DEFAULT TRUE NOT NULL,

    KEY idx_correo (correo),
    KEY idx_activo (activo),
    KEY idx_empleado_id (empleado_id),
    KEY idx_fecha_creacion (fecha_creacion),

    CONSTRAINT fk_usuarios_empleado
        FOREIGN KEY (empleado_id)
        REFERENCES tbl_empleados(id)
        ON DELETE SET NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;