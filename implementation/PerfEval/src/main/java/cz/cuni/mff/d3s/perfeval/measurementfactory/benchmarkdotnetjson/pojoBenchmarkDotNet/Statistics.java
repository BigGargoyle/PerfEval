package cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet;

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
    "OriginalValues",
    "N",
    "Min",
    "LowerFence",
    "Q1",
    "Median",
    "Mean",
    "Q3",
    "UpperFence",
    "Max",
    "InterquartileRange",
    "LowerOutliers",
    "UpperOutliers",
    "AllOutliers",
    "StandardError",
    "Variance",
    "StandardDeviation",
    "Skewness",
    "Kurtosis",
    "ConfidenceInterval",
    "Percentiles"
})
public class Statistics {

    @JsonProperty("OriginalValues")
    private List<Double> originalValues;
    @JsonProperty("N")
    private Integer n;
    @JsonProperty("Min")
    private Double min;
    @JsonProperty("LowerFence")
    private Double lowerFence;
    @JsonProperty("Q1")
    private Double q1;
    @JsonProperty("Median")
    private Double median;
    @JsonProperty("Mean")
    private Double mean;
    @JsonProperty("Q3")
    private Double q3;
    @JsonProperty("UpperFence")
    private Double upperFence;
    @JsonProperty("Max")
    private Double max;
    @JsonProperty("InterquartileRange")
    private Double interquartileRange;
    @JsonProperty("LowerOutliers")
    private List<Object> lowerOutliers;
    @JsonProperty("UpperOutliers")
    private List<Double> upperOutliers;
    @JsonProperty("AllOutliers")
    private List<Double> allOutliers;
    @JsonProperty("StandardError")
    private Double standardError;
    @JsonProperty("Variance")
    private Double variance;
    @JsonProperty("StandardDeviation")
    private Double standardDeviation;
    @JsonProperty("Skewness")
    private Double skewness;
    @JsonProperty("Kurtosis")
    private Double kurtosis;
    @JsonProperty("ConfidenceInterval")
    private ConfidenceInterval confidenceInterval;
    @JsonProperty("Percentiles")
    private Percentiles percentiles;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonProperty("OriginalValues")
    public List<Double> getOriginalValues() {
        return originalValues;
    }

    @JsonProperty("OriginalValues")
    public void setOriginalValues(List<Double> originalValues) {
        this.originalValues = originalValues;
    }

    @JsonProperty("N")
    public Integer getN() {
        return n;
    }

    @JsonProperty("N")
    public void setN(Integer n) {
        this.n = n;
    }

    @JsonProperty("Min")
    public Double getMin() {
        return min;
    }

    @JsonProperty("Min")
    public void setMin(Double min) {
        this.min = min;
    }

    @JsonProperty("LowerFence")
    public Double getLowerFence() {
        return lowerFence;
    }

    @JsonProperty("LowerFence")
    public void setLowerFence(Double lowerFence) {
        this.lowerFence = lowerFence;
    }

    @JsonProperty("Q1")
    public Double getQ1() {
        return q1;
    }

    @JsonProperty("Q1")
    public void setQ1(Double q1) {
        this.q1 = q1;
    }

    @JsonProperty("Median")
    public Double getMedian() {
        return median;
    }

    @JsonProperty("Median")
    public void setMedian(Double median) {
        this.median = median;
    }

    @JsonProperty("Mean")
    public Double getMean() {
        return mean;
    }

    @JsonProperty("Mean")
    public void setMean(Double mean) {
        this.mean = mean;
    }

    @JsonProperty("Q3")
    public Double getQ3() {
        return q3;
    }

    @JsonProperty("Q3")
    public void setQ3(Double q3) {
        this.q3 = q3;
    }

    @JsonProperty("UpperFence")
    public Double getUpperFence() {
        return upperFence;
    }

    @JsonProperty("UpperFence")
    public void setUpperFence(Double upperFence) {
        this.upperFence = upperFence;
    }

    @JsonProperty("Max")
    public Double getMax() {
        return max;
    }

    @JsonProperty("Max")
    public void setMax(Double max) {
        this.max = max;
    }

    @JsonProperty("InterquartileRange")
    public Double getInterquartileRange() {
        return interquartileRange;
    }

    @JsonProperty("InterquartileRange")
    public void setInterquartileRange(Double interquartileRange) {
        this.interquartileRange = interquartileRange;
    }

    @JsonProperty("LowerOutliers")
    public List<Object> getLowerOutliers() {
        return lowerOutliers;
    }

    @JsonProperty("LowerOutliers")
    public void setLowerOutliers(List<Object> lowerOutliers) {
        this.lowerOutliers = lowerOutliers;
    }

    @JsonProperty("UpperOutliers")
    public List<Double> getUpperOutliers() {
        return upperOutliers;
    }

    @JsonProperty("UpperOutliers")
    public void setUpperOutliers(List<Double> upperOutliers) {
        this.upperOutliers = upperOutliers;
    }

    @JsonProperty("AllOutliers")
    public List<Double> getAllOutliers() {
        return allOutliers;
    }

    @JsonProperty("AllOutliers")
    public void setAllOutliers(List<Double> allOutliers) {
        this.allOutliers = allOutliers;
    }

    @JsonProperty("StandardError")
    public Double getStandardError() {
        return standardError;
    }

    @JsonProperty("StandardError")
    public void setStandardError(Double standardError) {
        this.standardError = standardError;
    }

    @JsonProperty("Variance")
    public Double getVariance() {
        return variance;
    }

    @JsonProperty("Variance")
    public void setVariance(Double variance) {
        this.variance = variance;
    }

    @JsonProperty("StandardDeviation")
    public Double getStandardDeviation() {
        return standardDeviation;
    }

    @JsonProperty("StandardDeviation")
    public void setStandardDeviation(Double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    @JsonProperty("Skewness")
    public Double getSkewness() {
        return skewness;
    }

    @JsonProperty("Skewness")
    public void setSkewness(Double skewness) {
        this.skewness = skewness;
    }

    @JsonProperty("Kurtosis")
    public Double getKurtosis() {
        return kurtosis;
    }

    @JsonProperty("Kurtosis")
    public void setKurtosis(Double kurtosis) {
        this.kurtosis = kurtosis;
    }

    @JsonProperty("ConfidenceInterval")
    public ConfidenceInterval getConfidenceInterval() {
        return confidenceInterval;
    }

    @JsonProperty("ConfidenceInterval")
    public void setConfidenceInterval(ConfidenceInterval confidenceInterval) {
        this.confidenceInterval = confidenceInterval;
    }

    @JsonProperty("Percentiles")
    public Percentiles getPercentiles() {
        return percentiles;
    }

    @JsonProperty("Percentiles")
    public void setPercentiles(Percentiles percentiles) {
        this.percentiles = percentiles;
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
