package cz.cuni.mff.hrdydo.measurementFactory.JMHJSON.pojoJMH;

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
    "score",
    "scoreError",
    "scoreConfidence",
    "scorePercentiles",
    "scoreUnit",
    "rawData"
})
public class PrimaryMetric {

    @JsonProperty("score")
    private Double score;
    @JsonProperty("scoreError")
    private Double scoreError;
    @JsonProperty("scoreConfidence")
    private List<Double> scoreConfidence;
    @JsonProperty("scorePercentiles")
    private ScorePercentiles scorePercentiles;
    @JsonProperty("scoreUnit")
    private String scoreUnit;
    @JsonProperty("rawData")
    private List<List<Double>> rawData;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonProperty("score")
    public Double getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(Double score) {
        this.score = score;
    }

    @JsonProperty("scoreError")
    public Double getScoreError() {
        return scoreError;
    }

    @JsonProperty("scoreError")
    public void setScoreError(Double scoreError) {
        this.scoreError = scoreError;
    }

    @JsonProperty("scoreConfidence")
    public List<Double> getScoreConfidence() {
        return scoreConfidence;
    }

    @JsonProperty("scoreConfidence")
    public void setScoreConfidence(List<Double> scoreConfidence) {
        this.scoreConfidence = scoreConfidence;
    }

    @JsonProperty("scorePercentiles")
    public ScorePercentiles getScorePercentiles() {
        return scorePercentiles;
    }

    @JsonProperty("scorePercentiles")
    public void setScorePercentiles(ScorePercentiles scorePercentiles) {
        this.scorePercentiles = scorePercentiles;
    }

    @JsonProperty("scoreUnit")
    public String getScoreUnit() {
        return scoreUnit;
    }

    @JsonProperty("scoreUnit")
    public void setScoreUnit(String scoreUnit) {
        this.scoreUnit = scoreUnit;
    }

    @JsonProperty("rawData")
    public List<List<Double>> getRawData() {
        return rawData;
    }

    @JsonProperty("rawData")
    public void setRawData(List<List<Double>> rawData) {
        this.rawData = rawData;
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
