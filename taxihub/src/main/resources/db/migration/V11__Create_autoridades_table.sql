-- Crear tabla autoridades para Spring Security
CREATE TABLE tbl_autoridades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_autoridad VARCHAR(50) UNIQUE NOT NULL,
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
    KEY idx_nombre_autoridad (nombre_autoridad),
    KEY idx_activo (activo),
    KEY idx_fecha_creacion (fecha_creacion)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar autoridades del sistema
INSERT INTO tbl_autoridades (nombre_autoridad, descripcion, creado_por) VALUES
-- Permisos generales
('VER_DASHBOARD', 'Ver dashboard principal', 'SYSTEM'),

-- Empleados
('VER_EMPLEADOS', 'Ver lista de empleados', 'SYSTEM'),
('VER_EMPLEADOS_PROPIO', 'Ver mis propios datos de empleado', 'SYSTEM'),
('CREAR_EMPLEADOS', 'Crear nuevos empleados', 'SYSTEM'),
('EDITAR_EMPLEADOS', 'Editar empleados existentes', 'SYSTEM'),
('ELIMINAR_EMPLEADOS', 'Eliminar empleados', 'SYSTEM'),

-- Autos
('VER_AUTOS', 'Ver lista de autos', 'SYSTEM'),
('CREAR_AUTOS', 'Crear nuevos autos', 'SYSTEM'),
('EDITAR_AUTOS', 'Editar autos existentes', 'SYSTEM'),
('ELIMINAR_AUTOS', 'Eliminar autos', 'SYSTEM'),
('ASIGNAR_AUTOS','Asignar un auto a un empledo,remover o intercmabiar','SYSTEM'),
-- Comisiones
('VER_COMISIONES', 'Ver comisiones', 'SYSTEM'),
('CREAR_COMISIONES', 'Crear comisiones', 'SYSTEM'),
('EDITAR_COMISIONES', 'Editar comisiones', 'SYSTEM'),
('ELIMINAR_COMISIONES', 'Eliminar comisiones', 'SYSTEM'),

-- Deudas
('VER_DEUDAS_TODOS', 'Ver todas las deudas', 'SYSTEM'),
('VER_DEUDAS_PROPIOS', 'Ver mis propias deudas', 'SYSTEM'),
('CREAR_DEUDAS', 'Crear deudas de forma manual', 'SYSTEM'),
-- Pagos
('VER_PAGOS_TODOS', 'Ver todos los pagos', 'SYSTEM'),
('VER_PAGOS_PROPIOS', 'Ver mis propios pagos', 'SYSTEM'),
('CREAR_PAGOS_FISICOS', 'Crear pagos físicos', 'SYSTEM'),
('CREAR_PAGOS_VIRTUALES', 'Crear pagos virtuales', 'SYSTEM'),
('ELIMINAR_PAGOS_PROPIOS', 'Eliminar mis propios pagos', 'SYSTEM'),
('GESTIONAR_PAGOS', 'Crear pagos virtuales', 'SYSTEM'),

-- Marcaciones
('CREAR_MARCACIONES', 'Crear marcaciones de entrada/salida', 'SYSTEM'),

-- Seguridad
('VER_USUARIOS', 'Ver usuarios del sistema', 'SYSTEM'),
('CREAR_USUARIOS', 'Crear nuevos usuarios', 'SYSTEM'),
('EDITAR_USUARIOS', 'Editar usuarios existentes', 'SYSTEM'),
('ELIMINAR_USUARIOS', 'Eliminar usuarios', 'SYSTEM'),

('VER_AUTORIDADES', 'Ver autoridades del sistema', 'SYSTEM'),

('VER_GRUPOS', 'Ver grupos del sistema', 'SYSTEM'),
('CREAR_GRUPOS', 'Crear grupos', 'SYSTEM'),
('EDITAR_GRUPOS', 'Editar grupos', 'SYSTEM'),
('ELIMINAR_GRUPOS', 'Eliminar grupos', 'SYSTEM'),

-- Asignaciones
('ASIGNAR_PERMISOS', 'Asignar permisos a usuarios', 'SYSTEM'),
('ASIGNAR_GRUPOS', 'Asignar grupos a usuarios', 'SYSTEM');