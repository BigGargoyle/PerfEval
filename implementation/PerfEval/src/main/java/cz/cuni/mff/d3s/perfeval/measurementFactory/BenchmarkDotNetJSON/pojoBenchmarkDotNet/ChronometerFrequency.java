package cz.cuni.mff.d3s.perfeval.measurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet;

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
    "Hertz"
})
public class ChronometerFrequency {

    @JsonProperty("Hertz")
    private Integer hertz;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

    @JsonProperty("Hertz")
    public Integer getHertz() {
        return hertz;
    }

    @JsonProperty("Hertz")
    public void setHertz(Integer hertz) {
        this.hertz = hertz;
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
