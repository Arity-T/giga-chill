ALTER TABLE shopping_lists 
  ALTER COLUMN file_link SET DATA TYPE UUID USING file_link::UUID;

ALTER TABLE shopping_lists 
  RENAME COLUMN file_link TO receipt_id;