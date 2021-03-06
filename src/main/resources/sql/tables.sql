DROP TABLE IF EXISTS market_data.currencies CASCADE;
CREATE TABLE market_data.currencies (
  id          BIGSERIAL PRIMARY KEY,
  name        VARCHAR(40) UNIQUE NOT NULL,
  symbol      VARCHAR(20)        NOT NULL,
  max_supply  NUMERIC,
  description TEXT
);

COMMENT ON TABLE market_data.currencies IS 'All crypto-currencies';
COMMENT ON COLUMN market_data.currencies.name IS 'Name of currency';
COMMENT ON COLUMN market_data.currencies.symbol IS 'Unique symbol of currency';
COMMENT ON COLUMN market_data.currencies.max_supply IS 'Maximal supply of this currency';
COMMENT ON COLUMN market_data.currencies.description IS 'Description of currency or additional information';

DROP TABLE IF EXISTS market_data.history;
CREATE TABLE market_data.history (
  id        BIGSERIAL PRIMARY KEY,
  name      VARCHAR(40) NOT NULL REFERENCES market_data.currencies (name),
  dt        BIGINT,
  price_btc NUMERIC,
  price_usd NUMERIC
);

CREATE INDEX ON market_data.history (name);

COMMENT ON TABLE market_data.history IS 'History of prices on crypto-currencies';
COMMENT ON COLUMN market_data.history.name IS 'Name of currency';
COMMENT ON COLUMN market_data.history.dt IS 'Timestamp of price';
COMMENT ON COLUMN market_data.history.price_btc IS 'Price in BTC';
COMMENT ON COLUMN market_data.history.price_usd IS 'Price in USD';