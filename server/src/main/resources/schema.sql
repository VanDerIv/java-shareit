DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS requests;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE UNIQUE INDEX IF NOT EXISTS ind_uniq_user_email
    ON users (email ASC NULLS LAST);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(4000) NOT NULL,
  available BOOLEAN NOT NULL,
  owner_id BIGINT NOT NULL,
  request_id BIGINT
);
CREATE INDEX IF NOT EXISTS ind_items_request
    ON items
        (request_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS ind_items_user
    ON items
        (owner_id ASC NULLS LAST);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  text VARCHAR(4000) NOT NULL,
  item_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  created TIMESTAMP
);
CREATE INDEX IF NOT EXISTS ind_comments_item
    ON comments
        (item_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS ind_comments_user
    ON comments
        (author_id ASC NULLS LAST);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  status VARCHAR(60) DEFAULT 'WAITING'
);
CREATE INDEX IF NOT EXISTS ind_bookings_item
    ON bookings
        (item_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS ind_bookings_user
    ON bookings
        (booker_id ASC NULLS LAST);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  description VARCHAR(4000) NOT NULL,
  requestor_id BIGINT NOT NULL,
  created TIMESTAMP
);
CREATE INDEX IF NOT EXISTS ind_requests_user
    ON requests
        (requestor_id ASC NULLS LAST);