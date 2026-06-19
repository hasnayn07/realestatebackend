CREATE TABLE dealers (
                         id BINARY(16) NOT NULL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         cnic VARCHAR(15) NOT NULL UNIQUE,
                         agency_name VARCHAR(255),
                         commission_percentage DECIMAL(5,2) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE bookings
    ADD COLUMN dealer_id BINARY(16),
    ADD CONSTRAINT fk_booking_dealer FOREIGN KEY (dealer_id) REFERENCES dealers(id);