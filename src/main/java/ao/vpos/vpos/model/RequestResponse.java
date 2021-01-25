package ao.vpos.vpos.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestResponse{

	@JsonProperty("eta")
	private double eta;

	@JsonProperty("inserted_at")
	private String insertedAt;

	public double getEta(){
		return eta;
	}

	public String getInsertedAt(){
		return insertedAt;
	}
}