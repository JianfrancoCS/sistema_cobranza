CREATE TABLE tbl_marcaciones(
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    empleado_id BIGINT,
    tipo_marcacion BOOLEAN NOT NULL ,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) DEFAULT 'SYSTEM',
    actualizado_por VARCHAR(100) DEFAULT 'SYSTEM',
    eliminado_por VARCHAR(100) NULL,

    CONSTRAINT fk_maraciones_empleado
        FOREIGN KEY (empleado_id)
            REFERENCES tbl_empleados (id)
            ON DELETE SET NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_marcaciones_empleado_fecha_creacion
    ON tbl_marcaciones(empleado_id, fecha_creacion);