-- V5: Creación de las tablas de ventas, líneas de venta y modificaciones

CREATE TABLE ventas (
    id BIGSERIAL PRIMARY KEY,
    nombre_cliente VARCHAR(100) NOT NULL,
    fecha DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'REGISTRADA',
    tipo_entrega VARCHAR(20) NOT NULL,
    direccion VARCHAR(255),
    forma_pago VARCHAR(20) NOT NULL,
    descuento NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (descuento >= 0),
    total NUMERIC(12, 2) NOT NULL CHECK (total >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lineas_de_venta (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(12, 2) NOT NULL CHECK (precio_unitario > 0),
    subtotal NUMERIC(12, 2) NOT NULL CHECK (subtotal >= 0)
);

CREATE TABLE modificaciones (
    id BIGSERIAL PRIMARY KEY,
    linea_de_venta_id BIGINT NOT NULL REFERENCES lineas_de_venta(id) ON DELETE CASCADE,
    insumo_id BIGINT NOT NULL REFERENCES insumos(id),
    tipo_modificacion VARCHAR(20) NOT NULL,
    cantidad_extra INTEGER NOT NULL CHECK (cantidad_extra > 0),
    precio_extra NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (precio_extra >= 0),
    descripcion VARCHAR(255),
    CONSTRAINT uk_modificacion_linea_insumo UNIQUE (linea_de_venta_id, insumo_id)
);

CREATE INDEX idx_ventas_fecha ON ventas(fecha);
CREATE INDEX idx_ventas_estado ON ventas(estado);
CREATE INDEX idx_lineas_venta_id ON lineas_de_venta(venta_id);
CREATE INDEX idx_lineas_producto_id ON lineas_de_venta(producto_id);
CREATE INDEX idx_modificaciones_linea_id ON modificaciones(linea_de_venta_id);
CREATE INDEX idx_modificaciones_insumo_id ON modificaciones(insumo_id);
