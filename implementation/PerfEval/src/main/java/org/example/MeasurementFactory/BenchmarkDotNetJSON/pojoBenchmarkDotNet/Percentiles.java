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
    "P0",
    "P25",
    "P50",
    "P67",
    "P80",
    "P85",
    "P90",
    "P95",
    "P100"
})
public class Percentiles {

    @JsonProperty("P0")
    private Double p0;
    @JsonProperty("P25")
    private Double p25;
    @JsonProperty("P50")
    private Double p50;
    @JsonProperty("P67")
    private Double p67;
    @JsonProperty("P80")
    private Double p80;
    @JsonProperty("P85")
    private Double p85;
    @JsonProperty("P90")
    private Double p90;
    @JsonProperty("P95")
    private Double p95;
    @JsonProperty("P100")
    private Double p100;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonProperty("P0")
    public Double getP0() {
        return p0;
    }

    @JsonProperty("P0")
    public void setP0(Double p0) {
        this.p0 = p0;
    }

    @JsonProperty("P25")
    public Double getP25() {
        return p25;
    }

    @JsonProperty("P25")
    public void setP25(Double p25) {
        this.p25 = p25;
    }

    @JsonProperty("P50")
    public Double getP50() {
        return p50;
    }

    @JsonProperty("P50")
    public void setP50(Double p50) {
        this.p50 = p50;
    }

    @JsonProperty("P67")
    public Double getP67() {
        return p67;
    }

    @JsonProperty("P67")
    public void setP67(Double p67) {
        this.p67 = p67;
    }

    @JsonProperty("P80")
    public Double getP80() {
        return p80;
    }

    @JsonProperty("P80")
    public void setP80(Double p80) {
        this.p80 = p80;
    }

    @JsonProperty("P85")
    public Double getP85() {
        return p85;
    }

    @JsonProperty("P85")
    public void setP85(Double p85) {
        this.p85 = p85;
    }

    @JsonProperty("P90")
    public Double getP90() {
        return p90;
    }

    @JsonProperty("P90")
    public void setP90(Double p90) {
        this.p90 = p90;
    }

    @JsonProperty("P95")
    public Double getP95() {
        return p95;
    }

    @JsonProperty("P95")
    public void setP95(Double p95) {
        this.p95 = p95;
    }

    @JsonProperty("P100")
    public Double getP100() {
        return p100;
    }

    @JsonProperty("P100")
    public void setP100(Double p100) {
        this.p100 = p100;
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
