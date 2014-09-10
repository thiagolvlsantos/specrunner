SELECT TABLE_NAME, COLUMN_NAME, COLUMN_DEFAULT, DTD_IDENTIFIER, DATA_TYPE, IS_IDENTITY, IS_NULLABLE
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE table_schema = 'ORD' 
        AND table_name in(select TABLE_NAME from INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_SCHEM = 'ORD')