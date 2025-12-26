CREATE TABLE tbl_empleados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_numero_documento VARCHAR(20) NOT NULL,
    cargo_id BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) DEFAULT 'SYSTEM',
    actualizado_por VARCHAR(100) DEFAULT 'SYSTEM',
    eliminado_por VARCHAR(100) NULL,

    activo BOOLEAN DEFAULT TRUE NOT NULL,

    CONSTRAINT fk_empleados_personas
        FOREIGN KEY (persona_numero_documento)
        REFERENCES tbl_personas(numero_documento)
        ON DELETE CASCADE,

    CONSTRAINT fk_empleados_cargos
        FOREIGN KEY (cargo_id)
        REFERENCES tbl_cargos(id),
        
    -- Único: una persona solo puede ser empleado activo una vez
    UNIQUE KEY uk_empleados_activo_persona (persona_numero_documento, activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
