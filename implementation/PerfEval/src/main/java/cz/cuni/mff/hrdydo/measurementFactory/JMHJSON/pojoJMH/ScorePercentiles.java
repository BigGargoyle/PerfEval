package cz.cuni.mff.hrdydo.measurementFactory.JMHJSON.pojoJMH;

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
    "0.0",
    "50.0",
    "90.0",
    "95.0",
    "99.0",
    "99.9",
    "99.99",
    "99.999",
    "99.9999",
    "100.0"
})
public class ScorePercentiles {

    @JsonProperty("0.0")
    private Double _00;
    @JsonProperty("50.0")
    private Double _500;
    @JsonProperty("90.0")
    private Double _900;
    @JsonProperty("95.0")
    private Double _950;
    @JsonProperty("99.0")
    private Double _990;
    @JsonProperty("99.9")
    private Double _999;
    @JsonProperty("99.99")
    private Double _9999;
    @JsonProperty("99.999")
    private Double _99999;
    @JsonProperty("99.9999")
    private Double _999999;
    @JsonProperty("100.0")
    private Double _1000;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonProperty("0.0")
    public Double get00() {
        return _00;
    }

    @JsonProperty("0.0")
    public void set00(Double _00) {
        this._00 = _00;
    }

    @JsonProperty("50.0")
    public Double get500() {
        return _500;
    }

    @JsonProperty("50.0")
    public void set500(Double _500) {
        this._500 = _500;
    }

    @JsonProperty("90.0")
    public Double get900() {
        return _900;
    }

    @JsonProperty("90.0")
    public void set900(Double _900) {
        this._900 = _900;
    }

    @JsonProperty("95.0")
    public Double get950() {
        return _950;
    }

    @JsonProperty("95.0")
    public void set950(Double _950) {
        this._950 = _950;
    }

    @JsonProperty("99.0")
    public Double get990() {
        return _990;
    }

    @JsonProperty("99.0")
    public void set990(Double _990) {
        this._990 = _990;
    }

    @JsonProperty("99.9")
    public Double get999() {
        return _999;
    }

    @JsonProperty("99.9")
    public void set999(Double _999) {
        this._999 = _999;
    }

    @JsonProperty("99.99")
    public Double get9999() {
        return _9999;
    }

    @JsonProperty("99.99")
    public void set9999(Double _9999) {
        this._9999 = _9999;
    }

    @JsonProperty("99.999")
    public Double get99999() {
        return _99999;
    }

    @JsonProperty("99.999")
    public void set99999(Double _99999) {
        this._99999 = _99999;
    }

    @JsonProperty("99.9999")
    public Double get999999() {
        return _999999;
    }

    @JsonProperty("99.9999")
    public void set999999(Double _999999) {
        this._999999 = _999999;
    }

    @JsonProperty("100.0")
    public Double get1000() {
        return _1000;
    }

    @JsonProperty("100.0")
    public void set1000(Double _1000) {
        this._1000 = _1000;
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
