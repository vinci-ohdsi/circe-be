package org.ohdsi.circe.check.checkers;

import org.junit.Before;
import org.junit.Test;
import org.ohdsi.circe.check.Warning;
import org.ohdsi.circe.cohortdefinition.CohortExpression;
import org.ohdsi.circe.check.Constants;
import org.ohdsi.circe.helper.ResourceHelper;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NestedVisitOccurrenceCheckTest {

    // Load the expression with the nested visit (provided in the prompt)
    private static final String NESTED_VISIT_JSON_PATH = "/checkers/nestedVisitCriterion.json";
    private static CohortExpression INCORRECT_EXPRESSION;

    // Load a correct expression (without nested visits)
    private static final String CORRECT_VISIT_JSON_PATH = "/checkers/attributeVisitCriterion.json";
    private static CohortExpression CORRECT_EXPRESSION;


    private final BaseCheck check = new NestedVisitOccurrenceCheck();

    @Before
    public void setUp() {
         // Load expressions once for all tests in this class
         if (INCORRECT_EXPRESSION == null) {
             System.out.println("NestedVisitOccurrenceCheckTest: Loading incorrect expression from: " + NESTED_VISIT_JSON_PATH);
             String json = ResourceHelper.GetResourceAsString(NESTED_VISIT_JSON_PATH);
             if (json == null) {
                 throw new RuntimeException("Failed to load resource: " + NESTED_VISIT_JSON_PATH);
             }
             INCORRECT_EXPRESSION = CohortExpression.fromJson(json);
             System.out.println("NestedVisitOccurrenceCheckTest: Incorrect expression loaded successfully.");
         }
         if (CORRECT_EXPRESSION == null) {
              System.out.println("NestedVisitOccurrenceCheckTest: Loading correct expression from: " + CORRECT_VISIT_JSON_PATH);
             String json = ResourceHelper.GetResourceAsString(CORRECT_VISIT_JSON_PATH);
             if (json == null) {
                 throw new RuntimeException("Failed to load resource: " + CORRECT_VISIT_JSON_PATH);
             }
             CORRECT_EXPRESSION = CohortExpression.fromJson(json);
              System.out.println("NestedVisitOccurrenceCheckTest: Correct expression loaded successfully.");
         }
    }

    @Test
    public void checkIncorrectExpression() {
        System.out.println("NestedVisitOccurrenceCheckTest: Running check on incorrect expression...");
        List<Warning> warnings = check.check(INCORRECT_EXPRESSION);
        System.out.println("NestedVisitOccurrenceCheckTest: Found " + warnings.size() + " warnings for incorrect expression.");

        // We expect exactly one warning in the provided example JSON
        assertEquals("Expected 1 warning for the incorrect expression", 1, warnings.size());

        // Optionally, check the warning message content
        String expectedParent = Constants.Criteria.CONDITION_OCCURRENCE; // Parent is ConditionOccurrence
        String expectedGroup = BaseCheck.INCLUSION_RULE + "has diagnosis of hypertension in Visit derived from encounter on claim"; // Location is this inclusion rule
        assertTrue("Warning message should contain expected details",
                warnings.get(0).toMessage().contains(String.format("nested within a %s criterion in %s", expectedParent, expectedGroup)));
        System.out.println("NestedVisitOccurrenceCheckTest: Check on incorrect expression passed.");
    }

    @Test
    public void checkCorrectExpression() {
         System.out.println("NestedVisitOccurrenceCheckTest: Running check on correct expression...");
         List<Warning> warnings = check.check(CORRECT_EXPRESSION);
         System.out.println("NestedVisitOccurrenceCheckTest: Found " + warnings.size() + " warnings for correct expression.");

        // We expect no warnings for the correct expression
         assertEquals("Expected 0 warnings for the correct expression", 0, warnings.size());
         System.out.println("NestedVisitOccurrenceCheckTest: Check on correct expression passed.");
    }

    @Test
    public void checkExpressionWithNullCorrelatedCriteria() {
        // Create a minimal expression where CorrelatedCriteria might be null
        System.out.println("NestedVisitOccurrenceCheckTest: Running check on expression with potentially null CorrelatedCriteria...");
        String json = "{\"ConceptSets\":[],\"PrimaryCriteria\":{\"CriteriaList\":[{\"ConditionOccurrence\":{}}],\"ObservationWindow\":{\"PriorDays\":0,\"PostDays\":0},\"PrimaryCriteriaLimit\":{\"Type\":\"All\"}},\"InclusionRules\":[]}";
        CohortExpression expression = CohortExpression.fromJson(json);
        List<Warning> warnings = check.check(expression);
        System.out.println("NestedVisitOccurrenceCheckTest: Found " + warnings.size() + " warnings for null CorrelatedCriteria expression.");
        assertEquals("Expected 0 warnings when CorrelatedCriteria is null", 0, warnings.size());
        System.out.println("NestedVisitOccurrenceCheckTest: Check on null CorrelatedCriteria expression passed.");
    }

     @Test
     public void checkExpressionWithEmptyCorrelatedCriteriaLists() {
         // Create an expression with CorrelatedCriteria but empty lists/groups
         System.out.println("NestedVisitOccurrenceCheckTest: Running check on expression with empty CorrelatedCriteria lists...");


	 String json = "{\n" +
             "  \"ConceptSets\": [{\"id\":0,\"name\":\"concept\",\"expression\":{\"items\":[]}}],\n" +
             "  \"PrimaryCriteria\": {\n" +
             "    \"CriteriaList\": [\n" +
             "      {\n" +
             "        \"ConditionOccurrence\": {\n" +
             "          \"CodesetId\": 0,\n" +
             "          \"CorrelatedCriteria\": {\n" +
             "            \"Type\": \"ALL\",\n" +
             "            \"CriteriaList\": [],\n" +
             "            \"DemographicCriteriaList\": [],\n" +
             "            \"Groups\": []\n" +
             "          }\n" +
             "        }\n" +
             "      }\n" +
             "    ],\n" +
             "    \"ObservationWindow\": {\"PriorDays\": 0,\"PostDays\": 0},\n" +
             "    \"PrimaryCriteriaLimit\": {\"Type\": \"All\"}\n" +
             "  },\n" +
             "  \"InclusionRules\": []\n" +
             "}";
	 
          CohortExpression expression = CohortExpression.fromJson(json);
          List<Warning> warnings = check.check(expression);
          System.out.println("NestedVisitOccurrenceCheckTest: Found " + warnings.size() + " warnings for empty CorrelatedCriteria expression.");
          assertEquals("Expected 0 warnings for empty CorrelatedCriteria lists", 0, warnings.size());
          System.out.println("NestedVisitOccurrenceCheckTest: Check on empty CorrelatedCriteria expression passed.");
     }
}

