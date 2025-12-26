CREATE TABLE tbl_tipo_documento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Datos del tipo de documento
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,

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
    KEY idx_codigo (codigo),
    KEY idx_activo (activo),
    KEY idx_fecha_creacion (fecha_creacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar tipos de documento básicos
INSERT INTO tbl_tipo_documento (codigo, nombre, descripcion, creado_por) VALUES
('DNI', 'Documento Nacional de Identidad', 'Documento de identidad para ciudadanos peruanos', 'SYSTEM'),
('CE', 'Carnet de Extranjería', 'Documento de identidad para extranjeros residentes','SYSTEM');