-- Crear tabla grupos para Spring Security
CREATE TABLE tbl_grupos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_grupo VARCHAR(50)  NOT NULL,
    descripcion VARCHAR(255),

    -- Campos de auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) NULL,
    actualizado_por VARCHAR(100) NULL,
    eliminado_por VARCHAR(100) NULL,

    -- Campo activo
    activo BOOLEAN DEFAULT TRUE NOT NULL,

    -- Índices
    KEY idx_nombre_grupo (nombre_grupo),
    KEY idx_activo (activo),
    KEY idx_fecha_creacion (fecha_creacion)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO tbl_grupos (nombre_grupo, descripcion) VALUES
('ADMINISTRADOR', 'Grupo de administradores del sistema'),
('SUPERVISOR', 'Grupo de supervisores con permisos de gestión'),
('CONDUCTOR', 'Grupo de conductores del sistema');
