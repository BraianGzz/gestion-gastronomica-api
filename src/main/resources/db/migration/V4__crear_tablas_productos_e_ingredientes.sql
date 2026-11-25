-- V4: Creación de las tablas de productos e ingredientes (recetas)

CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    precio NUMERIC(12, 2) NOT NULL CHECK (precio > 0),
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ingredientes (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    insumo_id BIGINT NOT NULL REFERENCES insumos(id),
    cantidad_consumida INTEGER NOT NULL CHECK (cantidad_consumida > 0),
    CONSTRAINT uk_ingrediente_producto_insumo UNIQUE (producto_id, insumo_id)
);

CREATE INDEX idx_ingredientes_producto_id ON ingredientes(producto_id);
CREATE INDEX idx_ingredientes_insumo_id ON ingredientes(insumo_id);
