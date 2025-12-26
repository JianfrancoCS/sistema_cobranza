-- Crear tabla deudas de conductores
CREATE TABLE tbl_deudas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    empleado_id BIGINT NOT NULL,
    
    monto_deuda DECIMAL(10,2) NOT NULL COMMENT 'Monto total adeudado por el conductor',
    monto_pagado DECIMAL(10,2) DEFAULT 0.00 NOT NULL COMMENT 'Monto ya pagado por el conductor',
    
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) DEFAULT 'SYSTEM',
    actualizado_por VARCHAR(100) DEFAULT 'SYSTEM',
    eliminado_por VARCHAR(100) NULL,
    
    activo BOOLEAN DEFAULT TRUE NOT NULL COMMENT 'Indica si la deuda está activa',
    
    CONSTRAINT fk_deudas_empleado
        FOREIGN KEY (empleado_id) 
        REFERENCES tbl_empleados(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Constraints de negocio
    CONSTRAINT chk_deudas_monto_deuda_positivo 
        CHECK (monto_deuda >= 0),
    CONSTRAINT chk_deudas_monto_pagado_positivo 
        CHECK (monto_pagado >= 0),
    CONSTRAINT chk_deudas_monto_pagado_no_mayor_deuda 
        CHECK (monto_pagado <= monto_deuda),
    
    -- Índices
    KEY idx_deudas_empleado (empleado_id),
    KEY idx_deudas_fecha_creacion (fecha_creacion),
    KEY idx_deudas_activo (activo),
    KEY idx_deudas_montos (monto_deuda, monto_pagado),
    KEY idx_deudas_empleado_activo (empleado_id, activo)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Tabla de deudas generadas automáticamente por salidas de vehículos';