-- Create news table
CREATE TABLE news (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary TEXT NOT NULL,
    url VARCHAR(500) NOT NULL,
    source VARCHAR(255),
    published_at TIMESTAMP NOT NULL,
    category_id BIGINT,
    is_relevant BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
); 