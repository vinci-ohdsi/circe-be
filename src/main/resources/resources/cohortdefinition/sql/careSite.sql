-- Begin Care Site Criteria
select  
  C.person_id,
  C.care_site_concept_id as event_id,
  C.start_date as start_date,
  ISNULL(C.end_date, DATEFROMPARTS(2099,12,31)) as end_date,
  CAST(NULL as bigint) as visit_occurrence_id,
  C.start_date as sort_date@additionalColumns
from
(
 select  csh.care_site_concept_id,
         csh.care_site_history_id,
	 csh.entity_id person_id,
	 csh.start_date,
	 csh.end_date
  from @cdm_database_schema.CARE_SITE_HISTORY csh
    @codesetClause
  where csh.entity_field_id = 1147026 
) C
@whereClause
-- End Care Site Criteria
