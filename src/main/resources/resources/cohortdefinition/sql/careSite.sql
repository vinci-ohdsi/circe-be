-- Begin Care Site (via Care_Site_History) Criteria
select
  C.care_site_concept_id
  C.person_id,
  C.care_site_history_id as event_id,
  C.start_date as start_date,
  ISNULL(C.end_date, DATEFROMPARTS(2099,12,31)) as end_date,
  CAST(NULL as bigint) as visit_occurrence_id,
  C.start_date as sort_date@additionalColumns
from
(
 select  csh.care_site_concept_id,
         csh.care_site_history_id,
	 csh.person_id,
	 csh.start_date,
	 csh.end_date
  FROM @cdm_database_schema.CARE_SITE_HISTORY csh
    @codesetClause
) C
-- End Care Site Criteria
