-- V6: Cambiar descuento de monto fijo (NUMERIC) a porcentaje entero (0 a 100)

ALTER TABLE ventas DROP CONSTRAINT IF EXISTS ventas_descuento_check;

ALTER TABLE ventas
    ALTER COLUMN descuento DROP DEFAULT,
    ALTER COLUMN descuento TYPE INTEGER USING descuento::INTEGER,
    ALTER COLUMN descuento SET DEFAULT 0;

ALTER TABLE ventas
    ADD CONSTRAINT chk_ventas_descuento_porcentaje CHECK (descuento >= 0 AND descuento <= 100);
