-- Create user_activities table
CREATE TABLE IF NOT EXISTS user_activities (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    activity_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    description TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_activities_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Add indexes for faster lookups and reporting
CREATE INDEX IF NOT EXISTS idx_user_activities_user_id ON user_activities(user_id);
CREATE INDEX IF NOT EXISTS idx_user_activities_activity_type ON user_activities(activity_type);
CREATE INDEX IF NOT EXISTS idx_user_activities_entity_type_entity_id ON user_activities(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_user_activities_created_at ON user_activities(created_at); 