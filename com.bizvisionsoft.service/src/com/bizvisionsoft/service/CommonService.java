package com.bizvisionsoft.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.bizvisionsoft.service.model.AccountIncome;
import com.bizvisionsoft.service.model.AccountItem;
import com.bizvisionsoft.service.model.Calendar;
import com.bizvisionsoft.service.model.Certificate;
import com.bizvisionsoft.service.model.ChangeProcess;
import com.bizvisionsoft.service.model.Dictionary;
import com.bizvisionsoft.service.model.Equipment;
import com.bizvisionsoft.service.model.Message;
import com.bizvisionsoft.service.model.NewMessage;
import com.bizvisionsoft.service.model.ResourceType;
import com.bizvisionsoft.service.model.TrackView;
import com.mongodb.BasicDBObject;

@Path("/common")
public interface CommonService {

	@GET
	@Path("/workTag/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("工作标签/" + DataSet.LIST)
	public List<String> listWorkTag();

	@POST
	@Path("/msg/userId/{userId}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("消息收件箱/" + DataSet.LIST)
	public List<Message> listMessage(@MethodParam(MethodParam.CONDITION) BasicDBObject condition,
			@MethodParam(MethodParam.CURRENT_USER_ID) @PathParam("userId") String userId);

	@POST
	@Path("/msg/userId/{userId}/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("消息收件箱/" + DataSet.COUNT)
	public long countMessage(@MethodParam(MethodParam.FILTER) BasicDBObject filter,
			@MethodParam(MethodParam.CURRENT_USER_ID) @PathParam("userId") String userId);

	@PUT
	@Path("/msg/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("消息收件箱/" + DataSet.UPDATE)
	public long updateMessage(BasicDBObject filterAndUpdate);

	@POST
	@Path("/cert/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("执业资格列表/" + DataSet.LIST)
	public List<Certificate> getCertificates(@MethodParam(MethodParam.CONDITION) BasicDBObject condition);

	@POST
	@Path("/cert/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("执业资格列表/" + DataSet.COUNT)
	public long countCertificate(@MethodParam(MethodParam.FILTER) BasicDBObject filter);

	@POST
	@Path("/cert/names/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("执业资格选择器列表/" + DataSet.LIST)
	public List<String> getCertificateNames();

	@POST
	@Path("/cert/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("执业资格列表/" + DataSet.INSERT)
	public Certificate insertCertificate(@MethodParam(MethodParam.OBJECT) Certificate cert);

	@DELETE
	@Path("/cert/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("执业资格列表/" + DataSet.DELETE)
	public long deleteCertificate(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/cert/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("执业资格列表/" + DataSet.UPDATE)
	public long updateCertificate(BasicDBObject filterAndUpdate);
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	@POST
	@Path("/restype/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("资源类型/" + DataSet.LIST)
	public List<ResourceType> getResourceType();

	@POST
	@Path("/restype/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("资源类型/" + DataSet.INSERT)
	public ResourceType insertResourceType(@MethodParam(MethodParam.OBJECT) ResourceType resourceType);

	@DELETE
	@Path("/restype/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("资源类型/" + DataSet.DELETE)
	public long deleteResourceType(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/restype/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("资源类型/" + DataSet.UPDATE)
	public long updateResourceType(BasicDBObject filterAndUpdate);

	@POST
	@Path("/restype/{_id}/er")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<Equipment> getERResources(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/restype/{_id}/er/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countERResources(@PathParam("_id") ObjectId _id);

	@GET
	@Path("/restype/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public ResourceType getResourceType(@PathParam("_id") ObjectId _id);
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	@POST
	@Path("/euip/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "设备设施/" + DataSet.LIST, "设备设施选择表格/" + DataSet.LIST })
	public List<Equipment> getEquipments();

	@POST
	@Path("/euip/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("设备设施/" + DataSet.INSERT)
	public Equipment insertEquipment(@MethodParam(MethodParam.OBJECT) Equipment cert);

	@DELETE
	@Path("/euip/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("设备设施/" + DataSet.DELETE)
	public long deleteEquipment(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/euip/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("设备设施/" + DataSet.UPDATE)
	public long updateEquipment(BasicDBObject filterAndUpdate);

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	@POST
	@Path("/cal/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "工作日历/" + DataSet.LIST, "工作日历选择表格/" + DataSet.LIST })
	public List<Calendar> getCalendars();

	@GET
	@Path("/cal/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Calendar getCalendar(@PathParam("_id") ObjectId _id);

	@POST
	@Path("/cal/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("工作日历/" + DataSet.INSERT)
	public Calendar insertCalendar(@MethodParam(MethodParam.OBJECT) Calendar obj);

	@DELETE
	@Path("/cal/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("工作日历/" + DataSet.DELETE)
	public long deleteCalendar(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@POST
	@Path("/cal/{_id}/wt/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void addCalendarWorktime(BasicDBObject r, @PathParam("_id") ObjectId _cal_id);

	@PUT
	@Path("/cal/wt/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void updateCalendarWorkTime(BasicDBObject r);

	@PUT
	@Path("/cal/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("工作日历/" + DataSet.UPDATE)
	public long updateCalendar(BasicDBObject filterAndUpdate);

	@DELETE
	@Path("/cal/wt/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void deleteCalendarWorkTime(@PathParam("_id") ObjectId _id);

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	@POST
	@Path("/dict/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("名称字典/" + DataSet.LIST)
	public List<Dictionary> getDictionary();

	@POST
	@Path("/dict/{type}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Map<String, String> getDictionary(@PathParam("type") String type);

	@POST
	@Path("/dict/idname/{type}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Map<String, String> getDictionaryIdNamePair(@PathParam("type") String type);

	@POST
	@Path("/dict/{type}/{valueField}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<String> listDictionary(@PathParam("type") String type, @PathParam("valueField") String valueField);

	@POST
	@Path("/dict/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("名称字典/" + DataSet.INSERT)
	public Dictionary insertResourceType(@MethodParam(MethodParam.OBJECT) Dictionary resourceType);

	@DELETE
	@Path("/dict/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("名称字典/" + DataSet.DELETE)
	public long deleteDictionary(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/dict/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("名称字典/" + DataSet.UPDATE)
	public long updateDictionary(BasicDBObject filterAndUpdate);

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	@POST
	@Path("/accountItem/root/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "费用类科目/" + DataSet.LIST })
	public List<AccountItem> getAccoutItemRoot();

	@POST
	@Path("/accountIncome/root/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "损益类科目/" + DataSet.LIST })
	public List<AccountIncome> getAccoutIncomeRoot();

	@POST
	@Path("/accountItem/root/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countAccoutItemRoot();

	@POST
	@Path("/accountIncome/root/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countAccoutIncomeRoot();

	@POST
	@Path("/accountItem/parent/{id}/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<AccountItem> getAccoutItem(@PathParam("id")  String id);

	@POST
	@Path("/accountItem/parent/{id}/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countAccoutItem(@PathParam("id") String id);

	@POST
	@Path("/accountIncome/parent/{id}/count")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public long countAccoutIncome(@PathParam("id") String id);

	@POST
	@Path("/accountItem/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<AccountItem> queryAccountItem(BasicDBObject filter);

	@POST
	@Path("/accountIncome/ds/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<AccountIncome> queryAccountIncome(BasicDBObject filter);

	@POST
	@Path("/accountItem/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "费用类科目/" + DataSet.INSERT })
	public AccountItem insertAccountItem(@MethodParam(MethodParam.OBJECT) AccountItem ai);

	@POST
	@Path("/accountIncome/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "损益类科目/" + DataSet.INSERT })
	public AccountIncome insertAccountIncome(@MethodParam(MethodParam.OBJECT) AccountIncome ai);

	@DELETE
	@Path("/accountItem/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "费用类科目/" + DataSet.DELETE })
	public long deleteAccountItem(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@DELETE
	@Path("/accountIncome/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "损益类科目/" + DataSet.DELETE })
	public long deleteAccountIncome(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/accountItem/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "费用类科目/" + DataSet.UPDATE })
	public long updateAccountItem(BasicDBObject filterAndUpdate);

	@PUT
	@Path("/accountIncome/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "损益类科目/" + DataSet.UPDATE })
	public long updateAccountIncome(BasicDBObject filterAndUpdate);

	@POST
	@Path("/gencode/{name}/{key}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public int generateCode(@PathParam("name") String name, @PathParam("key") String key);

	///////////////////////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/track/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet({ "视图和工作包列表/" + DataSet.LIST, "视图和工作包选择器/" + DataSet.LIST })
	public List<TrackView> getTrackView();

	@POST
	@Path("/track/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("视图和工作包列表/" + DataSet.INSERT)
	public TrackView insertTrackView(@MethodParam(MethodParam.OBJECT) TrackView trackView);

	@DELETE
	@Path("/track/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("视图和工作包列表/" + DataSet.DELETE)
	public long deleteTrackView(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/track/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("视图和工作包列表/" + DataSet.UPDATE)
	public long updateTrackView(BasicDBObject filterAndUpdate);
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GET
	@Path("/currentcbsperiod")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Date getCurrentCBSPeriod();

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	@POST
	@Path("/changeprocess/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("变更审核/" + DataSet.LIST)
	public List<ChangeProcess> createChangeProcessDataSet();

	@GET
	@Path("/changeprocess/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("变更审核/" + DataSet.INPUT)
	public ChangeProcess getChangeProcess(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@POST
	@Path("/changeprocess/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("变更审核/" + DataSet.INSERT)
	public ChangeProcess insertChangeProcess(@MethodParam(MethodParam.OBJECT) ChangeProcess changeProcess);

	@DELETE
	@Path("/changeprocess/_id/{_id}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("变更审核/" + DataSet.DELETE)
	public long deleteChangeProcess(@PathParam("_id") @MethodParam(MethodParam._ID) ObjectId _id);

	@PUT
	@Path("/changeprocess/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("变更审核/" + DataSet.UPDATE)
	public long updateChangeProcess(BasicDBObject filterAndUpdate);

	@POST
	@Path("/strudata/ds")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public List<Document> listStructuredData(BasicDBObject query);

	@POST
	@Path("/strudata/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void insertStructuredData(List<Document> result);

	@PUT
	@Path("/strudata/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void updateStructuredData(BasicDBObject fu);

	@POST
	@Path("/newMessage/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void sendMessage(NewMessage msg);

	@GET
	@Path("/userId/{userId}/budget/mywork")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@DataSet("我的工作/budget")
	public boolean hasSomethingNewOfMyWork(
			@PathParam("userId") @MethodParam(MethodParam.CURRENT_USER_ID) String userId);

	@POST
	@Path("/tools/syncOrgFullName")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void syncOrgFullName();

	@GET
	@Path("/setting/{name}")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public Document getSetting(@PathParam("name") String name);

	@PUT
	@Path("/setting/")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public void updateSetting(Document setting);

}
