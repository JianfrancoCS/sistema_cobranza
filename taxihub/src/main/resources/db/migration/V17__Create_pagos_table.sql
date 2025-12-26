CREATE TABLE tbl_pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deuda_id BIGINT NOT NULL,
    modalidad_pago VARCHAR(50) NOT NULL COMMENT 'Modalidad de pago: virtual (Yape, Plin) o efectivo',
    monto_pago DECIMAL(10,2) NOT NULL COMMENT 'Monto pagado por el conductor',
    observacion TEXT NULL COMMENT 'Observaciones adicionales del pago',
    imagen MEDIUMBLOB NULL COMMENT 'Foto del comprobante de pago (para pagos virtuales)',
    imagen_tipo VARCHAR(100) NULL COMMENT 'Tipo MIME de la imagen del comprobante',
    estado  VARCHAR(50) NOT NULL COMMENT 'Estado del pago en el proceso de aprobación',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    fecha_eliminacion TIMESTAMP NULL,
    creado_por VARCHAR(100) DEFAULT 'SYSTEM',
    actualizado_por VARCHAR(100) DEFAULT 'SYSTEM',
    eliminado_por VARCHAR(100) NULL,
    activo BOOLEAN DEFAULT TRUE NOT NULL COMMENT 'Indica si el pago está activo',
    
    CONSTRAINT fk_pagos_deuda
        FOREIGN KEY (deuda_id) 
        REFERENCES tbl_deudas(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    KEY idx_pagos_deuda (deuda_id),
    KEY idx_pagos_estado (estado),
    KEY idx_pagos_modalidad (modalidad_pago),
    KEY idx_pagos_fecha_creacion (fecha_creacion),
    KEY idx_pagos_activo (activo),
    KEY idx_pagos_deuda_estado (deuda_id, estado),
    KEY idx_pagos_estado_fecha (estado, fecha_creacion)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Tabla de pagos realizados por conductores para saldar sus deudas';