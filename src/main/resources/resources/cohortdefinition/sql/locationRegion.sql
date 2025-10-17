-- Begin Location region Criteria
select
  C.entity_id AS person_id,
  C.location_history_id as event_id,
  C.start_date as start_date,
  ISNULL(C.end_date, DATEFROMPARTS(2099,12,31)) as end_date,
  CAST(NULL as bigint) as visit_occurrence_id,
  C.start_date as sort_date@additionalColumns
from
(
 select l.*,
         lh.location_history_id,
	 lh.entity_id,
	 lh.start_date,
	 lh.end_date,
	 lh.location_event_id
  FROM @cdm_database_schema.LOCATION l
    JOIN @cdm_database_schema.LOCATION_HISTORY lh ON l.location_id = lh.location_id
    @codesetClause
  WHERE loc_event_field_id = 1147792  -- Metadata domain code for person.care_site_id
) C
-- End Location region Criteria
