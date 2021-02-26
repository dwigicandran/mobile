package com.bsms.restobjclient.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Content {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String key;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String prefix;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String desc;
}
