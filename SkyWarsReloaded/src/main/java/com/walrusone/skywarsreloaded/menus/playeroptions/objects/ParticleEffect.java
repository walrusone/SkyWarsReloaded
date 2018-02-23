package com.walrusone.skywarsreloaded.menus.playeroptions.objects;

public class ParticleEffect {
	private String type;
	private float offsetYL;
	private float offsetYU;
	private float data;
	private int amountU;
	private int amountL;
	
	public ParticleEffect(String type, float offsetYL, float offsetYU, float data, int amountU, int amountL) {
		this.setType(type);
		this.setOffsetYL(offsetYL);
		this.setOffsetYU(offsetYU);
		this.setData(data);
		this.setAmountL(amountL);
		this.setAmountU(amountU);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getData() {
		return data;
	}

	public void setData(float data) {
		this.data = data;
	}

	public float getOffsetYL() {
		return offsetYL;
	}

	public void setOffsetYL(float offsetYL) {
		this.offsetYL = offsetYL;
	}

	public float getOffsetYU() {
		return offsetYU;
	}

	public void setOffsetYU(float offsetYU) {
		this.offsetYU = offsetYU;
	}

	public int getAmountU() {
		return amountU;
	}

	public void setAmountU(int amountU) {
		this.amountU = amountU;
	}

	public int getAmountL() {
		return amountL;
	}

	public void setAmountL(int amountL) {
		this.amountL = amountL;
	}
}
