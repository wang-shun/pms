package com.bizvisionsoft.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.bizvisionsoft.service.CommonService;
import com.bizvisionsoft.service.model.AccountItem;
import com.bizvisionsoft.service.model.Calendar;
import com.bizvisionsoft.service.model.Certificate;
import com.bizvisionsoft.service.model.Dictionary;
import com.bizvisionsoft.service.model.Equipment;
import com.bizvisionsoft.service.model.Message;
import com.bizvisionsoft.service.model.News;
import com.bizvisionsoft.service.model.ProjectStatus;
import com.bizvisionsoft.service.model.ResourceType;
import com.bizvisionsoft.service.model.TrackView;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;

public class CommonServiceImpl extends BasicServiceImpl implements CommonService {

	@Override
	public List<Certificate> getCertificates() {
		List<Certificate> result = new ArrayList<>();
		c(Certificate.class).find().into(result);
		return result;
	}

	@Override
	public Certificate insertCertificate(Certificate cert) {
		return insert(cert, Certificate.class);
	}

	@Override
	public long deleteCertificate(ObjectId _id) {
		// TODO 考虑已经使用的任职资格

		return delete(_id, Certificate.class);
	}

	@Override
	public long updateCertificate(BasicDBObject fu) {
		return update(fu, Certificate.class);
	}

	@Override
	public List<String> getCertificateNames() {
		ArrayList<String> result = new ArrayList<String>();
		c(Certificate.class).distinct("name", String.class).into(result);
		return result;
	}

	@Override
	public List<ResourceType> getResourceType() {
		List<ResourceType> result = new ArrayList<>();
		c(ResourceType.class).find().into(result);
		return result;
	}

	@Override
	public ResourceType insertResourceType(ResourceType resourceType) {
		return insert(resourceType, ResourceType.class);
	}

	@Override
	public long deleteResourceType(ObjectId _id) {
		// TODO 考虑资源类型被使用的状况
		return delete(_id, ResourceType.class);
	}

	@Override
	public long updateResourceType(BasicDBObject fu) {
		return update(fu, ResourceType.class);
	}

	@Override
	public List<Equipment> getERResources(ObjectId _id) {
		return queryEquipments(new BasicDBObject("resourceType_id", _id));
	}

	private ArrayList<Equipment> queryEquipments(BasicDBObject condition) {
		ArrayList<Bson> pipeline = new ArrayList<Bson>();
		if (condition != null)
			pipeline.add(Aggregates.match(condition));

		appendOrgFullName(pipeline, "org_id", "orgFullName");

		ArrayList<Equipment> result = new ArrayList<Equipment>();
		c(Equipment.class).aggregate(pipeline).into(result);
		return result;
	}

	@Override
	public ResourceType getResourceType(ObjectId _id) {
		return get(_id, ResourceType.class);
	}

	@Override
	public long countERResources(ObjectId _id) {
		return c(Equipment.class).count(new BasicDBObject("resourceType_id", _id));
	}

	@Override
	public List<Equipment> getEquipments() {
		return queryEquipments(null);
	}

	@Override
	public Equipment insertEquipment(Equipment cert) {
		return insert(cert, Equipment.class);
	}

	@Override
	public long deleteEquipment(ObjectId _id) {
		// TODO 完整性问题
		return delete(_id, Equipment.class);
	}

	@Override
	public long updateEquipment(BasicDBObject fu) {
		return update(fu, Equipment.class);
	}

	@Override
	public List<Calendar> getCalendars() {
		return c(Calendar.class).find().into(new ArrayList<Calendar>());
	}

	@Override
	public Calendar getCalendar(ObjectId _id) {
		return get(_id, Calendar.class);
	}

	@Override
	public Calendar insertCalendar(Calendar obj) {
		return insert(obj, Calendar.class);
	}

	@Override
	public long deleteCalendar(ObjectId _id) {
		// TODO 完整性问题
		return delete(_id, Calendar.class);
	}

	@Override
	public long updateCalendar(BasicDBObject fu) {
		return update(fu, Calendar.class);
	}

	@Override
	public void addCalendarWorktime(BasicDBObject r, ObjectId _cal_id) {
		c(Calendar.class).updateOne(new BasicDBObject("_id", _cal_id),
				new BasicDBObject("$addToSet", new BasicDBObject("workTime", r)));
	}

	/**
	 * db.getCollection('calendar').updateOne({"workTime._id":ObjectId("5ad49b5a85e0fb335c355fae")},
	 * {$set:{"workTime.$":"aaa"}
	 * 
	 * })
	 */
	@Override
	public void updateCalendarWorkTime(BasicDBObject r) {
		c(Calendar.class).updateOne(new BasicDBObject("workTime._id", r.get("_id")),
				new BasicDBObject("$set", new BasicDBObject("workTime.$", r)));
	}

	@Override
	public void deleteCalendarWorkTime(ObjectId _id) {
		c(Calendar.class).updateOne(new BasicDBObject("workTime._id", _id),
				new BasicDBObject("$pull", new BasicDBObject("workTime", new BasicDBObject("_id", _id))));
	}

	@Override
	public List<Dictionary> getDictionary() {
		List<Dictionary> target = new ArrayList<Dictionary>();
		c(Dictionary.class).find().sort(new BasicDBObject("type", 1)).into(target);
		return target;
	}

	@Override
	public Dictionary insertResourceType(Dictionary dic) {
		return insert(dic, Dictionary.class);
	}

	@Override
	public long deleteDictionary(ObjectId _id) {
		return delete(_id, Dictionary.class);
	}

	@Override
	public long updateDictionary(BasicDBObject filterAndUpdate) {
		return update(filterAndUpdate, Dictionary.class);
	}

	@Override
	public Map<String, String> getDictionary(String type) {
		Map<String, String> result = new HashMap<String, String>();
		Iterable<Document> itr = c("dictionary").find(new BasicDBObject("type", type));
		itr.forEach(d -> result.put(d.getString("name") + " [" + d.getString("id") + "]",
				d.getString("id") + "#" + d.getString("name")));
		return result;
	}

	@Override
	public List<String> listDictionary(String type, String valueField) {
		return c("dictionary").distinct(valueField, (new BasicDBObject("type", type)), String.class)
				.into(new ArrayList<>());
	}

	@Override
	public List<AccountItem> getAccoutItemRoot() {
		return getAccoutItem(null);
	}

	@Override
	public List<AccountItem> getAccoutItem(ObjectId parent_id) {
		return queryAccountItem(new BasicDBObject("parent_id", parent_id));
	}

	@Override
	public long countAccoutItem(ObjectId _id) {
		return count(new BasicDBObject("parent_id", _id), AccountItem.class);
	}

	@Override
	public long countAccoutItemRoot() {
		return countAccoutItem(null);
	}

	@Override
	public AccountItem insertAccountItem(AccountItem ai) {
		return insert(ai, AccountItem.class);
	}

	@Override
	public long deleteAccountItem(ObjectId _id) {
		return delete(_id, AccountItem.class);
	}

	@Override
	public long updateAccountItem(BasicDBObject filterAndUpdate) {
		return update(filterAndUpdate, AccountItem.class);
	}

	@Override
	public List<AccountItem> queryAccountItem(BasicDBObject filter) {
		List<Bson> pipeline = new ArrayList<Bson>();

		if (filter != null) {
			pipeline.add(Aggregates.match(filter));
		}

		pipeline.add(Aggregates.lookup("accountItem", "_id", "parent_id", "_children"));

		pipeline.add(Aggregates.addFields(//
				new Field<BasicDBObject>("children", new BasicDBObject("$map", new BasicDBObject()
						.append("input", "$_children._id").append("as", "id").append("in", "$$id")))));

		pipeline.add(Aggregates.project(new BasicDBObject("_children", false)));

		pipeline.add(Aggregates.sort(new BasicDBObject("id", 1)));

		return c(AccountItem.class).aggregate(pipeline).into(new ArrayList<AccountItem>());
	}

	@Override
	public List<Message> listMessage(BasicDBObject condition, String userId) {
		BasicDBObject filter = (BasicDBObject) condition.get("filter");
		if (filter == null) {
			condition.put("filter", filter = new BasicDBObject());
		}
		filter.put("receiver", userId);

		ArrayList<Bson> pipeline = new ArrayList<Bson>();

		if (filter != null)
			pipeline.add(Aggregates.match(filter));

		pipeline.add(Aggregates.sort(new BasicDBObject("sendDate", -1)));

		Integer skip = (Integer) condition.get("skip");
		if (skip != null)
			pipeline.add(Aggregates.skip(skip));

		Integer limit = (Integer) condition.get("limit");
		if (limit != null)
			pipeline.add(Aggregates.limit(limit));

		appendUserInfoAndHeadPic(pipeline, "sender", "senderInfo", "senderHeadPic");

		appendUserInfo(pipeline, "receiver", "receiverInfo");

		return c(Message.class).aggregate(pipeline).into(new ArrayList<Message>());
	}

	@Override
	public long countMessage(BasicDBObject filter, String userId) {
		if (filter == null) {
			filter = new BasicDBObject();
		}
		filter.put("receiver", userId);
		return count(filter, Message.class);
	}

	@Override
	public int generateCode(String name, String key) {
		return super.generateCode(name, key);
	}

	@Override
	public List<TrackView> getTrackView() {
		return c(TrackView.class).find().into(new ArrayList<TrackView>());
	}

	@Override
	public TrackView insertTrackView(TrackView trackView) {
		return insert(trackView, TrackView.class);
	}

	@Override
	public long deleteTrackView(ObjectId _id) {
		return delete(_id, TrackView.class);
	}

	@Override
	public long updateTrackView(BasicDBObject filterAndUpdate) {
		return update(filterAndUpdate, TrackView.class);
	}

	@Override
	public List<News> getRecentNews(ObjectId _id, int count) {
		// TODO Auto-generated method stub
		ArrayList<News> result = new ArrayList<News>();
		try {
			result.add(new News().setDate(new SimpleDateFormat("yyyyMMdd").parse("20180422"))
					.setContent("样机试验结果满足了要求。样机开发完成。"));
			result.add(new News().setDate(new SimpleDateFormat("yyyyMMdd").parse("20180310"))
					.setContent("杨文韬下达了A模块结构研发，B模块结构研发等工作的计划。"));
			result.add(new News().setDate(new SimpleDateFormat("yyyyMMdd").parse("20180210")).setContent("方案研发完成。"));
			result.add(new News().setDate(new SimpleDateFormat("yyyyMMdd").parse("20180108"))
					.setContent("因客户要求的变化，涉及到A模块多处研发更改，部分组件必须重新研发。杨文韬发起项目变更，预计项目将延期30天。"));
			result.add(new News().setDate(new SimpleDateFormat("yyyyMMdd").parse("20171222"))
					.setContent("样机完成结构力学试验，试验结果满足技术规格要求。有关试验机构已出具试验报告。"));
		} catch (ParseException e) {
		}
		return result;
	}

	@Override
	public Date getCurrentCBSPeriod() {
		Document doc = c("project")
				.find(new Document("status",
						new Document("$nin",
								Arrays.asList(ProjectStatus.Created, ProjectStatus.Created, ProjectStatus.Terminated))))
				.sort(new Document("settlementDate", -1)).projection(new Document("settlementDate", 1)).first();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.add(java.util.Calendar.MONTH, -1);
		if (doc != null) {
			Date settlementDate = doc.getDate("settlementDate");
			if (settlementDate != null) {
				cal.setTime(settlementDate);
			}
		}
		return cal.getTime();
	}

}
