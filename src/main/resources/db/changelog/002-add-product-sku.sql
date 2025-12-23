--liquibase formatted sql

--changeset monomart:007-add-product-sku
ALTER TABLE products ADD COLUMN sku VARCHAR(100);

--update existing rows with a temporary unique value
UPDATE products SET sku = gen_random_uuid()::text WHERE sku IS NULL;

--add not null constraint
ALTER TABLE products ALTER COLUMN sku SET NOT NULL;

--add unique constraint
ALTER TABLE products ADD CONSTRAINT uq_products_sku UNIQUE(sku);
