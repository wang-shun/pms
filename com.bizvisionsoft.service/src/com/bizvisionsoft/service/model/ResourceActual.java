package com.bizvisionsoft.service.model;

import java.util.Date;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.mongocodex.Persistence;
import com.bizvisionsoft.annotations.md.mongocodex.PersistenceCollection;
import com.bizvisionsoft.annotations.md.mongocodex.SetValue;
import com.bizvisionsoft.annotations.md.service.ImageURL;
import com.bizvisionsoft.annotations.md.service.Label;
import com.bizvisionsoft.annotations.md.service.ReadValue;

@PersistenceCollection("resourceActual")
public class ResourceActual {

	@ReadValue
	private ObjectId _id;

	@ReadValue
	@Persistence
	private ObjectId work_id;

	public ObjectId getWork_id() {
		return work_id;
	}

	@ReadValue({ "type", ReadValue.TYPE })
	@SetValue
	private String type;

	@ReadValue
	@SetValue
	private String name;

	@ReadValue
	@SetValue
	private String resId;

	@Persistence
	private String usedHumanResId;

	@Persistence
	private String usedEquipResId;

	@Persistence
	private String usedTypedResId;

	@SetValue
	private ResourceType resType;

	@ReadValue("basicRate")
	private double getBasicRate() {
		return resType.getBasicRate();
	}

	@ReadValue("overtimeRate")
	private double getOvertimeRate() {
		return resType.getOvertimeRate();
	}

	@ReadValue
	private double actualBasicQty;

	@ReadValue
	@SetValue
	private double planBasicQty;

	public void setActualBasicQty(double actualBasicQty) {
		this.actualBasicQty = actualBasicQty;
	}

	@ReadValue
	private double actualOverTimeQty;

	@ReadValue
	@SetValue
	private double planOverTimeQty;

	@Persistence
	private ObjectId resTypeId;

	@Persistence
	private Date id;

	@ReadValue("actualAmount")
	private Double getActualAmount() {
		return getBasicRate() * actualBasicQty + getOvertimeRate() * actualOverTimeQty;
	}

	@ReadValue("planAmount")
	private Double getPlanAmount() {
		return getBasicRate() * planBasicQty + getOvertimeRate() * planOverTimeQty;
	}

	@Override
	@Label
	public String toString() {
		return name + " [" + resId + "]";
	}

	@ImageURL("resId")
	private String getLogo() {
		if ("人力资源".equals(type))
			return "/img/user_c.svg";
		else if ("设备设施".equals(type))
			return "/img/equipment_c.svg";
		return "/img/resource_c.svg";
	}

	public ResourceActual setWork_id(ObjectId work_id) {
		this.work_id = work_id;
		return this;
	}

	public ObjectId get_id() {
		return _id;
	}

	public ResourceActual setUsedHumanResId(String usedHumanResId) {
		this.usedHumanResId = usedHumanResId;
		return this;
	}

	public ResourceActual setUsedEquipResId(String usedEquipResId) {
		this.usedEquipResId = usedEquipResId;
		return this;
	}

	public ResourceActual setUsedTypedResId(String usedTypedResId) {
		this.usedTypedResId = usedTypedResId;
		return this;
	}

	public ResourceActual setResTypeId(ObjectId resTypeId) {
		this.resTypeId = resTypeId;
		return this;
	}

	public void setId(Date id) {
		this.id = id;
	}

	public void setActualOverTimeQty(double actualOverTimeQty) {
		this.actualOverTimeQty = actualOverTimeQty;
	}

	public String getUsedHumanResId() {
		return usedHumanResId;
	}

	public String getUsedEquipResId() {
		return usedEquipResId;
	}

	public String getUsedTypedResId() {
		return usedTypedResId;
	}

	public ObjectId getResTypeId() {
		return resTypeId;
	}

}
