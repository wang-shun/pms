package com.bizvisionsoft.service;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.service.DataSet;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.service.model.CBSItem;
import com.bizvisionsoft.service.model.CBSPeriod;
import com.bizvisionsoft.service.model.CBSSubject;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.service.model.Result;
import com.bizvisionsoft.service.model.Work;
import com.mongodb.BasicDBObject;

@Path("/cbs")
public interface CBSService {

	@GET
	@Path("/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public CBSItem get(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/scope/root/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "CBS/list", "CBS（查看）/list" })
	public List<CBSItem> getScopeRoot(
			@PathParam("_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId scope_id);

	@POST
	@Path("/icbsscope/root/project/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Project getICBSScopeRootProject(
			@PathParam("_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId scope_id);

	@POST
	@Path("/icbsscope/root/work/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Work getICBSScopeRootWork(
			@PathParam("_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId scope_id);

	@POST
	@Path("/{_id}/subcbs/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<CBSItem> getSubCBSItems(@PathParam("_id") ObjectId parent_id);

	@POST
	@Path("/{_id}/subcbs/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countSubCBSItems(@PathParam("_id") ObjectId parent_id);

	@POST
	@Path("/{_id}/subject/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<CBSSubject> getCBSSubject(@PathParam("_id") ObjectId cbs_id);

	@POST
	@Path("/{_id}/period/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<CBSPeriod> getCBSPeriod(@PathParam("_id") ObjectId cbs_id);

	@POST
	@Path("/{_id}/subject/{number}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<CBSSubject> getAllSubCBSSubjectByNumber(@PathParam("_id") ObjectId cbs_id,
			@PathParam("number") String number);

	@GET
	@Path("/{_id}/cost/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@Deprecated
	public CBSItem getCBSItemCost(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public CBSItem insertCBSItem(CBSItem o);

	@POST
	@Path("/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<CBSItem> createDataSet(@MethodParam(MethodParam.CONDITION) BasicDBObject condition);

	@DELETE
	@Path("/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet(DataSet.DELETE)
	public void delete(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("CBS/update")
	public long update(BasicDBObject filterAndUpdate);

	@PUT
	@Path("/period/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public ObjectId updateCBSPeriodBudget(CBSPeriod o);

	@PUT
	@Path("/subject/budget/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public CBSSubject upsertCBSSubjectBudget(CBSSubject o);

	@PUT
	@Path("/subject/cost/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public CBSSubject upsertCBSSubjectCost(CBSSubject o);

	@PUT
	@Path("/_id/{_id}/allocate/{scope_id}/{scopename}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public CBSItem allocateBudget(@PathParam("_id") ObjectId _id, @PathParam("scope_id") ObjectId scope_id,
			@PathParam("scopename") String scopename);

	@PUT
	@Path("/_id/{_id}/unallocate/{parent_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public CBSItem unallocateBudget(@PathParam("_id") ObjectId _id, @PathParam("parent_id") ObjectId parent_id);

	@PUT
	@Path("/_id/{_id}/calculation/{userId}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Result calculationBudget(@PathParam("_id") ObjectId _id, @PathParam("userId") String userId);

	@PUT
	@Path("/_id/{_id}/addcbsbystage/{project_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<CBSItem> addCBSItemByStage(@PathParam("_id") ObjectId _id,
			@PathParam("project_id") ObjectId project_id);

	@POST
	@Path("/projectcost/userId/{userId}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "成本管理/" + DataSet.LIST })
	public List<CBSItem> listProjectCost(@MethodParam(MethodParam.CONDITION) BasicDBObject condition,
			@PathParam("userId") @MethodParam(MethodParam.CURRENT_USER_ID) String userId);

	@POST
	@Path("/projectcostanalysis/userId/{userId}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "预算成本对比分析/" + DataSet.LIST })
	public List<CBSItem> listProjectCostAnalysis(@MethodParam(MethodParam.CONDITION) BasicDBObject condition,
			@PathParam("userId") @MethodParam(MethodParam.CURRENT_USER_ID) String userId);

	@POST
	@Path("/projectcost/userId/{userId}/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "成本管理/" + DataSet.COUNT, "预算成本对比分析/" + DataSet.COUNT })
	public long countProjectCost(@MethodParam(MethodParam.FILTER) BasicDBObject filter,
			@PathParam("userId") @MethodParam(MethodParam.CURRENT_USER_ID) String userId);

	@GET
	@Path("/settlementdate/{scope_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Date getNextSettlementDate(@PathParam("scope_id") ObjectId scope_id);

	@PUT
	@Path("/submitcost/{scope_id}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<Result> submitCBSSubjectCost(Date id, @PathParam("scope_id") ObjectId scope_id);

	@POST
	@Path("/costcompositionanalysis/cbsscope_id/{cbsscope_id}/{year}/{userId}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getCostCompositionAnalysis(@PathParam("cbsscope_id") ObjectId cbsScope_id,
			@PathParam("year") String year, @PathParam("userId") String userId);

	@POST
	@Path("/periodcostcompositionanalysis/cbsscope_id/{cbsscope_id}/{startPeriod}/{endPeriod}/{userId}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getPeriodCostCompositionAnalysis(@PathParam("cbsscope_id") ObjectId cbsScope_id,
			@PathParam("startPeriod") String startPeriod, @PathParam("endPeriod") String endPeriod,
			@PathParam("userId") String userId);

	@POST
	@Path("/monthcostcompositionanalysis/cbsscope_id/{cbsscope_id}/{year}/{userId}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getMonthCostCompositionAnalysis(@PathParam("cbsscope_id") ObjectId cbsScope_id,
			@PathParam("year") String year, @PathParam("userId") String userId);

	@POST
	@Path("/chart/cbsscope_id/{cbsscope_id}/monthlyBudgetAndCost")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("项目预算成本对比（首页小组件）/list")
	public Document getMonthlyCostAndBudgetChart(
			@PathParam("cbsscope_id") @MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT_ID) ObjectId cbsScope_id);

	@POST
	@Path("/costcompositionanalysis/{year}/{userId}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getCostCompositionAnalysis(@PathParam("year") String year, @PathParam("userId") String userId);

	@POST
	@Path("/periodcostcompositionanalysis/{startPeriod}/{endPeriod}/{userId}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getPeriodCostCompositionAnalysis(@PathParam("startPeriod") String startPeriod,
			@PathParam("endPeriod") String endPeriod, @PathParam("userId") String userId);

	@POST
	@Path("/monthcostcompositionanalysis/{year}/{userId}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getMonthCostCompositionAnalysis(@PathParam("year") String year, @PathParam("userId") String userId);

	@POST
	@Path("/cbssummart/{cbsscope_id}/{startPeriod}/{endPeriod}/{userId}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getCBSSummary(@PathParam("cbsscope_id") ObjectId cbsScope_id,
			@PathParam("startPeriod") String startPeriod, @PathParam("endPeriod") String endPeriod,
			@PathParam("userId") String userId);

	@POST
	@Path("/cbssummart/{startPeriod}/{endPeriod}/{userId}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getCBSSummary(@PathParam("startPeriod") String startPeriod,
			@PathParam("endPeriod") String endPeriod, @PathParam("userId") String userId);

}