DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'phonebook_db') THEN
      CREATE DATABASE phonebook_db;
END IF;
END $$;
