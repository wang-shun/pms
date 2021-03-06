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
import com.bizvisionsoft.service.model.Result;
import com.bizvisionsoft.service.model.WorkInfo;
import com.bizvisionsoft.service.model.WorkLinkInfo;
import com.bizvisionsoft.service.model.Workspace;
import com.bizvisionsoft.service.model.WorkspaceGanttData;
import com.mongodb.BasicDBObject;

@Path("/workspace")
public interface WorkSpaceService {

	@POST
	@Path("/nextwbsidx")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public int nextWBSIndex(BasicDBObject condition);

	@GET
	@Path("/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet(DataSet.INPUT)
	public WorkInfo getWorkInfo(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@POST
	@Path("/gantt/works")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<WorkInfo> createTaskDataSet(@MethodParam(MethodParam.FILTER) BasicDBObject condition);

	@POST
	@Path("/gantt/links")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<WorkLinkInfo> createLinkDataSet(@MethodParam(MethodParam.FILTER) BasicDBObject condition);

	@POST
	@Path("/work/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public WorkInfo insertWork(WorkInfo work);

	@POST
	@Path("/link/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public WorkLinkInfo insertLink(WorkLinkInfo link);

	@PUT
	@Path("/work/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long updateWork(BasicDBObject filterAndUpdate);

	@PUT
	@Path("/link/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long updateLink(BasicDBObject filterAndUpdate);

	@DELETE
	@Path("/work/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long deleteWork(@PathParam("_id") ObjectId _id);

	@DELETE
	@Path("/link/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long deleteLink(@PathParam("_id") ObjectId _id);

	@PUT
	@Path("/checkout/{userId}/{cancelCheckoutSubSchedule}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Result checkout(@MethodParam(MethodParam.OBJECT) Workspace workspace, @PathParam("userId") String userId,
			@PathParam("cancelCheckoutSubSchedule") Boolean cancelCheckoutSubSchedule);

	@POST
	@Path("/check/{checkManageItem}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Result schedulePlanCheck(@MethodParam(MethodParam.OBJECT) Workspace workspace,
			@PathParam("checkManageItem") Boolean checkManageItem);

	@PUT
	@Path("/checkin/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Result checkin(@MethodParam(MethodParam.OBJECT) Workspace workspace);

	@PUT
	@Path("/cancelcheckout/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Result cancelCheckout(@MethodParam(MethodParam.OBJECT) Workspace workspace);

	@POST
	@Path("/compare/{root_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<WorkInfo> createComparableWorkDataSet(@PathParam("root_id") ObjectId root_id);

	@PUT
	@Path("/ganttdata/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Result updateGanttData(WorkspaceGanttData ganttData);

}
