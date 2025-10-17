package org.ohdsi.circe.cohortdefinition.builders;

import org.apache.commons.lang3.StringUtils;
import org.ohdsi.circe.cohortdefinition.CareSite;
import org.ohdsi.circe.helper.ResourceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.ohdsi.circe.cohortdefinition.builders.BuilderUtils.getCodesetJoinExpression;

public class CareSiteSqlBuilder<T extends CareSite> extends CriteriaSqlBuilder<T> {

  private final static String CARE_SITE_TEMPLATE = ResourceHelper.GetResourceAsString("/resources/cohortdefinition/sql/careSite.sql");

  // default columns are those that are specified in the template, and dont' need to be added if specifeid in 'additionalColumns'
  private final Set<CriteriaColumn> DEFAULT_COLUMNS = new HashSet<>(Arrays.asList(CriteriaColumn.START_DATE, CriteriaColumn.END_DATE, CriteriaColumn.VISIT_ID));

  @Override
  protected Set<CriteriaColumn> getDefaultColumns() {
    return DEFAULT_COLUMNS;
  }

  @Override
  protected String getQueryTemplate() {
    return CARE_SITE_TEMPLATE;
  }

  @Override
  protected String getTableColumnForCriteriaColumn(CriteriaColumn column) {
    switch (column) {
      case DOMAIN_CONCEPT:
        return "C.care_site_concept_id";
      default:
        throw new IllegalArgumentException("Invalid CriteriaColumn for Care Site:" + column.toString());
    }
  }

  @Override
  protected String embedCodesetClause(String query, T criteria) {

    return StringUtils.replace(query, "@codesetClause",
            getCodesetJoinExpression(criteria.codesetId,
                    "csh.care_site_concept_id",
                    null,
                    null)
    );
  }

  @Override
  protected String embedOrdinalExpression(String query, T criteria, List<String> whereClauses) {

    return query;
  }

  @Override
  protected List<String> resolveJoinClauses(T criteria) {

    return new ArrayList<>();
  }

  @Override
  protected List<String> resolveWhereClauses(T criteria) {

    return new ArrayList<>();
  }
}
