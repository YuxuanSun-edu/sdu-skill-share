USE shanwei_skill;

ALTER TABLE skill
  ADD COLUMN service_mode VARCHAR(64) NULL AFTER price_type,
  ADD COLUMN price VARCHAR(64) NULL AFTER service_mode;

UPDATE skill
SET service_mode = COALESCE(service_mode, price_type),
    price = COALESCE(price, '面议')
WHERE service_mode IS NULL OR price IS NULL;
