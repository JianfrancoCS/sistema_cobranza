CREATE TABLE tbl_usuario_autoridades (
    usuario_id BIGINT NOT NULL,
    autoridad_id BIGINT NOT NULL,

    PRIMARY KEY (usuario_id, autoridad_id),

    CONSTRAINT fk_usuario_autoridades_usuario
        FOREIGN KEY (usuario_id) REFERENCES tbl_usuarios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_usuario_autoridades_autoridad
        FOREIGN KEY (autoridad_id) REFERENCES tbl_autoridades(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_grupo_autoridades (
    grupo_id BIGINT NOT NULL,
    autoridad_id BIGINT NOT NULL,

    PRIMARY KEY (grupo_id, autoridad_id),
    CONSTRAINT fk_grupo_autoridades_grupo
        FOREIGN KEY (grupo_id) REFERENCES tbl_grupos(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_grupo_autoridades_autoridad
        FOREIGN KEY (autoridad_id) REFERENCES tbl_autoridades(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_usuario_grupos (
    usuario_id BIGINT NOT NULL,
    grupo_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, grupo_id),
    CONSTRAINT fk_usuario_grupos_usuario
        FOREIGN KEY (usuario_id) REFERENCES tbl_usuarios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_usuario_grupos_grupo
        FOREIGN KEY (grupo_id) REFERENCES tbl_grupos(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;