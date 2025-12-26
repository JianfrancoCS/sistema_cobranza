-- Crear tabla departamentos
CREATE TABLE tbl_departamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,

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
    UNIQUE KEY UQ_departamentos_nombre_activo (nombre, activo),
    KEY IX_departamentos_fecha_creacion (fecha_creacion),
    KEY IX_departamentos_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO tbl_departamentos (nombre) VALUES
('AMAZONAS'),
('ANCASH'),
('APURIMAC'),
('AREQUIPA'),
('AYACUCHO'),
('CAJAMARCA'),
('CALLAO'),
('CUSCO'),
('HUANCAVELICA'),
('HUANUCO'),
('ICA'),
('JUNIN'),
('LA LIBERTAD'),
('LAMBAYEQUE'),
('LIMA'),
('LORETO'),
('MADRE DE DIOS'),
('MOQUEGUA'),
('PASCO'),
('PIURA'),
('PUNO'),
('SAN MARTIN'),
('TACNA'),
('TUMBES'),
('UCAYALI');

