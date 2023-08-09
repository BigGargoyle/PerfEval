package org.example.MeasurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet;
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
    "N",
    "Mean",
    "StandardError",
    "Level",
    "Margin",
    "Lower",
    "Upper"
})
public class ConfidenceInterval {

    @JsonProperty("N")
    private Integer n;
    @JsonProperty("Mean")
    private Double mean;
    @JsonProperty("StandardError")
    private Double standardError;
    @JsonProperty("Level")
    private Integer level;
    @JsonProperty("Margin")
    private Double margin;
    @JsonProperty("Lower")
    private Double lower;
    @JsonProperty("Upper")
    private Double upper;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("N")
    public Integer getN() {
        return n;
    }

    @JsonProperty("N")
    public void setN(Integer n) {
        this.n = n;
    }

    @JsonProperty("Mean")
    public Double getMean() {
        return mean;
    }

    @JsonProperty("Mean")
    public void setMean(Double mean) {
        this.mean = mean;
    }

    @JsonProperty("StandardError")
    public Double getStandardError() {
        return standardError;
    }

    @JsonProperty("StandardError")
    public void setStandardError(Double standardError) {
        this.standardError = standardError;
    }

    @JsonProperty("Level")
    public Integer getLevel() {
        return level;
    }

    @JsonProperty("Level")
    public void setLevel(Integer level) {
        this.level = level;
    }

    @JsonProperty("Margin")
    public Double getMargin() {
        return margin;
    }

    @JsonProperty("Margin")
    public void setMargin(Double margin) {
        this.margin = margin;
    }

    @JsonProperty("Lower")
    public Double getLower() {
        return lower;
    }

    @JsonProperty("Lower")
    public void setLower(Double lower) {
        this.lower = lower;
    }

    @JsonProperty("Upper")
    public Double getUpper() {
        return upper;
    }

    @JsonProperty("Upper")
    public void setUpper(Double upper) {
        this.upper = upper;
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
