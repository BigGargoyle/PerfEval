package org.example.MeasurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet;

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
    "Title",
    "HostEnvironmentInfo",
    "Benchmarks"
})
public class BenchmarkDotNetJSONBase {

    @JsonProperty("Title")
    private String title;
    @JsonProperty("HostEnvironmentInfo")
    private HostEnvironmentInfo hostEnvironmentInfo;
    @JsonProperty("Benchmarks")
    private List<Benchmark> benchmarks;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("Title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("Title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("HostEnvironmentInfo")
    public HostEnvironmentInfo getHostEnvironmentInfo() {
        return hostEnvironmentInfo;
    }

    @JsonProperty("HostEnvironmentInfo")
    public void setHostEnvironmentInfo(HostEnvironmentInfo hostEnvironmentInfo) {
        this.hostEnvironmentInfo = hostEnvironmentInfo;
    }

    @JsonProperty("Benchmarks")
    public List<Benchmark> getBenchmarks() {
        return benchmarks;
    }

    @JsonProperty("Benchmarks")
    public void setBenchmarks(List<Benchmark> benchmarks) {
        this.benchmarks = benchmarks;
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
