package com.bizvisionsoft.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.bizvisionsoft.service.RiskService;
import com.bizvisionsoft.service.model.DetectionInd;
import com.bizvisionsoft.service.model.QuanlityInfInd;
import com.bizvisionsoft.service.model.RBSItem;
import com.bizvisionsoft.service.model.RBSType;
import com.bizvisionsoft.service.model.Result;
import com.bizvisionsoft.service.model.RiskEffect;
import com.bizvisionsoft.service.model.RiskScore;
import com.bizvisionsoft.service.model.RiskUrgencyInd;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.result.DeleteResult;

public class RiskServiceImpl extends BasicServiceImpl implements RiskService {

	@Override
	public List<RBSType> listRBSType() {
		return c(RBSType.class).find().into(new ArrayList<RBSType>());
	}

	@Override
	public RBSType insertRBSType(RBSType item) {
		return insert(item, RBSType.class);
	}

	@Override
	public long deleteRBSType(ObjectId _id) {
		return delete(_id, RBSType.class);
	}

	@Override
	public long updateRBSType(BasicDBObject fu) {
		return update(fu, RBSType.class);
	}

	@Override
	public List<RBSItem> listRBSItem(BasicDBObject condition) {
		return createDataSet(condition, RBSItem.class);
	}

	@Override
	public long countRBSItem(BasicDBObject filter) {
		return c("rbsItem").count(filter);
	}

	@Override
	public RBSItem insertRBSItem(RBSItem item) {
		// �������
		ObjectId pj_id = item.getProject_id();
		String typeId = item.getRbsType().getId();
		int idx = nextRBSItemIndex(pj_id, typeId);
		item.setIndex(idx);
		// ��������
		String qty = item.getQtyInf();
		double cost = item.getCostInf();
		int time = item.getTimeInf();
		Double score = calculateRiskInfValue(qty, cost, time);
		item.setInfValue(score);
		return insert(item);
	}

	private double calculateRiskInfValue(String qty, double cost, int time) {
		List<RiskScore> stdlist = listRiskScoreInd();
		Double score = null;
		
		for (int i = 0; i < stdlist.size(); i++) {
			RiskScore std = stdlist.get(i);
			if (std.getQuanlityImpact().stream().anyMatch(qid->qid.value.equals(qty))) {
				if(score == null ||score.doubleValue()<std.getScore()) {
					score = std.getScore();
					break;
				}
			}
		}
		for (int i = 0; i < stdlist.size(); i++) {
			RiskScore std = stdlist.get(i);
			double maxCost = Optional.ofNullable(std.getMaxCostImpact()).orElse(Double.MAX_VALUE);
			double minCost = Optional.ofNullable(std.getMinCostImpact()).orElse(Double.MIN_VALUE);
			if (cost <= maxCost && cost >= minCost) {
				if(score == null ||score.doubleValue()<std.getScore()) {
					score = std.getScore();
					break;
				}
			}
		}
		for (int i = 0; i < stdlist.size(); i++) {
			RiskScore std = stdlist.get(i);
			int maxTime = Optional.ofNullable(std.getMaxTimeImpact()).orElse(Integer.MAX_VALUE);
			int minTime = Optional.ofNullable(std.getMinTimeImpact()).orElse(Integer.MIN_VALUE);
			if (time <= maxTime && time >= minTime) {
				if(score == null ||score.doubleValue()<std.getScore()) {
					score = std.getScore();
					break;
				}
			}
		}

		return score==null? 0d:score.doubleValue();
	}

	public int nextRBSItemIndex(ObjectId project_id, String type_id) {
		Document doc = c("rbsItem").find(new BasicDBObject("project_id", project_id).append("rbsType.id", type_id))
				.sort(new BasicDBObject("index", -1)).projection(new BasicDBObject("index", 1)).first();
		return Optional.ofNullable(doc).map(d -> d.getInteger("index", 0)).orElse(0) + 1;
	}

	@Override
	public long deleteRBSItem(ObjectId _id) {
		List<ObjectId> items = getDesentItems(Arrays.asList(_id), "rbsItem", "parent_id");
		DeleteResult rs = c("rbsItem").deleteMany(new BasicDBObject("_id", new BasicDBObject("$in", items)));
		c("riskEffect").deleteMany(new BasicDBObject("rbsItem_id",_id));
		return rs.getDeletedCount();
	}

	@Override
	public long updateRBSItem(BasicDBObject fu) {
		long count = update(fu, RBSItem.class);
		
		BasicDBObject filter = (BasicDBObject) fu.get("filter");
		c("rbsItem").find(filter).forEach((Document doc) -> {
			String qty = (String) doc.getString("qtyInf");
			double cost = doc.getDouble("costInf");
			int time = doc.getInteger("timeInf");
			double score = calculateRiskInfValue(qty, cost, time);
			c("rbsItem").updateOne(new Document("_id", doc.get("_id")),
					new Document("$set", new Document("infValue", score)));
		});
		
		return count;
	}

	@Override
	public RiskEffect addRiskEffect(RiskEffect re) {
		return insert(re);
	}

	@Override
	public List<RiskUrgencyInd> listRiskUrgencyInd() {
		return c(RiskUrgencyInd.class).find().into(new ArrayList<RiskUrgencyInd>());
	}

	@Override
	public RiskUrgencyInd insertRiskUrgencyInd(RiskUrgencyInd item) {
		return insert(item);
	}

	@Override
	public long deleteRiskUrgencyInd(ObjectId _id) {
		return delete(_id, RiskUrgencyInd.class);
	}

	@Override
	public long updateRiskUrgencyInd(BasicDBObject fu) {
		return update(fu, RiskUrgencyInd.class);
	}

	@Override
	public String getUrgencyText(long days) {
		String text = c("riskUrInd").distinct("text",
				new BasicDBObject("min", new BasicDBObject("$lt", days)).append("max", new BasicDBObject("$gte", days)),
				String.class).first();
		if (text != null) {
			return text;
		} else {
			return "";
		}
	}

	@Override
	public List<QuanlityInfInd> listRiskQuanlityInfInd() {
		return c(QuanlityInfInd.class).find().sort(new BasicDBObject("_id", 1)).into(new ArrayList<QuanlityInfInd>());
	}

	@Override
	public QuanlityInfInd insertRiskQuanlityInfInd(QuanlityInfInd item) {
		return insert(item);
	}

	@Override
	public long deleteRiskQuanlityInfInd(ObjectId _id) {
		return delete(_id, QuanlityInfInd.class);
	}

	@Override
	public long updateRiskQuanlityInfInd(BasicDBObject fu) {
		return update(fu, QuanlityInfInd.class);

	}

	@Override
	public List<DetectionInd> listRiskDetectionInd() {
		return c(DetectionInd.class).find().sort(new BasicDBObject("_id", 1)).into(new ArrayList<DetectionInd>());
	}

	@Override
	public DetectionInd insertRiskDetectionInd(DetectionInd item) {
		return insert(item);
	}

	@Override
	public long deleteRiskDetectionInd(ObjectId _id) {
		return delete(_id, DetectionInd.class);
	}

	@Override
	public long updateRiskDetectionInd(BasicDBObject fu) {
		return update(fu, DetectionInd.class);
	}

	@Override
	public List<RiskScore> listRiskScoreInd() {
		return c(RiskScore.class).find().sort(new BasicDBObject("score", -1)).into(new ArrayList<RiskScore>());
	}

	@Override
	public RiskScore insertRiskScoreInd(RiskScore item) {
		return insert(item);
	}

	@Override
	public long deleteRiskScoreInd(ObjectId _id) {
		return delete(_id, RiskScore.class);
	}

	@Override
	public long updateRiskScoreInd(BasicDBObject fu) {
		return update(fu, RiskScore.class);
	}

	@Override
	public List<RiskEffect> listRiskEffect(BasicDBObject condition) {
		Integer skip = (Integer) condition.get("skip");
		Integer limit = (Integer) condition.get("limit");
		BasicDBObject filter = (BasicDBObject) condition.get("filter");
		BasicDBObject sort = (BasicDBObject) condition.get("sort");
		
		List<Bson> pipeline = new ArrayList<Bson>();
		if (filter != null)
			pipeline.add(Aggregates.match(filter));

		if (sort != null)
			pipeline.add(Aggregates.sort(sort));

		if (skip != null)
			pipeline.add(Aggregates.skip(skip));

		if (limit != null)
			pipeline.add(Aggregates.limit(limit));
		
		appendWork(pipeline);
		
		appendUserInfo(pipeline, "work.chargerId", "work.chargerInfo");
		
		pipeline.add(Aggregates.lookup("rbsItem", "rbsItem_id", "_id", "rbsItem"));
		
		pipeline.add(Aggregates.unwind("$rbsItem"));
		
		return c(RiskEffect.class).aggregate(pipeline).into(new ArrayList<>());
	}

	@Override
	public long countRiskEffect(BasicDBObject filter) {
		return count(filter, "riskEffect");
	}

	@Override
	public long deleteRiskEffect(ObjectId _id) {
		return delete(_id, RiskEffect.class);
	}

	@Override
	public long updateRiskEffect(BasicDBObject fu) {
		return update(fu, RiskEffect.class);
	}

	@Override
	public List<Result> monteCarloSimulate(ObjectId project_id) {
		// TODO Auto-generated method stub
		return null;
	}

}
