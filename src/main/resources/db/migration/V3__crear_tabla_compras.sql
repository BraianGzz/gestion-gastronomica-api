-- V3: Creación de la tabla de compras de insumos

CREATE TABLE compras (
    id BIGSERIAL PRIMARY KEY,
    insumo_id BIGINT NOT NULL REFERENCES insumos(id),
    fecha DATE NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    costo NUMERIC(12, 2) NOT NULL CHECK (costo >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_compras_insumo_id ON compras(insumo_id);
CREATE INDEX idx_compras_fecha ON compras(fecha);
