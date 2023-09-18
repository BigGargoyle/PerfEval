package org.example.measurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet;

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
    "BenchmarkDotNetCaption",
    "BenchmarkDotNetVersion",
    "OsVersion",
    "ProcessorName",
    "PhysicalProcessorCount",
    "PhysicalCoreCount",
    "LogicalCoreCount",
    "RuntimeVersion",
    "Architecture",
    "HasAttachedDebugger",
    "HasRyuJit",
    "Configuration",
    "DotNetCliVersion",
    "ChronometerFrequency",
    "HardwareTimerKind"
})
public class HostEnvironmentInfo {

    @JsonProperty("BenchmarkDotNetCaption")
    private String benchmarkDotNetCaption;
    @JsonProperty("BenchmarkDotNetVersion")
    private String benchmarkDotNetVersion;
    @JsonProperty("OsVersion")
    private String osVersion;
    @JsonProperty("ProcessorName")
    private String processorName;
    @JsonProperty("PhysicalProcessorCount")
    private Integer physicalProcessorCount;
    @JsonProperty("PhysicalCoreCount")
    private Integer physicalCoreCount;
    @JsonProperty("LogicalCoreCount")
    private Integer logicalCoreCount;
    @JsonProperty("RuntimeVersion")
    private String runtimeVersion;
    @JsonProperty("Architecture")
    private String architecture;
    @JsonProperty("HasAttachedDebugger")
    private Boolean hasAttachedDebugger;
    @JsonProperty("HasRyuJit")
    private Boolean hasRyuJit;
    @JsonProperty("Configuration")
    private String configuration;
    @JsonProperty("DotNetCliVersion")
    private String dotNetCliVersion;
    @JsonProperty("ChronometerFrequency")
    private ChronometerFrequency chronometerFrequency;
    @JsonProperty("HardwareTimerKind")
    private String hardwareTimerKind;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonProperty("BenchmarkDotNetCaption")
    public String getBenchmarkDotNetCaption() {
        return benchmarkDotNetCaption;
    }

    @JsonProperty("BenchmarkDotNetCaption")
    public void setBenchmarkDotNetCaption(String benchmarkDotNetCaption) {
        this.benchmarkDotNetCaption = benchmarkDotNetCaption;
    }

    @JsonProperty("BenchmarkDotNetVersion")
    public String getBenchmarkDotNetVersion() {
        return benchmarkDotNetVersion;
    }

    @JsonProperty("BenchmarkDotNetVersion")
    public void setBenchmarkDotNetVersion(String benchmarkDotNetVersion) {
        this.benchmarkDotNetVersion = benchmarkDotNetVersion;
    }

    @JsonProperty("OsVersion")
    public String getOsVersion() {
        return osVersion;
    }

    @JsonProperty("OsVersion")
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    @JsonProperty("ProcessorName")
    public String getProcessorName() {
        return processorName;
    }

    @JsonProperty("ProcessorName")
    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    @JsonProperty("PhysicalProcessorCount")
    public Integer getPhysicalProcessorCount() {
        return physicalProcessorCount;
    }

    @JsonProperty("PhysicalProcessorCount")
    public void setPhysicalProcessorCount(Integer physicalProcessorCount) {
        this.physicalProcessorCount = physicalProcessorCount;
    }

    @JsonProperty("PhysicalCoreCount")
    public Integer getPhysicalCoreCount() {
        return physicalCoreCount;
    }

    @JsonProperty("PhysicalCoreCount")
    public void setPhysicalCoreCount(Integer physicalCoreCount) {
        this.physicalCoreCount = physicalCoreCount;
    }

    @JsonProperty("LogicalCoreCount")
    public Integer getLogicalCoreCount() {
        return logicalCoreCount;
    }

    @JsonProperty("LogicalCoreCount")
    public void setLogicalCoreCount(Integer logicalCoreCount) {
        this.logicalCoreCount = logicalCoreCount;
    }

    @JsonProperty("RuntimeVersion")
    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    @JsonProperty("RuntimeVersion")
    public void setRuntimeVersion(String runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    @JsonProperty("Architecture")
    public String getArchitecture() {
        return architecture;
    }

    @JsonProperty("Architecture")
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    @JsonProperty("HasAttachedDebugger")
    public Boolean getHasAttachedDebugger() {
        return hasAttachedDebugger;
    }

    @JsonProperty("HasAttachedDebugger")
    public void setHasAttachedDebugger(Boolean hasAttachedDebugger) {
        this.hasAttachedDebugger = hasAttachedDebugger;
    }

    @JsonProperty("HasRyuJit")
    public Boolean getHasRyuJit() {
        return hasRyuJit;
    }

    @JsonProperty("HasRyuJit")
    public void setHasRyuJit(Boolean hasRyuJit) {
        this.hasRyuJit = hasRyuJit;
    }

    @JsonProperty("Configuration")
    public String getConfiguration() {
        return configuration;
    }

    @JsonProperty("Configuration")
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    @JsonProperty("DotNetCliVersion")
    public String getDotNetCliVersion() {
        return dotNetCliVersion;
    }

    @JsonProperty("DotNetCliVersion")
    public void setDotNetCliVersion(String dotNetCliVersion) {
        this.dotNetCliVersion = dotNetCliVersion;
    }

    @JsonProperty("ChronometerFrequency")
    public ChronometerFrequency getChronometerFrequency() {
        return chronometerFrequency;
    }

    @JsonProperty("ChronometerFrequency")
    public void setChronometerFrequency(ChronometerFrequency chronometerFrequency) {
        this.chronometerFrequency = chronometerFrequency;
    }

    @JsonProperty("HardwareTimerKind")
    public String getHardwareTimerKind() {
        return hardwareTimerKind;
    }

    @JsonProperty("HardwareTimerKind")
    public void setHardwareTimerKind(String hardwareTimerKind) {
        this.hardwareTimerKind = hardwareTimerKind;
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
