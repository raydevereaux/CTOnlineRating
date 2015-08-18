package com.bc.ct.beans;

public class Commodity {

	private String code;
	private String desc;
	private String commClass;
    private int weight;
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getCommClass() {
		return commClass;
	}
	public void setCommClass(String commClass) {
		this.commClass = commClass;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
    
	public com.bc.ct.ws.model.RateRequest.Commoditys.Commodity toRateRequestCommodity(){
		com.bc.ct.ws.model.RateRequest.Commoditys.Commodity comm = new com.bc.ct.ws.model.RateRequest.Commoditys.Commodity();
		comm.setCode(this.code);
		comm.setDesc(this.desc);
		comm.setWgt(this.weight);
		return comm;
	}
}
