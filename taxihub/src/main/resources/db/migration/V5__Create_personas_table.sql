CREATE TABLE tbl_personas (

    -- Datos personales
    nombre VARCHAR(100) NOT NULL,
    ape_paterno VARCHAR(100) NOT NULL,
    ape_materno VARCHAR(100),
    numero_documento VARCHAR(20) PRIMARY KEY,
    genero VARCHAR(1) NOT NULL COMMENT 'M: Masculino, F: Femenino',
    fecha_nac DATE,
    direccion TEXT NULL,
    
    -- Imagen de la persona
    imagen MEDIUMBLOB,
    imagen_tipo VARCHAR(100),

    -- FK a tipo de documento y distrito
    tipo_documento_id BIGINT NOT NULL,
    distrito_id BIGINT NULL,

    -- Campos de auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) DEFAULT 'SYSTEM',
    actualizado_por VARCHAR(100) DEFAULT 'SYSTEM',
    eliminado_por VARCHAR(100) NULL,

    -- Campo activo clásico
    activo BOOLEAN DEFAULT TRUE NOT NULL,

    -- Índices básicos
    KEY idx_nombre_completo (nombre, ape_paterno, ape_materno),
    KEY idx_fecha_creacion (fecha_creacion),
    KEY idx_distrito (distrito_id),

    -- Foreign Keys
    CONSTRAINT fk_personas_tipo_documento
        FOREIGN KEY (tipo_documento_id) REFERENCES tbl_tipo_documento(id),
    CONSTRAINT fk_personas_distrito
        FOREIGN KEY (distrito_id) REFERENCES tbl_distritos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
