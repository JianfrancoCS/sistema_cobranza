-- Asignar autoridades al grupo ADMINISTRADOR
INSERT INTO tbl_grupo_autoridades (grupo_id, autoridad_id) 
SELECT g.id, a.id 
FROM tbl_grupos g, tbl_autoridades a 
WHERE g.nombre_grupo = 'ADMINISTRADOR' 
AND a.nombre_autoridad IN (
    'VER_DASHBOARD',
    'VER_EMPLEADOS', 'CREAR_EMPLEADOS', 'EDITAR_EMPLEADOS', 'ELIMINAR_EMPLEADOS',
    'VER_AUTOS', 'CREAR_AUTOS', 'EDITAR_AUTOS', 'ELIMINAR_AUTOS',
    'VER_COMISIONES', 'CREAR_COMISIONES', 'EDITAR_COMISIONES', 'ELIMINAR_COMISIONES',
    'VER_DEUDAS_TODOS',
    'CREAR_DEUDAS',
    'VER_PAGOS_TODOS',
    'GESTIONAR_PAGOS',
    'VER_USUARIOS', 'CREAR_USUARIOS', 'EDITAR_USUARIOS', 'ELIMINAR_USUARIOS',
    'VER_AUTORIDADES',
    'VER_GRUPOS', 'CREAR_GRUPOS', 'EDITAR_GRUPOS', 'ELIMINAR_GRUPOS',
    'ASIGNAR_PERMISOS', 'ASIGNAR_GRUPOS','ASIGNAR_AUTOS'
);

-- Asignar autoridades al grupo SUPERVISOR  
INSERT INTO tbl_grupo_autoridades (grupo_id, autoridad_id)
SELECT g.id, a.id 
FROM tbl_grupos g, tbl_autoridades a 
WHERE g.nombre_grupo = 'SUPERVISOR'
AND a.nombre_autoridad IN (
    'VER_EMPLEADOS', 'VER_AUTOS',
    'VER_COMISIONES',
    'VER_DEUDAS_TODOS',
    'CREAR_DEUDAS',
    'VER_PAGOS_TODOS', 'CREAR_PAGOS_FISICOS',
    'CREAR_MARCACIONES'
);

-- Asignar autoridades al grupo CONDUCTOR
INSERT INTO tbl_grupo_autoridades (grupo_id, autoridad_id)
SELECT g.id, a.id 
FROM tbl_grupos g, tbl_autoridades a 
WHERE g.nombre_grupo = 'CONDUCTOR'
AND a.nombre_autoridad IN (
    'VER_EMPLEADOS_PROPIO',
    'VER_DEUDAS_PROPIOS',
    'VER_PAGOS_PROPIOS', 'CREAR_PAGOS_VIRTUALES', 'ELIMINAR_PAGOS_PROPIOS'
);