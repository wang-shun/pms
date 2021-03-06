package com.bizvisionsoft.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.bizvisionsoft.service.EPSService;
import com.bizvisionsoft.service.model.EPS;
import com.bizvisionsoft.service.model.EPSInfo;
import com.bizvisionsoft.service.model.EPSInvestmentAnalysis;
import com.bizvisionsoft.service.model.Program;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.service.tools.Formatter;
import com.bizvisionsoft.serviceimpl.exception.ServiceException;
import com.bizvisionsoft.serviceimpl.query.JQ;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;

public class EPSServiceImpl extends BasicServiceImpl implements EPSService {

	@Override
	public long update(BasicDBObject fu) {
		return update(fu, EPS.class);
	}

	@Override
	public EPS insert(EPS eps) {
		return insert(eps, EPS.class);
	}

	@Override
	public EPS get(ObjectId _id) {
		return get(_id, EPS.class);
	}

	@Override
	public List<EPS> getRootEPS() {
		return getSubEPS(null);
	}

	@Override
	public long count(BasicDBObject filter) {
		return count(filter, EPS.class);
	}

	@Override
	public long delete(ObjectId _id) {
		// 检查有没有下级的EPS节点
		if (c(EPS.class).countDocuments(new Document("parent_id", _id)) > 0) {
			throw new ServiceException("不允许删除有下级节点的EPS记录");
		}
		// 检查有没有下级的项目集节点
		if (c(Program.class).countDocuments(new Document("eps_id", _id)) > 0) {
			throw new ServiceException("不允许删除有下级节点的EPS记录");
		}

		// 检查有没有下级的项目节点
		if (c(Project.class).countDocuments(new Document("eps_id", _id)) > 0) {
			throw new ServiceException("不允许删除有下级节点的EPS记录");
		}

		// TODO 即便下面没有节点同样也需要考虑是否有其他数据（比如，绩效等等）
		return delete(_id, EPS.class);
	}

	@Override
	public List<EPS> getSubEPS(ObjectId parent_id) {
		ArrayList<EPS> result = new ArrayList<EPS>();
		c(EPS.class).find(new Document("parent_id", parent_id)).sort(new Document("id", 1)).into(result);
		return result;
	}

	@Override
	public long countSubEPS(ObjectId _id) {
		return c(EPS.class).countDocuments(new Document("parent_id", _id));
	}

	@Override
	public List<EPSInfo> listRootEPSInfo() {
		List<EPSInfo> result = new ArrayList<EPSInfo>();
		c("eps", EPSInfo.class).find(new Document("parent_id", null)).forEach((EPSInfo epsInfo) -> {
			result.add(epsInfo.setType(EPSInfo.TYPE_EPS));
		});
		return result;
	}

	@Override
	public long countRootEPSInfo() {
		return c("eps").countDocuments(new Document("parent_id", null));
	}

	@Override
	public List<EPSInfo> listSubEPSInfo(ObjectId _id) {
		List<EPSInfo> result = new ArrayList<EPSInfo>();
		c("eps", EPSInfo.class).find(new Document("parent_id", _id)).forEach((EPSInfo epsInfo) -> {
			result.add(epsInfo.setType(EPSInfo.TYPE_EPS));
		});
		List<? extends Bson> pipeline = new JQ("查询-销售和成本-项目")
				.set("match", new Document("eps_id", _id)).array();
		c("project", EPSInfo.class).aggregate(pipeline).forEach((EPSInfo epsInfo) -> {
			result.add(epsInfo.setType(EPSInfo.TYPE_PROJECT));
		});
		pipeline = new JQ("查询-销售和成本-项目").set("match", new Document("program_id", _id))
				.array();
		c("project", EPSInfo.class).aggregate(pipeline).forEach((EPSInfo epsInfo) -> {
			result.add(epsInfo.setType(EPSInfo.TYPE_PROJECT));
		});
		return result;
	}

	@Override
	public long countSubEPSInfo(ObjectId _id) {
		long countDocuments = c("eps").countDocuments(new Document("parent_id", _id));
		countDocuments += c("program").countDocuments(new Document("eps_id", _id));
		countDocuments += c("program").countDocuments(new Document("parent_id", _id));
		countDocuments += c("project").countDocuments(new Document("eps_id", _id));
		countDocuments += c("project").countDocuments(new Document("program_id", _id));
		return countDocuments;
	}

	@Override
	public Document getMonthProfitIA(List<EPSInvestmentAnalysis> epsIAs, String year) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.set(Integer.parseInt(year), 0, 1, 0, 0, 0);
		cal2.set(Integer.parseInt(year), 11, 1, 0, 0, 0);
		return getProfitIA(epsIAs, year + "年 销售利润分析（万元）", cal1.getTime(), cal2.getTime());
	}

	@Override
	public Document getMonthCostIA(List<EPSInvestmentAnalysis> epsIAs, String year) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.set(Integer.parseInt(year), 0, 1, 0, 0, 0);
		cal2.set(Integer.parseInt(year), 11, 1, 0, 0, 0);
		return getCostIA(epsIAs, year + "年 资金投入分析（万元）", cal1.getTime(), cal2.getTime());
	}

	private Document getProfitIA(List<EPSInvestmentAnalysis> epsIAs, String title, Date startDate, Date endDate) {
		List<String> xAxis = createXAxis(null, startDate, endDate);
		List<Document> series = new ArrayList<Document>();
		createProfitSeries(series, epsIAs, startDate, endDate);
		List<String> legend = getLegend(epsIAs);

		Document option = new Document();
		option.append("title", new Document("text", title).append("x", "center"));
		option.append("grid",
				new Document("left", "3%").append("right", "5%").append("bottom", "6%").append("containLabel", true));

		if (legend != null && legend.size() > 0)
			option.append("legend", new Document("data", legend).append("bottom", 10).append("left", "center"));

		option.append("xAxis", Arrays.asList(new Document("type", "category").append("data", xAxis)));
		option.append("yAxis", Arrays.asList(new Document("type", "value")));

		option.append("series", series);

		return option;
	}

	private Document getCostIA(List<EPSInvestmentAnalysis> epsIAs, String title, Date startDate, Date endDate) {
		List<String> xAxis = createXAxis(null, startDate, endDate);
		List<Document> series = new ArrayList<Document>();
		createCostSeries(series, epsIAs, startDate, endDate);
		List<String> legend = getLegend(epsIAs);

		Document option = new Document();
		option.append("title", new Document("text", title).append("x", "center"));
		option.append("grid",
				new Document("left", "3%").append("right", "5%").append("bottom", "6%").append("containLabel", true));

		if (legend != null && legend.size() > 0)
			option.append("legend", new Document("data", legend).append("bottom", 10).append("left", "center"));

		option.append("xAxis", Arrays.asList(new Document("type", "category").append("data", xAxis)));
		option.append("yAxis", Arrays.asList(new Document("type", "value")));

		option.append("series", series);

		return option;
	}

	private List<String> getLegend(List<EPSInvestmentAnalysis> epsIAs) {
		if (epsIAs != null && epsIAs.size() > 0) {
			List<String> legend = new ArrayList<String>();
			epsIAs.forEach(epsIA -> {
				legend.add(epsIA.name);
			});
			return legend;
		}
		return null;
	}

	private List<String> createXAxis(List<String> xAxis, Date startDate, Date endDate) {
		if (xAxis == null)
			xAxis = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M");
		xAxis.add(sdf.format(startDate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		while (cal.getTime().before(endDate)) {
			cal.add(Calendar.MONTH, 1);
			xAxis.add(sdf.format(cal.getTime()));
		}
		return xAxis;
	}

	private void createCostSeries(List<Document> series, List<EPSInvestmentAnalysis> epsIAs, Date startDate,
			Date endDate) {
		Map<String, Double> mapKeys = new TreeMap<String, Double>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		mapKeys.put(sdf.format(startDate), 0d);

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		while (cal.getTime().before(endDate)) {
			cal.add(Calendar.MONTH, 1);
			mapKeys.put(sdf.format(cal.getTime()), 0d);
		}
		if (epsIAs != null && epsIAs.size() > 0) {
			epsIAs.forEach(epsIA -> {
				AggregateIterable<Document> aggregate;
				if (epsIA.project_ids != null) {
					aggregate = c("project").aggregate(new JQ("查询-销售和成本-项目")
							.set("match", new Document("_id", new Document("$in", epsIA.project_ids))).array());
					createSeriesData(series, mapKeys, aggregate, "cbsSubjects", "id", "cost", epsIA.name);
				}
			});
		} else {
			AggregateIterable<Document> aggregate = c("project")
					.aggregate(new JQ("查询-销售和成本-项目").set("match", new Document()).array());
			createSeriesData(series, mapKeys, aggregate, "cbsSubjects", "id", "cost", "资金投入");
		}
	}

	private void createProfitSeries(List<Document> series, List<EPSInvestmentAnalysis> epsIAs, Date startDate,
			Date endDate) {
		Map<String, Double> mapKeys = new TreeMap<String, Double>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		mapKeys.put(sdf.format(startDate), 0d);

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		while (cal.getTime().before(endDate)) {
			cal.add(Calendar.MONTH, 1);
			mapKeys.put(sdf.format(cal.getTime()), 0d);
		}
		if (epsIAs != null && epsIAs.size() > 0) {
			epsIAs.forEach(epsIA -> {
				AggregateIterable<Document> aggregate;
				if (epsIA.project_ids != null) {
					aggregate = c("project").aggregate(new JQ("查询-销售和成本-项目")
							.set("match", new Document("_id", new Document("$in", epsIA.project_ids))).array());
					createSeriesData(series, mapKeys, aggregate, "salesItems", "period", "profit", epsIA.name);
				}
			});
		} else {
			AggregateIterable<Document> aggregate = c("project")
					.aggregate(new JQ("查询-销售和成本-项目").set("match", new Document()).array());
			createSeriesData(series, mapKeys, aggregate, "salesItems", "period", "profit", "销售利润");
		}
	}

	@SuppressWarnings("unchecked")
	private void createSeriesData(List<Document> series, Map<String, Double> mapKeys,
			AggregateIterable<Document> aggregate, String fieldName, String childFieldName, String valueName,
			String docName) {
		Map<String, Double> map = new TreeMap<String, Double>(mapKeys);
		aggregate.forEach((Document doc) -> {
			Object obj = doc.get(fieldName);
			if (obj != null && obj instanceof List) {
				((List<Document>) obj).forEach(child -> {
					Double d = map.get(child.getString(childFieldName));
					if (d != null) {
						Object value = child.get(valueName);
						if (value != null) {
							d += ((Number) value).doubleValue();
							map.put(child.getString(childFieldName), d);
						}
					}
				});
			}
		});

		Document doc = new Document();
		series.add(doc);
		doc.append("name", docName);
		doc.append("type", "bar");
		doc.append("label", new Document("normal", new Document("show", true).append("position", "inside")));
		List<Object> data = new ArrayList<Object>();
		for (Double d : map.values()) {
			data.add(getStringValue(d));
		}
		doc.append("data", data);
	}

	private String getStringValue(Object value) {
		if (value instanceof Number) {
			double d = ((Number) value).doubleValue();
			if (d != 0d) {
				return Formatter.getString(d);
			}
		}
		return "";
	}

	@Override
	public List<ObjectId> getSubProjectId(ObjectId _id) {
		List<ObjectId> epsIds = getDesentItems(Arrays.asList(_id), "eps", "parent_id");

		List<ObjectId> projectIds = c("project")
				.distinct("_id", new Document("eps_id", new Document("$in", epsIds)), ObjectId.class)
				.into(new ArrayList<ObjectId>());

		List<ObjectId> programIds = c("program")
				.distinct("_id", new Document("eps_id", new Document("$in", epsIds)), ObjectId.class)
				.into(new ArrayList<ObjectId>());
		if (programIds.size() > 0) {
			programIds = getDesentItems(programIds, "program", "parent_id");

			projectIds.addAll(c("project")
					.distinct("_id", new Document("program_id", new Document("$in", programIds)), ObjectId.class)
					.into(new ArrayList<ObjectId>()));
		}
		projectIds = getDesentItems(projectIds, "project", "parentProject_id");
		return projectIds;
	}

}
