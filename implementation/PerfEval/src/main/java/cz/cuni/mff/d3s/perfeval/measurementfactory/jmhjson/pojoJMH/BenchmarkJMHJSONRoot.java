package cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.pojoJMH;

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
    "jmhVersion",
    "benchmark",
    "mode",
    "threads",
    "forks",
    "jvm",
    "jvmArgs",
    "jdkVersion",
    "vmName",
    "vmVersion",
    "warmupIterations",
    "warmupTime",
    "warmupBatchSize",
    "measurementIterations",
    "measurementTime",
    "measurementBatchSize",
    "primaryMetric",
    "secondaryMetrics"
})
public class BenchmarkJMHJSONRoot {

    @JsonProperty("jmhVersion")
    private String jmhVersion;
    @JsonProperty("benchmark")
    private String benchmark;
    @JsonProperty("mode")
    private String mode;
    @JsonProperty("threads")
    private Integer threads;
    @JsonProperty("forks")
    private Integer forks;
    @JsonProperty("jvm")
    private String jvm;
    @JsonProperty("jvmArgs")
    private List<Object> jvmArgs;
    @JsonProperty("jdkVersion")
    private String jdkVersion;
    @JsonProperty("vmName")
    private String vmName;
    @JsonProperty("vmVersion")
    private String vmVersion;
    @JsonProperty("warmupIterations")
    private Integer warmupIterations;
    @JsonProperty("warmupTime")
    private String warmupTime;
    @JsonProperty("warmupBatchSize")
    private Integer warmupBatchSize;
    @JsonProperty("measurementIterations")
    private Integer measurementIterations;
    @JsonProperty("measurementTime")
    private String measurementTime;
    @JsonProperty("measurementBatchSize")
    private Integer measurementBatchSize;
    @JsonProperty("primaryMetric")
    private PrimaryMetric primaryMetric;
    @JsonProperty("secondaryMetrics")
    private SecondaryMetrics secondaryMetrics;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonProperty("jmhVersion")
    public String getJmhVersion() {
        return jmhVersion;
    }

    @JsonProperty("jmhVersion")
    public void setJmhVersion(String jmhVersion) {
        this.jmhVersion = jmhVersion;
    }

    @JsonProperty("benchmark")
    public String getBenchmark() {
        return benchmark;
    }

    @JsonProperty("benchmark")
    public void setBenchmark(String benchmark) {
        this.benchmark = benchmark;
    }

    @JsonProperty("mode")
    public String getMode() {
        return mode;
    }

    @JsonProperty("mode")
    public void setMode(String mode) {
        this.mode = mode;
    }

    @JsonProperty("threads")
    public Integer getThreads() {
        return threads;
    }

    @JsonProperty("threads")
    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    @JsonProperty("forks")
    public Integer getForks() {
        return forks;
    }

    @JsonProperty("forks")
    public void setForks(Integer forks) {
        this.forks = forks;
    }

    @JsonProperty("jvm")
    public String getJvm() {
        return jvm;
    }

    @JsonProperty("jvm")
    public void setJvm(String jvm) {
        this.jvm = jvm;
    }

    @JsonProperty("jvmArgs")
    public List<Object> getJvmArgs() {
        return jvmArgs;
    }

    @JsonProperty("jvmArgs")
    public void setJvmArgs(List<Object> jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    @JsonProperty("jdkVersion")
    public String getJdkVersion() {
        return jdkVersion;
    }

    @JsonProperty("jdkVersion")
    public void setJdkVersion(String jdkVersion) {
        this.jdkVersion = jdkVersion;
    }

    @JsonProperty("vmName")
    public String getVmName() {
        return vmName;
    }

    @JsonProperty("vmName")
    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    @JsonProperty("vmVersion")
    public String getVmVersion() {
        return vmVersion;
    }

    @JsonProperty("vmVersion")
    public void setVmVersion(String vmVersion) {
        this.vmVersion = vmVersion;
    }

    @JsonProperty("warmupIterations")
    public Integer getWarmupIterations() {
        return warmupIterations;
    }

    @JsonProperty("warmupIterations")
    public void setWarmupIterations(Integer warmupIterations) {
        this.warmupIterations = warmupIterations;
    }

    @JsonProperty("warmupTime")
    public String getWarmupTime() {
        return warmupTime;
    }

    @JsonProperty("warmupTime")
    public void setWarmupTime(String warmupTime) {
        this.warmupTime = warmupTime;
    }

    @JsonProperty("warmupBatchSize")
    public Integer getWarmupBatchSize() {
        return warmupBatchSize;
    }

    @JsonProperty("warmupBatchSize")
    public void setWarmupBatchSize(Integer warmupBatchSize) {
        this.warmupBatchSize = warmupBatchSize;
    }

    @JsonProperty("measurementIterations")
    public Integer getMeasurementIterations() {
        return measurementIterations;
    }

    @JsonProperty("measurementIterations")
    public void setMeasurementIterations(Integer measurementIterations) {
        this.measurementIterations = measurementIterations;
    }

    @JsonProperty("measurementTime")
    public String getMeasurementTime() {
        return measurementTime;
    }

    @JsonProperty("measurementTime")
    public void setMeasurementTime(String measurementTime) {
        this.measurementTime = measurementTime;
    }

    @JsonProperty("measurementBatchSize")
    public Integer getMeasurementBatchSize() {
        return measurementBatchSize;
    }

    @JsonProperty("measurementBatchSize")
    public void setMeasurementBatchSize(Integer measurementBatchSize) {
        this.measurementBatchSize = measurementBatchSize;
    }

    @JsonProperty("primaryMetric")
    public PrimaryMetric getPrimaryMetric() {
        return primaryMetric;
    }

    @JsonProperty("primaryMetric")
    public void setPrimaryMetric(PrimaryMetric primaryMetric) {
        this.primaryMetric = primaryMetric;
    }

    @JsonProperty("secondaryMetrics")
    public SecondaryMetrics getSecondaryMetrics() {
        return secondaryMetrics;
    }

    @JsonProperty("secondaryMetrics")
    public void setSecondaryMetrics(SecondaryMetrics secondaryMetrics) {
        this.secondaryMetrics = secondaryMetrics;
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
