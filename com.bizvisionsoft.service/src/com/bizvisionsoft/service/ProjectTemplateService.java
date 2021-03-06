package com.bizvisionsoft.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.service.DataSet;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.service.model.OBSInTemplate;
import com.bizvisionsoft.service.model.OBSModule;
import com.bizvisionsoft.service.model.ProjectTemplate;
import com.bizvisionsoft.service.model.ResourceAssignment;
import com.bizvisionsoft.service.model.ResourcePlanInTemplate;
import com.bizvisionsoft.service.model.Result;
import com.bizvisionsoft.service.model.WBSModule;
import com.bizvisionsoft.service.model.WorkInTemplate;
import com.bizvisionsoft.service.model.WorkLinkInTemplate;
import com.bizvisionsoft.service.model.WorkspaceGanttData;
import com.mongodb.BasicDBObject;

@Path("/projectTemplate")
public interface ProjectTemplateService {

	@POST
	@Path("/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目模板管理/" + DataSet.INSERT)
	public ProjectTemplate insertProjectTemplate(@MethodParam(MethodParam.OBJECT) ProjectTemplate project);

	@DELETE
	@Path("/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "项目模板管理/" + DataSet.DELETE, "WBS模块/" + DataSet.DELETE })
	public long delete(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@GET
	@Path("/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目模板/" + DataSet.INPUT)
	public ProjectTemplate get(@PathParam("_id") @MethodParam("_id") ObjectId _id);

	@POST
	@Path("/count/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目模板管理/" + DataSet.COUNT)
	public long countProjectTemplate(@MethodParam(MethodParam.FILTER) BasicDBObject filter);

	@PUT
	@Path("/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "项目模板管理/" + DataSet.UPDATE, "WBS模块/" + DataSet.UPDATE })
	public long update(BasicDBObject filterAndUpdate);

	@POST
	@Path("/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目模板管理/" + DataSet.LIST)
	public List<ProjectTemplate> listProjectTemplate(@MethodParam(MethodParam.CONDITION) BasicDBObject condition);

	@POST
	@Path("wbsModule/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("WBS模块/" + DataSet.INSERT)
	public WBSModule insertWBSModule(@MethodParam(MethodParam.OBJECT) WBSModule wbsModule);

	@POST
	@Path("/wbsmodule/count/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("WBS模块/" + DataSet.COUNT)
	public long countWBSModule(@MethodParam(MethodParam.FILTER) BasicDBObject filter);

	@POST
	@Path("/wbsmodule/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("WBS模块/" + DataSet.LIST)
	public List<WBSModule> listWBSModule(@MethodParam(MethodParam.CONDITION) BasicDBObject condition);

	@GET
	@Path("/work/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public WorkInTemplate getWorkInTemplate(@PathParam("_id") ObjectId work_id);

	@POST
	@Path("/_id/{_id}/works/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<WorkInTemplate> listWorks(@PathParam("_id") ObjectId template_id);

	@POST
	@Path("/_id/{_id}/links/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<WorkLinkInTemplate> listLinks(@PathParam("_id") ObjectId template_id);

	@POST
	@Path("/work/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public WorkInTemplate insertWork(WorkInTemplate work);

	@PUT
	@Path("/work/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "项目模板WBS/" + DataSet.UPDATE, "项目模板WBS（分配角色）/" + DataSet.UPDATE })
	public long updateWork(BasicDBObject bson);

	@DELETE
	@Path("/work/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long deleteWork(@PathParam("_id") ObjectId work_id);

	@POST
	@Path("/link/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public WorkLinkInTemplate insertLink(WorkLinkInTemplate link);

	@PUT
	@Path("/link/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long updateLink(BasicDBObject bson);

	@DELETE
	@Path("/link/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long deleteLink(@PathParam("_id") ObjectId link_id);

	@POST
	@Path("/nextwbsidx")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public int nextWBSIndex(BasicDBObject append);

	@POST
	@Path("/nextobsseq")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public int nextOBSSeq(BasicDBObject condition);

	@POST
	@Path("/id/{_id}/obs/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目模板组织结构图/list")
	public List<OBSInTemplate> getOBSTemplate(
			@PathParam("_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId template_id);

	@DELETE
	@Path("/obs/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "项目模板组织结构图/" + DataSet.DELETE, "OBS模板组织结构图/" + DataSet.DELETE })
	public void deleteOBSItem(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@POST
	@Path("/id/{_id}/obs/role/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("OBS模板节点选择器/list")
	public List<OBSInTemplate> getOBSRoleTemplate(
			@PathParam("_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId template_id);

	@GET
	@Path("/id/{_id}/hasOBS")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public boolean hasOBS(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/id/{_id}/obs/root")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public OBSInTemplate createRootOBS(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/obs/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public OBSInTemplate insertOBSItem(OBSInTemplate t);

	@PUT
	@Path("/obs/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "项目模板组织结构图/" + DataSet.UPDATE, "OBS模板组织结构图/" + DataSet.UPDATE })
	public long updateOBSItem(BasicDBObject fu);

	@POST
	@Path("/_id/{_id}/wbs/root/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "项目模板WBS/" + DataSet.LIST, "项目模板WBS（分配角色）/" + DataSet.LIST })
	public List<WorkInTemplate> listWBSRoot(
			@PathParam("_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId template_id);

	@POST
	@Path("/_id/{_id}/wbs/root/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "项目模板WBS/" + DataSet.COUNT, "项目模板WBS（分配角色）/" + DataSet.COUNT })
	public long countWBSRoot(
			@PathParam("_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId template_id);

	@POST
	@Path("/parent_id/{parent_id}/wbs/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<WorkInTemplate> listWBSChildren(@PathParam("parent_id") ObjectId parent_id);

	@POST
	@Path("/parent_id/{parent_id}/wbs/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countWBSChildren(@PathParam("parent_id") ObjectId parent_id);

	@POST
	@Path("/resourceplan/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<ResourcePlanInTemplate> addResourcePlan(List<ResourceAssignment> resas);

	@PUT
	@Path("/resourceplan/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long updateResourcePlan(BasicDBObject filterAndUpdate);

	@DELETE
	@Path("/_id/{_id}/resourceplan/hr/{resId}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long deleteHumanResourcePlan(@PathParam("_id") ObjectId work_id, @PathParam("resId") String hrResId);

	@DELETE
	@Path("/_id/{_id}/resourceplan/eq/{resId}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long deleteEquipmentResourcePlan(@PathParam("_id") ObjectId work_id, @PathParam("resId") String eqResId);

	@DELETE
	@Path("/_id/{_id}/resourceplan/ty/{resId}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long deleteTypedResourcePlan(@PathParam("_id") ObjectId work_id, @PathParam("resId") String tyResId);

	@POST
	@Path("/_id/{_id}/resourceplan/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<ResourcePlanInTemplate> listResourcePlan(@PathParam("_id") ObjectId _id);

	@PUT
	@Path("/_id/{_id}/enabled/{enabled}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void setEnabled(@PathParam("_id") ObjectId _id, @PathParam("enabled") boolean enabled);

	@PUT
	@Path("/_id/{_id}/project_id/{project_id}/checkoutBy/{checkoutBy}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void useTemplate(@PathParam("_id") ObjectId _id, @PathParam("project_id") ObjectId project_id,
			@PathParam("checkoutBy") String checkoutBy);

	@PUT
	@Path("/ganttdata/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Result updateGanttData(WorkspaceGanttData ganttData);

	@POST
	@Path("/enabledtemplate/count/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目模板选择器列表/" + DataSet.COUNT)
	public long countEnabledTemplate(@MethodParam(MethodParam.FILTER) BasicDBObject filter);

	@POST
	@Path("/enabledtemplate/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目模板选择器列表/" + DataSet.LIST)
	public List<ProjectTemplate> createEnabledTemplateDataSet(
			@MethodParam(MethodParam.CONDITION) BasicDBObject condition);

	@POST
	@Path("/obsModule/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("组织模板/" + DataSet.INSERT)
	public OBSModule insertOBSModule(@MethodParam(MethodParam.OBJECT) OBSModule obsModule);

	@POST
	@Path("/obsModule/count/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("组织模板/" + DataSet.COUNT)
	public long countOBSModule(@MethodParam(MethodParam.FILTER) BasicDBObject filter);

	@POST
	@Path("/obsModule/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("组织模板/" + DataSet.LIST)
	public List<OBSModule> listOBSModule(@MethodParam(MethodParam.CONDITION) BasicDBObject condition);

	@DELETE
	@Path("/obsModule/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "组织模板/" + DataSet.DELETE })
	public long deleteOBSModule(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/obsModule/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "组织模板/" + DataSet.UPDATE })
	public long updateOBSModule(BasicDBObject filterAndUpdate);

	@POST
	@Path("/id/{_id}/obsModule/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("OBS模板组织结构图/" + DataSet.LIST)
	public List<OBSInTemplate> getOBSInTemplateByModule(
			@PathParam("_id") @MethodParam(MethodParam.CONTEXT_INPUT_OBJECT_ID) ObjectId module_id);

	@GET
	@Path("/id/{_id}/obsModule/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("组织模板/" + DataSet.INPUT)
	public OBSModule getOBSModule(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/useOBSModule/_id/{_id}/project_id/{project_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void useOBSModule(@PathParam("_id") ObjectId _id, @PathParam("project_id") ObjectId project_id);

	@POST
	@Path("/obsModule/project_id/{project_id}/count/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("组织模板选择器列表/" + DataSet.COUNT)
	public long countOBSModuleSelector(@MethodParam(MethodParam.FILTER) BasicDBObject filter,
			@PathParam("project_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId project_id);

	@POST
	@Path("/obsModule/project_id/{project_id}/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("组织模板选择器列表/" + DataSet.LIST)
	public List<OBSModule> listOBSModuleSelector(@MethodParam(MethodParam.CONDITION) BasicDBObject condition,
			@PathParam("project_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId project_id);

	@POST
	@Path("/obsModule/template_id/{template_id}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void templateOBSSaveAsOBSModule(OBSModule obsModule, @PathParam("template_id") ObjectId template_id);

	@POST
	@Path("/obsModule/project_id/{project_id}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void projectOBSSaveAsOBSModule(OBSModule obsModule, @PathParam("project_id") ObjectId project_id);
}
