package clp.models.response.getOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DefaultNic{

	@JsonProperty("net_segment")
	private String netSegment;

	@JsonProperty("subnet")
	private Subnet subnet;

	@JsonProperty("addresses")
	private List<AddressesItem> addresses;

	@JsonProperty("mac_address")
	private String macAddress;

	@JsonProperty("name")
	private String name;

	@JsonProperty("address_assignment")
	private String addressAssignment;

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("mtu")
	private int mtu;

	public String getNetSegment(){
		return netSegment;
	}

	public Subnet getSubnet(){
		return subnet;
	}

	public List<AddressesItem> getAddresses(){
		return addresses;
	}

	public String getMacAddress(){
		return macAddress;
	}

	public String getName(){
		return name;
	}

	public String getAddressAssignment(){
		return addressAssignment;
	}

	public String getUuid(){
		return uuid;
	}

	public int getMtu(){
		return mtu;
	}
}