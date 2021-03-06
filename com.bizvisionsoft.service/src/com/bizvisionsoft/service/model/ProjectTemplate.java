package com.bizvisionsoft.service.model;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.mongocodex.GetValue;
import com.bizvisionsoft.annotations.md.mongocodex.Persistence;
import com.bizvisionsoft.annotations.md.mongocodex.PersistenceCollection;
import com.bizvisionsoft.annotations.md.mongocodex.SetValue;
import com.bizvisionsoft.annotations.md.mongocodex.Strict;
import com.bizvisionsoft.annotations.md.service.Label;
import com.bizvisionsoft.annotations.md.service.ReadValue;
import com.bizvisionsoft.annotations.md.service.WriteValue;

/**
 * 项目模板，用于创建和编辑
 * 
 * @author hua
 *
 */
@Strict
@PersistenceCollection("projectTemplate")
public class ProjectTemplate{

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 标识属性
	/**
	 * _id
	 */
	@SetValue
	@GetValue
	private ObjectId _id;

	/**
	 * 编号
	 */
	@ReadValue
	@Label(Label.ID_LABEL)
	@WriteValue
	@Persistence
	private String id;

	@ReadValue
	@WriteValue
	@Persistence
	private boolean enabled;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 描述属性
	/**
	 * 名称
	 */
	@ReadValue
	@WriteValue
	@Persistence
	@Label(Label.NAME_LABEL)
	private String name;

	/**
	 * 描述
	 */
	@ReadValue
	@WriteValue
	@Persistence
	private String description;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	@Override
	@Label
	public String toString() {
		return name + " [" + id + "]";
	}

	@ReadValue(ReadValue.TYPE)
	private String typeName = "项目模板";

	@Persistence
	private boolean module = false;

	public boolean isEnabled() {
		return enabled;
	}

	public ProjectTemplate setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public void setModule(boolean module) {
		this.module = module;
	}
	
	public boolean isModule() {
		return module;
	}

}
