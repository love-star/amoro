-- Licensed to the Apache Software Foundation (ASF) under one or more
-- contributor license agreements.  See the NOTICE file distributed with
-- this work for additional information regarding copyright ownership.
-- The ASF licenses this file to You under the Apache License, Version 2.0
-- (the "License"); you may not use this file except in compliance with
-- the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

-- If you have any changes to the AMS database, please record them in this file.
-- We will confirm the corresponding version of these upgrade scripts when releasing.

-- Update the precision from s level to ms.
ALTER TABLE `table_runtime` MODIFY COLUMN `optimizing_status_start_time` TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'Table optimize status start time';

-- Update processId to SnowflakeId
UPDATE `table_optimizing_process` SET `process_id` = `process_id` /10 << 13;
UPDATE `task_runtime` SET `process_id` = `process_id` /10 << 13;
UPDATE `optimizing_task_quota` SET `process_id` = `process_id` /10 << 13;
UPDATE `table_runtime` SET `optimizing_process_id` = `optimizing_process_id` /10 << 13;