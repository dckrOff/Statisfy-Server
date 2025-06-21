-- Create categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Create facts table
CREATE TABLE facts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category_id BIGINT,
    source VARCHAR(255),
    is_published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create statistics table
CREATE TABLE statistics (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    unit VARCHAR(50),
    category_id BIGINT,
    source VARCHAR(255),
    date DATE NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Insert some default categories
INSERT INTO categories (name, description) VALUES 
('Science', 'Scientific facts and discoveries'),
('History', 'Historical events and figures'),
('Technology', 'Tech innovations and trends'),
('Nature', 'Facts about our natural world'),
('Space', 'Astronomy and space exploration'); 