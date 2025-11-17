-- V1.8__alter_work_location_and_expense_type_table.sql

-- Step 1: Thêm cột source_id (nullable trước)
ALTER TABLE work_location
    ADD COLUMN source_id VARCHAR(100);

ALTER TABLE expense_type
    ADD COLUMN source_id VARCHAR(100);

-- Step 2: Cập nhật source_id cho work_location
UPDATE work_location AS w
    JOIN (SELECT work_location_id,
                 ROW_NUMBER() OVER (ORDER BY work_location_id) AS rn
          FROM work_location) AS t ON w.work_location_id = t.work_location_id
SET w.source_id = CONCAT('WL-', t.rn);

-- Step 3: Cập nhật source_id cho expense_type
UPDATE expense_type AS e
    JOIN (SELECT expense_type_id,
                 ROW_NUMBER() OVER (ORDER BY expense_type_id) AS rn
          FROM expense_type) AS t2 ON e.expense_type_id = t2.expense_type_id
SET e.source_id = CONCAT('ET-', t2.rn);

-- Step 4: NOT NULL + UNIQUE
ALTER TABLE work_location
    MODIFY source_id VARCHAR(100) NOT NULL UNIQUE;

ALTER TABLE expense_type
    MODIFY source_id VARCHAR(100) NOT NULL UNIQUE;
