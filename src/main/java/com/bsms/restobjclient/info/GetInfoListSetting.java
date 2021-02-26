package com.bsms.restobjclient.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
public class GetInfoListSetting {

    @JsonProperty("islamic.gold.price")
    @SerializedName("islamic.gold.price")
    private String islamicGoldPrice;
    private String islamicMenuVmap;
    @JsonProperty("islamic.rice.price")
    @SerializedName("islamic.rice.price")
    private String islamicRicePrice;
    private String islamicYoutubeId;

}
