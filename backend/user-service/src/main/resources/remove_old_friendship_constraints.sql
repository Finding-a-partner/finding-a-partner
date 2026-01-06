-- Удаление старых check constraints для таблицы friendship
-- Эти constraints не разрешают статус NO_CONNECTION

-- Удаляем constraint для status_for_user_1, если он существует
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'friendship_status_for_user_1_check'
    ) THEN
        ALTER TABLE friendship DROP CONSTRAINT friendship_status_for_user_1_check;
    END IF;
END $$;

-- Удаляем constraint для status_for_user_2, если он существует
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'friendship_status_for_user_2_check'
    ) THEN
        ALTER TABLE friendship DROP CONSTRAINT friendship_status_for_user_2_check;
    END IF;
END $$;

