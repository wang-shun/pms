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

import org.bson.Document;
import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.service.DataSet;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.service.model.EPS;
import com.bizvisionsoft.service.model.EPSInfo;
import com.bizvisionsoft.service.model.EPSInvestmentAnalysis;
import com.mongodb.BasicDBObject;

@Path("/eps")
public interface EPSService {

	@GET
	@Path("/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public EPS get(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/_id/{_id}/eps")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<EPS> getSubEPS(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/root")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "EPS管理/list", "EPS浏览/list", "EPS选择/list" })
	public List<EPS> getRootEPS();

	@POST
	@Path("/count/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "EPS管理/count", "EPS浏览/count",  "EPS选择/count"})
	public long count(@MethodParam(MethodParam.FILTER) BasicDBObject filter);

	@POST
	@Path("/_id/{_id}/count/eps")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countSubEPS(@PathParam("_id") ObjectId _id);

	@DELETE
	@Path("/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("EPS管理/" + DataSet.DELETE)
	public long delete(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId get_id);

	@PUT
	@Path("/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("EPS管理/" + DataSet.UPDATE)
	public long update(BasicDBObject filterAndUpdate);

	@POST
	@Path("/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("EPS管理/" + DataSet.INSERT)
	public EPS insert(@MethodParam(MethodParam.OBJECT) EPS eps);

	@POST
	@Path("/info/root/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "EPS浏览-投资分析/list" })
	public List<EPSInfo> listRootEPSInfo();

	@POST
	@Path("/info/root/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "EPS浏览-投资分析/count" })
	public long countRootEPSInfo();

	@POST
	@Path("/info/{_id}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<EPSInfo> listSubEPSInfo(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/info/{_id}/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countSubEPSInfo(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/profitanalysis/{year}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getMonthProfitIA(List<EPSInvestmentAnalysis> epsIAs, @PathParam("year") String year);

	@POST
	@Path("/costanalysis/{year}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getMonthCostIA(List<EPSInvestmentAnalysis> epsIAs, @PathParam("year") String year);

	@GET
	@Path("/project/{_id}/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<ObjectId> getSubProjectId(@PathParam("_id") ObjectId _id);

}
