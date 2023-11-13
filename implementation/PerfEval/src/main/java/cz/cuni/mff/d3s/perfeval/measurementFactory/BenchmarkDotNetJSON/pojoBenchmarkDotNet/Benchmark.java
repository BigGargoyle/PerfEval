package cz.cuni.mff.d3s.perfeval.measurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DisplayInfo",
    "Namespace",
    "Type",
    "Method",
    "MethodTitle",
    "Parameters",
    "FullName",
    "HardwareIntrinsics",
    "Statistics",
    "Measurements"
})
public class Benchmark {

    @JsonProperty("DisplayInfo")
    private String displayInfo;
    @JsonProperty("Namespace")
    private String namespace;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Method")
    private String method;
    @JsonProperty("MethodTitle")
    private String methodTitle;
    @JsonProperty("Parameters")
    private String parameters;
    @JsonProperty("FullName")
    private String fullName;
    @JsonProperty("HardwareIntrinsics")
    private String hardwareIntrinsics;
    @JsonProperty("Statistics")
    private Statistics statistics;
    @JsonProperty("Measurements")
    private List<Measurement> measurements;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonProperty("DisplayInfo")
    public String getDisplayInfo() {
        return displayInfo;
    }

    @JsonProperty("DisplayInfo")
    public void setDisplayInfo(String displayInfo) {
        this.displayInfo = displayInfo;
    }

    @JsonProperty("Namespace")
    public String getNamespace() {
        return namespace;
    }

    @JsonProperty("Namespace")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonProperty("Type")
    public String getType() {
        return type;
    }

    @JsonProperty("Type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("Method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("Method")
    public void setMethod(String method) {
        this.method = method;
    }

    @JsonProperty("MethodTitle")
    public String getMethodTitle() {
        return methodTitle;
    }

    @JsonProperty("MethodTitle")
    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    @JsonProperty("Parameters")
    public String getParameters() {
        return parameters;
    }

    @JsonProperty("Parameters")
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @JsonProperty("FullName")
    public String getFullName() {
        return fullName;
    }

    @JsonProperty("FullName")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonProperty("HardwareIntrinsics")
    public String getHardwareIntrinsics() {
        return hardwareIntrinsics;
    }

    @JsonProperty("HardwareIntrinsics")
    public void setHardwareIntrinsics(String hardwareIntrinsics) {
        this.hardwareIntrinsics = hardwareIntrinsics;
    }

    @JsonProperty("Statistics")
    public Statistics getStatistics() {
        return statistics;
    }

    @JsonProperty("Statistics")
    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    @JsonProperty("Measurements")
    public List<Measurement> getMeasurements() {
        return measurements;
    }

    @JsonProperty("Measurements")
    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
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
