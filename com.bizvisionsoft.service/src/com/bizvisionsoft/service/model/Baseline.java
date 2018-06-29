package com.bizvisionsoft.service.model;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.mongocodex.Persistence;
import com.bizvisionsoft.annotations.md.mongocodex.PersistenceCollection;
import com.bizvisionsoft.annotations.md.service.ReadValue;
import com.bizvisionsoft.annotations.md.service.WriteValue;
import com.bizvisionsoft.service.ServicesLoader;
import com.bizvisionsoft.service.WorkService;

@PersistenceCollection("baseline")
public class Baseline {

	@Persistence
	private ObjectId _id;

	@Persistence
	private ObjectId project_id;

	@ReadValue
	@Persistence
	private Date creationDate;

	@ReadValue
	@WriteValue
	@Persistence
	private String description;

	@ReadValue
	@WriteValue
	@Persistence
	private String name;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public Baseline setProject_id(ObjectId project_id) {
		this.project_id = project_id;
		return this;
	}

	public ObjectId getProject_id() {
		return project_id;
	}

	public Baseline setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	@ReadValue("createDate")
	public Date getCreateDate() {
		return creationDate;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public Baseline setName(String name) {
		this.name = name;
		return this;
	}

	public List<Work> createGanttTaskDataSet() {
		return ServicesLoader.get(WorkService.class).createBaselineTaskDataSet(_id);
	}

	public List<WorkLink> createGanttLinkDataSet() {
		return ServicesLoader.get(WorkService.class).createBaselineLinkDataSet(_id);
	}
}
