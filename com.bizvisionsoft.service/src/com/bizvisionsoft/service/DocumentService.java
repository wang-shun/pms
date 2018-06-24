package com.bizvisionsoft.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.service.DataSet;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.service.model.Docu;
import com.bizvisionsoft.service.model.Folder;
import com.mongodb.BasicDBObject;

@Path("/doc")
public interface DocumentService {

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 文件夹
	@POST
	@Path("/folder/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Folder createFolder(Folder folder);

	@POST
	@Path("/folder/project_id/{project_id}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目文件夹选择列表/list")
	public List<Folder> listRootFolder(
			@PathParam("project_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId project_id);

	@POST
	@Path("/folder/project_id/{project_id}/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countRootFolder(@PathParam("project_id") ObjectId project_id);

	@POST
	@Path("/folder/parent_id/{parent_id}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<Folder> listChildrenFolder(@PathParam("parent_id") ObjectId parentFolder_id);

	@POST
	@Path("/folder/parent_id/{parent_id}/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countChildrenFolder(@PathParam("parent_id") ObjectId parentFolder_id);

	@DELETE
	@Path("/folder/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void deleteFolder(@PathParam("_id") ObjectId folder_id);

	@PUT
	@Path("/folder/_id/{_id}/{name}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void renameFolder(@PathParam("_id") ObjectId folder_id, @PathParam("name") String name);

	@POST
	@Path("/docu/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Docu createDocument(Docu doc);

	// @POST
	// @Path("/docu/folder_id/{folder_id}/ds")
	// @Consumes("application/json; charset=UTF-8")
	// @Produces("application/json; charset=UTF-8")
	// public List<Docu> listDocument(@PathParam("folder_id") ObjectId folder_id);

	// @POST
	// @Path("/docu/folder_id/{folder_id}/count")
	// @Consumes("application/json; charset=UTF-8")
	// @Produces("application/json; charset=UTF-8")
	// public long countDocument(@PathParam("folder_id") ObjectId folder_id);

	@DELETE
	@Path("/docu/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目档案库文件列表/" + DataSet.DELETE)
	public long deleteDocument(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/docu/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目档案库文件列表/" + DataSet.UPDATE)
	public long updateDocument(BasicDBObject filterAndUpdate);

	@POST
	@Path("/docu/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目档案库文件列表/" + DataSet.LIST)
	public List<Docu> listDocument(@MethodParam(MethodParam.CONDITION) BasicDBObject condition);

	@POST
	@Path("/docu/wp_id/{wp_id}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("输出文档/" + DataSet.LIST)
	public List<Docu> listWorkPackageDocument(@PathParam("wp_id") @MethodParam(MethodParam.CONTEXT_INPUT_OBJECT_ID) ObjectId wp_id);

	@POST
	@Path("/docu/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目档案库文件列表/" + DataSet.COUNT)
	public long countDocument(@MethodParam(MethodParam.FILTER) BasicDBObject filter);

}
