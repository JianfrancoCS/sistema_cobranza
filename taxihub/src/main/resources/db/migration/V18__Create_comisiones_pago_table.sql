CREATE TABLE tbl_comisiones_pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(100) UNIQUE NOT NULL COMMENT 'Código único de la comisión',
    nombre VARCHAR(255) NOT NULL COMMENT 'Nombre descriptivo de la comisión',
    monto DECIMAL(10,2) NOT NULL COMMENT 'Monto de la comisión en soles',
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) DEFAULT 'SYSTEM',
    actualizado_por VARCHAR(100) DEFAULT 'SYSTEM',
    eliminado_por VARCHAR(100) NULL,
    
    activo BOOLEAN DEFAULT TRUE NOT NULL COMMENT 'Indica si la comisión está activa',
    
    CONSTRAINT chk_comisiones_monto_positivo
        CHECK (monto >= 0),
    
    KEY idx_comisiones_codigo (codigo),
    KEY idx_comisiones_activo (activo),
    KEY idx_comisiones_fecha_creacion (fecha_creacion),
    KEY idx_comisiones_activo_codigo (activo, codigo)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Tabla de configuración de comisiones por salidas de vehículos';

INSERT INTO tbl_comisiones_pago (codigo, nombre, monto, creado_por) VALUES
('comisionSalidaVehiculoEmpleado', 'Comisión por salida con vehiculo propio',  1.50, 'SYSTEM'),
('comisionSalidaVehiculoEmpresa', 'Comisión por salida con vehiculo de empresa',  3.40, 'SYSTEM');