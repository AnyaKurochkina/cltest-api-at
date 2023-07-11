package models.t1.imageService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @JsonProperty("schema")
    private String schema;

    @JsonProperty("availability_zone")
    private String availabilityZone;

    @JsonProperty("marketing")
    private Marketing marketing;

    private List<Categories> categories;

    @JsonProperty("owner_specified.openstack.object")
    private String ownerSpecifiedOpenstackObject;

    @JsonProperty("owner_specified.openstack.md5")
    private String ownerSpecifiedOpenstackMd5;

    @JsonProperty("min_disk")
    private Integer minDisk;

    @JsonProperty("CIM_PASD_ProcessorArchitecture")
    private String cIMPASDProcessorArchitecture;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("os_hash_value")
    private String osHashValue;

    @JsonProperty("container_format")
    private String containerFormat;

    @JsonProperty("file")
    private String file;

    @JsonProperty("os_type")
    private String osType;

    @JsonProperty("direct_url")
    private String directUrl;

    @JsonProperty("protected")
    private Boolean jsonMemberProtected;

    @JsonProperty("os_hidden")
    private Boolean osHidden;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("checksum")
    private String checksum;

    @JsonProperty("id")
    private String id;

    @JsonProperty("min_ram")
    private Integer minRam;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("visibility")
    private String visibility;

    @JsonProperty("stores")
    private String stores;

    @JsonProperty("internal_id")
    private String internalId;

    @JsonProperty("os_version")
    private String osVersion;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("size")
    private Long size;

    @JsonProperty("name")
    private String name;

    @JsonProperty("disk_format")
    private String diskFormat;

    @JsonProperty("os_hash_algo")
    private String osHashAlgo;

    @JsonProperty("self")
    private String self;

    @JsonProperty("owner_specified.openstack.sha256")
    private String ownerSpecifiedOpenstackSha256;

    @JsonProperty("locations")
    @JsonIgnore
    private List<Object> locations;

    @JsonProperty("os_distro")
    private String osDistro;

    @JsonProperty("virtual_size")
    private Long virtualSize;

    @JsonProperty("status")
    private String status;
}