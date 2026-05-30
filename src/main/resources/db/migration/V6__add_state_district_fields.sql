-- Add state and district columns to users table
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS state VARCHAR(128),
    ADD COLUMN IF NOT EXISTS district VARCHAR(128);

-- Add district column to addresses table
ALTER TABLE addresses
    ADD COLUMN IF NOT EXISTS district VARCHAR(128);

-- Ensure state is also on addresses (might already exist from V5, but adding just in case)
ALTER TABLE addresses
    ADD COLUMN IF NOT EXISTS state VARCHAR(128);
