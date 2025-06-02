package org.example.aqa.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MyProperties {
    private String localhostURL;
    private String datajsonLocation;
    private int secondstowait;
    @JsonProperty("db_url")
    private String dbURL;
    @JsonProperty("db_user")
    private String dbUser;
    @JsonProperty("db_password")
    private String dbPassword;

}