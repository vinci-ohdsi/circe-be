package org.ohdsi.circe.check.checkers;

import static org.ohdsi.circe.check.operations.Operations.match;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import org.ohdsi.circe.check.WarningSeverity;
import org.ohdsi.circe.check.utils.CriteriaNameHelper;
import org.ohdsi.circe.cohortdefinition.CorelatedCriteria;
import org.ohdsi.circe.cohortdefinition.Criteria;
import org.ohdsi.circe.cohortdefinition.CriteriaGroup;
import org.ohdsi.circe.cohortdefinition.VisitOccurrence;
import org.ohdsi.circe.check.operations.Execution; 

/**
 * Checks for nested VisitOccurrence criteria within CorrelatedCriteria of another parent criterion.
 * This pattern is discouraged as it can lead to unexpected results and performance issues.
 * The preferred approach is to use visit-related attributes on the parent criterion if available.
 */
public class NestedVisitOccurrenceCheck extends BaseCriteriaCheck {

    private static final String NESTED_VISIT_WARNING = "A Visit Occurrence criterion is nested within a %s criterion in %s. "
            + "This pattern can cause unexpected results or performance issues. Consider using visit-related attributes on the parent criterion instead.";

    @Override
    protected WarningSeverity defineSeverity() {
        return WarningSeverity.WARNING;
    }

    @Override
    protected void checkCriteria(Criteria parentCriteria, String groupName, WarningReporter reporter) {
        if (parentCriteria == null) {
            System.out.println("[DEBUG] NestedVisitOccurrenceCheck: Parent criteria is null in group: " + groupName);
            return;
        }

        final String parentName = CriteriaNameHelper.getCriteriaName(parentCriteria);
        System.out.println("[DEBUG] NestedVisitOccurrenceCheck: Checking parent criteria type: " + parentName + " in group: " + groupName);

        match(parentCriteria.CorrelatedCriteria)
                .when(Objects::nonNull)
                .then(correlatedCriteria -> {
                    System.out.println("[DEBUG] NestedVisitOccurrenceCheck: Found CorrelatedCriteria within " + parentName + " in group " + groupName + ". Checking for nested Visit Occurrences.");

                    // Define the action to take when a nested VisitOccurrence is found
                    // Use Execution instead of Runnable
                    Execution raiseWarning = () -> { 
                        System.out.println("[WARN] NestedVisitOccurrenceCheck: Nested Visit Occurrence found within " + parentName + " in group " + groupName);
                        reporter.add(NESTED_VISIT_WARNING, parentName, groupName);
                    };

                    // Define a consumer to check a single CorelatedCriteria item
                    Consumer<CorelatedCriteria> checkNestedVisit = nestedCorelated ->
                            match(nestedCorelated.criteria)
                                    .isA(VisitOccurrence.class)
                                    .then(raiseWarning); // Now this matches then(Execution execution)

                    // Check criteriaList
                    if (correlatedCriteria.criteriaList != null) {
                        Arrays.stream(correlatedCriteria.criteriaList)
                                .filter(Objects::nonNull)
                                .forEach(checkNestedVisit);
                    } else {
                         System.out.println("[DEBUG] NestedVisitOccurrenceCheck: CorrelatedCriteria.criteriaList is null for " + parentName + " in group " + groupName);
                    }

                    // Check groups within CorrelatedCriteria
                    if (correlatedCriteria.groups != null) {
                        Arrays.stream(correlatedCriteria.groups)
                                .filter(Objects::nonNull)
                                .forEach(group -> {
                                    if (group.criteriaList != null) {
                                        Arrays.stream(group.criteriaList)
                                                .filter(Objects::nonNull)
                                                .forEach(checkNestedVisit);
                                    } else {
                                         System.out.println("[DEBUG] NestedVisitOccurrenceCheck: CorrelatedCriteria group criteriaList is null for " + parentName + " in group " + groupName);
                                    }
                                    // Recursively check sub-groups (though less common)
                                    if (group.groups != null) {
                                        System.out.println("[WARN] NestedVisitOccurrenceCheck: Deeply nested groups found within CorrelatedCriteria for " + parentName + " in group " + groupName + ". Checking recursively.");
                                        Arrays.stream(group.groups)
                                            .filter(Objects::nonNull)
                                            .forEach(subGroup -> checkCriteriaGroupInGroup(subGroup, parentName, groupName, reporter, checkNestedVisit));
                                    }
                                });
                    } else {
                         System.out.println("[DEBUG] NestedVisitOccurrenceCheck: CorrelatedCriteria.groups is null for " + parentName + " in group " + groupName);
                    }
                });
    }

    private void checkCriteriaGroupInGroup(CriteriaGroup criteriaGroup, String parentName, String groupName, WarningReporter reporter, Consumer<CorelatedCriteria> checkNestedVisit) {
        if (criteriaGroup.criteriaList != null) {
             Arrays.stream(criteriaGroup.criteriaList)
                     .filter(Objects::nonNull)
                     .forEach(checkNestedVisit);
        }
         if (criteriaGroup.groups != null) {
             Arrays.stream(criteriaGroup.groups)
                     .filter(Objects::nonNull)
                     .forEach(subGroup -> checkCriteriaGroupInGroup(subGroup, parentName, groupName, reporter, checkNestedVisit));
        }
    }
}
