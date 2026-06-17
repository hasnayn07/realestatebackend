-- V3__bookings.sql
-- Bookings module: customers, bookings, installments, payments

CREATE TABLE customers (
                           id          BINARY(16)   PRIMARY KEY,
                           full_name   VARCHAR(150) NOT NULL,
                           cnic        VARCHAR(20)  NOT NULL UNIQUE,
                           phone       VARCHAR(20),
                           address     VARCHAR(300),
                           created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE bookings (
                          id                     BINARY(16)   PRIMARY KEY,
                          customer_id            BINARY(16)   NOT NULL,
                          unit_id                BINARY(16)   NOT NULL,
                          agent_id               BINARY(16),
                          sale_price             DECIMAL(14,2) NOT NULL,
                          down_payment           DECIMAL(14,2) NOT NULL,
                          number_of_installments INT          NOT NULL,
                          frequency              VARCHAR(12)  NOT NULL,
                          installment_start_date DATE         NOT NULL,
                          booking_date           DATE         NOT NULL,
                          status                 VARCHAR(12)  NOT NULL,
                          created_at             DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                          CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT,
                          CONSTRAINT fk_booking_unit     FOREIGN KEY (unit_id)     REFERENCES units(id)     ON DELETE RESTRICT,
                          CONSTRAINT fk_booking_agent    FOREIGN KEY (agent_id)    REFERENCES users(id)     ON DELETE SET NULL
);
CREATE INDEX idx_bookings_customer ON bookings(customer_id);
CREATE INDEX idx_bookings_unit     ON bookings(unit_id);
CREATE INDEX idx_bookings_status   ON bookings(status);

CREATE TABLE installments (
                              id                  BINARY(16)   PRIMARY KEY,
                              booking_id          BINARY(16)   NOT NULL,
                              installment_number  INT          NOT NULL,
                              due_date            DATE         NOT NULL,
                              amount_due          DECIMAL(14,2) NOT NULL,
                              amount_paid         DECIMAL(14,2) NOT NULL,
                              status              VARCHAR(12)  NOT NULL,
                              CONSTRAINT fk_installment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);
CREATE INDEX idx_installments_booking ON installments(booking_id);
CREATE INDEX idx_installments_status  ON installments(status);
CREATE INDEX idx_installments_duedate ON installments(due_date);

CREATE TABLE payments (
                          id              BINARY(16)   PRIMARY KEY,
                          installment_id  BINARY(16)   NOT NULL,
                          amount          DECIMAL(14,2) NOT NULL,
                          paid_date       DATE         NOT NULL,
                          proof_image_url VARCHAR(500),
                          status          VARCHAR(12)  NOT NULL,
                          verified_by     BINARY(16),
                          verified_at     DATETIME(6),
                          created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                          CONSTRAINT fk_payment_installment FOREIGN KEY (installment_id) REFERENCES installments(id) ON DELETE CASCADE,
                          CONSTRAINT fk_payment_verifier    FOREIGN KEY (verified_by)    REFERENCES users(id)        ON DELETE SET NULL
);
CREATE INDEX idx_payments_installment ON payments(installment_id);
CREATE INDEX idx_payments_status      ON payments(status);