package org.ohdsi.circe.cohortdefinition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.ohdsi.analysis.versioning.CdmVersion;
import org.ohdsi.circe.cohortdefinition.builders.BuilderOptions;

@JsonTypeName("CareSite")
@CdmVersion(range = ">=6.1")
public class CareSite extends GeoCriteria {

    @JsonProperty("CodesetId")
    public Integer codesetId;

    @Override
    public String accept(IGetCriteriaSqlDispatcher dispatcher, BuilderOptions options)
    {
        return dispatcher.getCriteriaSql(this, options);
    }
}
