package com.bizvisionsoft.service.model;

import java.util.List;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.mongocodex.Exclude;
import com.bizvisionsoft.annotations.md.mongocodex.PersistenceCollection;
import com.bizvisionsoft.annotations.md.service.DataSet;
import com.bizvisionsoft.annotations.md.service.Label;
import com.bizvisionsoft.annotations.md.service.ReadValue;
import com.bizvisionsoft.annotations.md.service.Structure;
import com.bizvisionsoft.annotations.md.service.WriteValue;
import com.bizvisionsoft.service.DocumentService;
import com.bizvisionsoft.service.ServicesLoader;

@PersistenceCollection("folderInTemplate")
public class FolderInTemplate implements IFolder{

	////////////////////////////////////////////////////////////////////////////////////////////////
	// 基本的一些字段

	/** 标识 Y **/
	@ReadValue
	@WriteValue
	private ObjectId _id;

	@ReadValue
	@WriteValue
	private ObjectId template_id;

	@ReadValue
	@WriteValue
	private ObjectId parent_id;

	@Exclude
	private boolean opened;

	@ReadValue
	@WriteValue
	private String name;

	@Override
	@Label
	public String toString() {
		return name;
	}

	@ReadValue({ "项目档案库文件夹/folderName" })
	private String getHTMLName() {
		String iconUrl;
		if (parent_id == null) {
			iconUrl = "rwt-resources/extres/img/cabinet_c.svg";
		} else if (opened) {
			iconUrl = "rwt-resources/extres/img/folder_opened.svg";
		} else {
			iconUrl = "rwt-resources/extres/img/folder_closed.svg";
		}
		String html = "<div style='display:inline-flex;justify-content:space-between;width:100%;'>" + "<img src="
				+ iconUrl + " style='margin-right:8px;' width='20px' height='20px'/>" + "<div style='flex:auto;'>"
				+ name + "</div>"
				+ "<a href='open/' target='_rwt' style='margin-right:8px;'><i class='layui-icon layui-btn layui-btn-primary layui-btn-xs' style='cursor:pointer;'>&#xe671;</i></a>"
				+ "</div>";
		return html;
	}

	@ReadValue("项目模板文件夹选择列表/folderName" )
	private String getHTMLName2() {
		String iconUrl;
		if (parent_id == null) {
			iconUrl = "rwt-resources/extres/img/cabinet_c.svg";
		} else if (opened) {
			iconUrl = "rwt-resources/extres/img/folder_opened.svg";
		} else {
			iconUrl = "rwt-resources/extres/img/folder_closed.svg";
		}
		String html = "<div style='display:inline-flex;justify-content:space-between;width:100%;'>" + "<img src="
				+ iconUrl + " style='margin-right:8px;' width='20px' height='20px'/>" + "<div style='flex:auto;'>"
				+ name + "</div>" + "</div>";
		return html;
	}
	

	@Structure(DataSet.LIST)
	private List<FolderInTemplate> listChildren() {
		return ServicesLoader.get(DocumentService.class).listChildrenFolderTemplate(_id);
	}

	@Structure(DataSet.COUNT)
	private long countChildren() {
		return ServicesLoader.get(DocumentService.class).countChildrenFolderTemplate(_id);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ObjectId get_id() {
		return _id;
	}

	public String getName() {
		return name;
	}

	public FolderInTemplate setName(String name) {
		this.name = name;
		return this;
	}

	public FolderInTemplate setTempalte_id(ObjectId template_id) {
		this.template_id = template_id;
		return this;
	}

	public FolderInTemplate setParent_id(ObjectId parent_id) {
		this.parent_id = parent_id;
		return this;
	}

	public FolderInTemplate setOpened(boolean opened) {
		this.opened = opened;
		return this;
	}

}
