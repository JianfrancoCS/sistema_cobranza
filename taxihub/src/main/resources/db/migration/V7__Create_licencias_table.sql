-- Crear tabla conductores
CREATE TABLE tbl_licencias (
    numero_licencia VARCHAR(20) NOT NULL PRIMARY KEY ,
    numero_documento VARCHAR(20),
    -- Datos específicos del conductor
    categoria_licencia VARCHAR(10) NOT NULL,
    fecha_vencimiento_licencia DATE NOT NULL,

    -- Campos de auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) DEFAULT 'SYSTEM',
    actualizado_por VARCHAR(100) DEFAULT 'SYSTEM',
    eliminado_por VARCHAR(100) NULL,

    -- Campo activo
    activo BOOLEAN DEFAULT TRUE NOT NULL,

    -- Índices
    KEY idx_numero_licencia (numero_licencia),
    KEY idx_fecha_vencimiento (fecha_vencimiento_licencia),

    -- Relación 1 a 1 con Personas
    CONSTRAINT fk_conductores_personas
        FOREIGN KEY (numero_documento)
        REFERENCES tbl_personas(numero_documento)
        ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
