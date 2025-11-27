package org.ohdsi.circe.cohortdefinition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.ohdsi.analysis.versioning.CdmVersion;
import org.ohdsi.circe.cohortdefinition.builders.BuilderOptions;

@JsonTypeName("CareSite")
@CdmVersion(range = ">=5.1")
public class CareSite extends GeoCriteria {

    @JsonProperty("CodesetId")
    public Integer codesetId;

    @JsonProperty("OccurrenceStartDate")
    public DateRange occurrenceStartDate;

    @JsonProperty("OccurrenceEndDate")
    public DateRange occurrenceEndDate;

  
    @Override
    public String accept(IGetCriteriaSqlDispatcher dispatcher, BuilderOptions options)
    {
        return dispatcher.getCriteriaSql(this, options);
    }
}
