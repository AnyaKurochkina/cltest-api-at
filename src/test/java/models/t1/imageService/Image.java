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

    @JsonProperty("architecture")
    private String architecture;

    @JsonProperty("os_hash_value")
    private String osHashValue;

    @JsonProperty("container_format")
    private String containerFormat;

    @JsonProperty("file")
    private String file;

    @JsonProperty("type")
    private String type;

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

    private String vmware_ostype;
    private String description;

    private String cinder_img_volume_type;

    @JsonProperty("internal_id")
    private String internalId;

    @JsonProperty("os_version")
    private String osVersion;

    @JsonProperty("hw_disk_bus")
    private String hw_disk_bus;

    @JsonProperty("hw_pmu")
    private String hw_pmu;

    @JsonProperty("hw_machine_type")
    private String hw_machine_type;

    @JsonProperty("hw_qemu_guest_agent")
    private String hw_qemu_guest_agent;

    @JsonProperty("hw_rng_model")
    private String hw_rng_model;

    @JsonProperty("hw_scsi_model")
    private String hw_scsi_model;

    @JsonProperty("hw_time_hpet")
    private String hw_time_hpet;

    @JsonProperty("hw_video_model")
    private String hw_video_model;

    @JsonProperty("hw_vif_model")
    private String hw_vif_model;

    @JsonProperty("os_require_quiesce")
    private String os_require_quiesce;

    @JsonProperty("hw_vif_multiqueue_enabled")
    private String hw_vif_multiqueue_enabled;

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