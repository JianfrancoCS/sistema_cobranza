CREATE TABLE tbl_autos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    serie VARCHAR(50),
    color VARCHAR(30) NOT NULL,
    motor VARCHAR(50),
    vin VARCHAR(17) ,
    empleado_id BIGINT,
    imagen MEDIUMBLOB,
    imagen_tipo VARCHAR(100),
    es_propio_empresa BOOLEAN DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_por VARCHAR(50),
    actualizado_por VARCHAR(50),
    eliminado_por VARCHAR(50),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fecha_eliminacion TIMESTAMP NULL,

    CONSTRAINT fk_autos_empleado
        FOREIGN KEY (empleado_id)
        REFERENCES tbl_empleados(id)
        ON DELETE SET NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_autos_placa ON tbl_autos(placa);
CREATE INDEX idx_autos_activo ON tbl_autos(activo);