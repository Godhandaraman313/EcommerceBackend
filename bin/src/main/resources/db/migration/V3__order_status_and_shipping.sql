ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS status VARCHAR(32) NOT NULL DEFAULT 'NEW',
    ADD COLUMN IF NOT EXISTS shipping_address TEXT;

UPDATE orders SET status = 'NEW' WHERE status IS NULL OR status = '';
