-- Run manually if not using spring.jpa.hibernate.ddl-auto=update
-- Default sample image for all products/categories until a per-product upload is set

ALTER TABLE product
    ADD COLUMN IF NOT EXISTS image_url VARCHAR(512) DEFAULT '/images/sample-product.jpg',
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS in_stock BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS average_rating DOUBLE DEFAULT 0,
    ADD COLUMN IF NOT EXISTS review_count INT DEFAULT 0;

UPDATE product
SET image_url = '/images/sample-product.jpg'
WHERE image_url IS NULL OR image_url = '';

UPDATE product SET in_stock = TRUE WHERE in_stock IS NULL;
UPDATE product SET average_rating = 0 WHERE average_rating IS NULL;
UPDATE product SET review_count = 0 WHERE review_count IS NULL;

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    headline VARCHAR(128) NOT NULL,
    comment TEXT NOT NULL,
    rating INT NOT NULL,
    review_time DATETIME(6) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255),
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_reviews_product ON reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_reviews_customer ON reviews(customer_email);
