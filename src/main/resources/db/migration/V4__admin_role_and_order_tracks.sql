ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(32) NOT NULL DEFAULT 'CUSTOMER';

UPDATE users SET role = 'CUSTOMER' WHERE role IS NULL OR role = '';

CREATE TABLE IF NOT EXISTS order_tracks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    notes VARCHAR(255),
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_track_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_order_tracks_order ON order_tracks(order_id);
