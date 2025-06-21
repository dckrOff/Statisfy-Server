-- Create user_preferences table
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    interests VARCHAR(500),
    preferred_language VARCHAR(50),
    CONSTRAINT fk_user_preferences_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create join table for user preferred categories
CREATE TABLE IF NOT EXISTS user_preferred_categories (
    preference_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (preference_id, category_id),
    CONSTRAINT fk_user_preferred_categories_preference FOREIGN KEY (preference_id) REFERENCES user_preferences(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_preferred_categories_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Add index for faster lookups
CREATE INDEX IF NOT EXISTS idx_user_preferences_user_id ON user_preferences(user_id);
CREATE INDEX IF NOT EXISTS idx_user_preferred_categories_preference_id ON user_preferred_categories(preference_id);
CREATE INDEX IF NOT EXISTS idx_user_preferred_categories_category_id ON user_preferred_categories(category_id); 