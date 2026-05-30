-- Add hashtags column to product table (stored as CSV e.g. "gaming,laptop,tech")
ALTER TABLE product
    ADD COLUMN IF NOT EXISTS hashtags TEXT;
