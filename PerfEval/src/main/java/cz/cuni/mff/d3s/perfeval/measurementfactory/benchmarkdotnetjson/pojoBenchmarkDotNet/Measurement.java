
package cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "IterationMode",
    "IterationStage",
    "LaunchIndex",
    "IterationIndex",
    "Operations",
    "Nanoseconds"
})
public class Measurement {

    @JsonProperty("IterationMode")
    private String iterationMode;
    @JsonProperty("IterationStage")
    private String iterationStage;
    @JsonProperty("LaunchIndex")
    private Integer launchIndex;
    @JsonProperty("IterationIndex")
    private Integer iterationIndex;
    @JsonProperty("Operations")
    private Integer operations;
    @JsonProperty("Nanoseconds")
    private Integer nanoseconds;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("IterationMode")
    public String getIterationMode() {
        return iterationMode;
    }

    @JsonProperty("IterationMode")
    public void setIterationMode(String iterationMode) {
        this.iterationMode = iterationMode;
    }

    @JsonProperty("IterationStage")
    public String getIterationStage() {
        return iterationStage;
    }

    @JsonProperty("IterationStage")
    public void setIterationStage(String iterationStage) {
        this.iterationStage = iterationStage;
    }

    @JsonProperty("LaunchIndex")
    public Integer getLaunchIndex() {
        return launchIndex;
    }

    @JsonProperty("LaunchIndex")
    public void setLaunchIndex(Integer launchIndex) {
        this.launchIndex = launchIndex;
    }

    @JsonProperty("IterationIndex")
    public Integer getIterationIndex() {
        return iterationIndex;
    }

    @JsonProperty("IterationIndex")
    public void setIterationIndex(Integer iterationIndex) {
        this.iterationIndex = iterationIndex;
    }

    @JsonProperty("Operations")
    public Integer getOperations() {
        return operations;
    }

    @JsonProperty("Operations")
    public void setOperations(Integer operations) {
        this.operations = operations;
    }

    @JsonProperty("Nanoseconds")
    public Integer getNanoseconds() {
        return nanoseconds;
    }

    @JsonProperty("Nanoseconds")
    public void setNanoseconds(Integer nanoseconds) {
        this.nanoseconds = nanoseconds;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
