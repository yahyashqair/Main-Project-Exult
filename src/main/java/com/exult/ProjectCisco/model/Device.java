package com.exult.ProjectCisco.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
public @Data
class Device {

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "device_Configuration", joinColumns = @JoinColumn(name = "device_id"))
    Map<String, String> configurations = new HashMap<String, String>(); // maps from attribute name to value

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("CLI_ADDRESS")
    private String cliAddress;

    @JsonProperty("CLI_LOGIN_USERNAME")
    private String cliLoginUsername;

    @JsonProperty("CLI_LOGIN_PASSWORD")
    private String cliLoginPassword;

    @JsonProperty("CLI_PORT")
    private String cliPort;

    @JsonProperty("CLI_TRANSPORT")
    private String cliTransport;

    @JsonProperty("cli_enable_password")
    private String cliEnablePassword;

    @JsonProperty("snmp_READ_CS")
    private String snmpReadCs;

    @JsonProperty("snmpPort")
    private String snmpPort;

    @Column(name = "local_date_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime localDateTime;

    @JsonProperty(access = Access.READ_ONLY)
    @ManyToMany
    private List<Profile> profileSet = new ArrayList<>();

}
