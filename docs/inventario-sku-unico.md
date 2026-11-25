# Unicidad de SKU en una base existente

Prueba primero el procedimiento en una copia aislada. Programa una ventana de
mantenimiento, detén las escrituras de Inventario y respalda su esquema y datos.
Verifica con `SELECT DATABASE();` que estás conectado al esquema correcto.

Comprueba si ya existe un índice único de columna completa sobre el SKU.
La consulta debe devolver una fila para un índice de una sola columna.

```sql
SELECT index_name
FROM information_schema.statistics
WHERE table_schema = DATABASE() AND table_name = 'inventario' AND non_unique = 0
GROUP BY index_name
HAVING COUNT(*) = 1 AND SUM(column_name = 'producto_sku' AND sub_part IS NULL) = 1;
```

Busca duplicados según la normalización aplicada por el servicio y según
la comparación de la columna. Conserva las fichas y los movimientos.

```sql
SELECT UPPER(TRIM(producto_sku)) AS sku_normalizado, COUNT(*) AS fichas
FROM inventario
GROUP BY sku_normalizado
HAVING COUNT(*) > 1;

SELECT producto_sku, COUNT(*) AS fichas
FROM inventario
GROUP BY producto_sku
HAVING COUNT(*) > 1;
```

Si hay duplicados, detén el procedimiento y acuerda la corrección con el
responsable de datos. Si ambas consultas están vacías y falta el índice,
aplica la restricción explícitamente. Hibernate con `ddl-auto=update`
puede dejar una tabla existente sin el índice esperado.

```sql
ALTER TABLE inventario
ADD CONSTRAINT uq_inventario_producto_sku UNIQUE (producto_sku);
```

Repite la consulta de índices y comprueba que las fichas y los movimientos
conservan sus conteos. El DDL de MySQL confirma implícitamente la operación.
Si falla, conserva el respaldo y revisa el error antes de reabrir escrituras.
