CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--CREATE EXTENSION IF NOT EXISTS spi;

CREATE TABLE COMPANY(
    id integer PRIMARY KEY,
    ext_id uuid default uuid_generate_v4(),
    name VARCHAR NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE MOVIE(
    id integer PRIMARY KEY,
    ext_id uuid default uuid_generate_v4(),
    title VARCHAR NOT NULL,
    budget BIGINT DEFAULT 0,
    revenue BIGINT DEFAULT 0,
    profit BIGINT DEFAULT 0,
    release_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE GENRE(
    id serial PRIMARY KEY,
    ext_id uuid default uuid_generate_v4(),
    name VARCHAR,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE RATING(
    user_id INTEGER NOT NULL,
    movie_id INTEGER NOT NULL,
    score REAL NOT NULL,
    rating_timestamp DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT user_rating PRIMARY KEY (user_id,movie_id),
    FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE MOVIE_TO_GENRE(
    movie_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    CONSTRAINT movie_genre PRIMARY KEY (movie_id,genre_id),
    FOREIGN KEY (movie_id) REFERENCES movie (id),
    FOREIGN KEY (genre_id) REFERENCES genre (id)
);

CREATE TABLE MOVIE_TO_COMPANY(
    movie_id INTEGER NOT NULL,
    company_id INTEGER NOT NULL,
    CONSTRAINT movie_company PRIMARY KEY (movie_id,company_id),
    FOREIGN KEY (movie_id) REFERENCES movie (id),
    FOREIGN KEY (company_id) REFERENCES company (id)
);

-- Temporary function until spi module with moddatetime() available
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- Triggers
CREATE TRIGGER set_timestamp BEFORE UPDATE ON COMPANY FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();
CREATE TRIGGER set_timestamp BEFORE UPDATE ON MOVIE FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();
CREATE TRIGGER set_timestamp BEFORE UPDATE ON GENRE FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();
CREATE TRIGGER set_timestamp BEFORE UPDATE ON RATING FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();