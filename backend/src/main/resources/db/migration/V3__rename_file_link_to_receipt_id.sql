DO $$
BEGIN
  -- есть ли колонка file_link в public.shopping_lists ?
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name  = 'shopping_lists'
      AND column_name = 'file_link'
  ) THEN
    -- если тип не uuid, меняем тип
    IF EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_schema = 'public'
        AND table_name  = 'shopping_lists'
        AND column_name = 'file_link'
        AND data_type  <> 'uuid'
    ) THEN
      EXECUTE 'ALTER TABLE public.shopping_lists
               ALTER COLUMN file_link TYPE uuid USING file_link::uuid';
    END IF;

    -- если ещё нет целевой колонки, переименовываем
    IF NOT EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_schema = 'public'
        AND table_name  = 'shopping_lists'
        AND column_name = 'receipt_id'
    ) THEN
      EXECUTE 'ALTER TABLE public.shopping_lists
               RENAME COLUMN file_link TO receipt_id';
    END IF;
  END IF;
END $$;
