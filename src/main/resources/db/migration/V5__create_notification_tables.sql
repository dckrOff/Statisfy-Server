-- Create device_tokens table
CREATE TABLE IF NOT EXISTS device_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    device_type VARCHAR(50),
    created_at TIMESTAMP,
    last_used_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_device_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create notification_settings table
CREATE TABLE IF NOT EXISTS notification_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    daily_fact_enabled BOOLEAN DEFAULT TRUE,
    weekly_news_enabled BOOLEAN DEFAULT TRUE,
    breaking_news_enabled BOOLEAN DEFAULT TRUE,
    preferred_time TIME,
    timezone VARCHAR(50) DEFAULT 'UTC',
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    quiet_hours_enabled BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_notification_settings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Add indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_device_tokens_user_id ON device_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_device_tokens_is_active ON device_tokens(is_active);
CREATE INDEX IF NOT EXISTS idx_notification_settings_daily_fact_enabled ON notification_settings(daily_fact_enabled);
CREATE INDEX IF NOT EXISTS idx_notification_settings_preferred_time ON notification_settings(preferred_time); 