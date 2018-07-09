package com.bizvisionsoft.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.bizvisionsoft.service.WorkSpaceService;
import com.bizvisionsoft.service.model.Baseline;
import com.bizvisionsoft.service.model.Message;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.service.model.ProjectStatus;
import com.bizvisionsoft.service.model.ResourcePlan;
import com.bizvisionsoft.service.model.Result;
import com.bizvisionsoft.service.model.Work;
import com.bizvisionsoft.service.model.WorkInfo;
import com.bizvisionsoft.service.model.WorkLinkInfo;
import com.bizvisionsoft.service.model.Workspace;
import com.bizvisionsoft.service.model.WorkspaceGanttData;
import com.bizvisionsoft.service.tools.Util;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;

public class WorkSpaceServiceImpl extends BasicServiceImpl implements WorkSpaceService {

	@Override
	public int nextWBSIndex(BasicDBObject condition) {
		Document doc = c("workspace").find(condition).sort(new BasicDBObject("index", -1))
				.projection(new BasicDBObject("index", 1)).first();
		return Optional.ofNullable(doc).map(d -> d.getInteger("index", 0)).orElse(0) + 1;
	}

	public WorkInfo getWorkInfo(ObjectId _id) {
		return get(_id, WorkInfo.class);
	}

	@Override
	public List<WorkInfo> createTaskDataSet(BasicDBObject condition) {
		List<Bson> pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.match(condition));
		pipeline.add(Aggregates.lookup("project", "project_id", "_id", "project"));
		pipeline.add(Aggregates.unwind("$project"));
		List<Field<?>> fields = new ArrayList<Field<?>>();
		fields.add(new Field<String>("projectName", "$project.name"));
		fields.add(new Field<String>("projectNumber", "$project.id"));
		pipeline.add(Aggregates.addFields(fields));
		pipeline.add(Aggregates.project(new BasicDBObject("project", false)));
		pipeline.add(Aggregates.sort(new BasicDBObject("index", 1)));
		return c(WorkInfo.class).aggregate(pipeline).into(new ArrayList<WorkInfo>());
	}

	@Override
	public List<WorkLinkInfo> createLinkDataSet(BasicDBObject condition) {
		return c(WorkLinkInfo.class).find(condition).into(new ArrayList<WorkLinkInfo>());
	}

	@Override
	public WorkInfo insertWork(WorkInfo work) {
		return insert(work, WorkInfo.class);
	}

	@Override
	public WorkLinkInfo insertLink(WorkLinkInfo link) {
		return insert(link, WorkLinkInfo.class);
	}

	@Override
	public long updateWork(BasicDBObject filterAndUpdate) {
		return update(filterAndUpdate, WorkInfo.class);
	}

	@Override
	public long updateLink(BasicDBObject filterAndUpdate) {
		return update(filterAndUpdate, WorkLinkInfo.class);
	}

	@Override
	public long deleteWork(ObjectId _id) {
		return delete(_id, WorkInfo.class);
	}

	@Override
	public long deleteLink(ObjectId _id) {
		return delete(_id, WorkLinkInfo.class);
	}

	@Override
	public Result checkout(Workspace workspace, String userId, Boolean cancelCheckoutSubSchedule) {
		// 获取所有需检出的工作ID inputIds为需要复制到Workspace中的工作。inputIdHasWorks为需要进行校验的工作
		List<ObjectId> inputIds = new ArrayList<ObjectId>();
		List<ObjectId> inputIdHasWorks = new ArrayList<ObjectId>();
		// 判断是否为项目
		if (workspace.getWork_id() == null) {
			// 获取项目下所有工作
			inputIds = c(Work.class)
					.distinct("_id", new BasicDBObject("project_id", workspace.getProject_id()), ObjectId.class)
					.into(new ArrayList<ObjectId>());
			inputIdHasWorks.addAll(inputIds);
		} else {
			// 获取检出工作及其下级工作
			inputIdHasWorks.add(workspace.getWork_id());
			inputIdHasWorks = getDesentItems(inputIdHasWorks, "work", "parent_id");
			// 获取检出工作的下级工作
			inputIds.addAll(inputIdHasWorks);
			inputIds.remove(workspace.getWork_id());
		}
		// 判断是否需要检查本计划被其他人员检出
		if (Boolean.TRUE.equals(cancelCheckoutSubSchedule)) {
			// 获取要检出工作及该工作下级工作的工作区id，并进行清除
			List<ObjectId> spaceIds = c("work").distinct("space_id",
					new BasicDBObject("_id", new BasicDBObject("$in", inputIdHasWorks)), ObjectId.class)
					.into(new ArrayList<ObjectId>());
			spaceIds.add(workspace.getSpace_id());
			cleanWorkspace(spaceIds);
		} else {
			// 获取被其他人员检出的计划
			List<Bson> pipeline = new ArrayList<Bson>();
			pipeline.add(Aggregates.match(new BasicDBObject("_id", new BasicDBObject().append("$in", inputIdHasWorks))
					.append("checkoutBy", new BasicDBObject("$ne", null))));
			pipeline.add(Aggregates.lookup("user", "checkoutBy", "userId", "user"));
			pipeline.add(Aggregates.unwind("$user"));
			pipeline.add(Aggregates.project(new BasicDBObject("name", Boolean.TRUE).append("username", "$user.name")));

			BasicDBObject checkout = c("work").aggregate(pipeline, BasicDBObject.class).first();
			if (checkout != null) {
				// 如被其他人员检出,则提示给当前用户
				Result result = Result.checkoutError("计划正在进行计划编辑。", Result.CODE_HASCHECKOUTSUB);
				result.setResultDate(checkout);
				return result;
			}
		}

		// 生成工作区标识
		ObjectId space_id = new ObjectId();

		// 检出项目时，给项目标记检出人和工作区标记
		if (workspace.getWork_id() == null) {
			c("project").updateOne(new BasicDBObject("_id", workspace.getProject_id()),
					new BasicDBObject("$set", new BasicDBObject("checkoutBy", userId).append("space_id", space_id)));
		}
		// 给work集合中检出的工作增加检出人和工作区标记
		c("work").updateMany(new BasicDBObject("_id", new BasicDBObject("$in", inputIdHasWorks)),
				new BasicDBObject("$set", new BasicDBObject("checkoutBy", userId).append("space_id", space_id)));

		// 获取需检出到工作区的Work到List中,并为获取的工作添加工作区标记
		List<Bson> pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.match(new BasicDBObject("_id", new BasicDBObject().append("$in", inputIds))));
		pipeline.add(Aggregates.addFields(new Field<ObjectId>("space_id", space_id)));
		pipeline.add(Aggregates.project(new BasicDBObject("checkoutBy", false)));
		List<Document> works = c("work").aggregate(pipeline).into(new ArrayList<Document>());
		if (works.size() > 0) {

			// 将检出的工作存入workspace集合中
			c("workspace").insertMany(works);

			// 获取检出的工作搭接关系，并存入worklinksspace集合中
			pipeline = new ArrayList<Bson>();
			pipeline.add(Aggregates.match(new BasicDBObject("source", new BasicDBObject("$in", inputIds))
					.append("target", new BasicDBObject("$in", inputIds))));
			pipeline.add(Aggregates.addFields(new Field<ObjectId>("space_id", space_id)));

			List<Document> workLinkInfos = c("worklinks").aggregate(pipeline).into(new ArrayList<Document>());

			if (workLinkInfos.size() > 0) {
				c("worklinksspace").insertMany(workLinkInfos);
			}
		}
		return Result.checkoutSuccess("检出成功。");
	}

	@Override
	public Result schedulePlanCheck(Workspace workspace, Boolean checkManageItem) {
		// 获取需检查的节点。
		if (checkManageItem) {
			List<Bson> pipeline = new ArrayList<Bson>();
			pipeline.add(Aggregates.match(new BasicDBObject("space_id", workspace.getSpace_id()).append("manageLevel",
					new BasicDBObject("$ne", null))));
			pipeline.add(Aggregates.lookup("work", "_id", "_id", "work"));
			pipeline.add(Aggregates.unwind("$work"));
			pipeline.add(Aggregates.project(new BasicDBObject("name", Boolean.TRUE).append("wpf",
					new BasicDBObject("$gt", new String[] { "$planFinish", "$work.planFinish" }))));
			pipeline.add(Aggregates.match(new BasicDBObject("wpf", Boolean.TRUE)));
			WorkInfo workInfo = c(WorkInfo.class).aggregate(pipeline).first();
			if (workInfo != null) {
				Result result = Result.checkoutError("管理节点完成时间超过限定。", Result.CODE_UPDATEMANAGEITEM);
				result.data = new BasicDBObject("name", workInfo.getText());
				return result;
			}
		} else {
			Document doc = c("workspace")
					.aggregate(Arrays.asList(new Document("$match", new Document("_id", workspace.getSpace_id())),
							new Document("$group",
									new Document("_id", null).append("finish", new Document("$max", "$planFinish")))))
					.first();
			if (workspace.getWork_id() != null) {
				Work work = new WorkServiceImpl().getWork(workspace.getWork_id());
				if (work.getPlanFinish().after(doc.getDate("finish"))) {
					Result result = Result.checkoutError("完成时间超过阶段限定。", Result.CODE_UPDATEMANAGEITEM);
					result.data = new BasicDBObject("name", work.getText());
					return result;
				}
			} else {
				Project project = new ProjectServiceImpl().get(workspace.getProject_id());
				if (project.getPlanFinish().after(doc.getDate("finish"))) {
					Result result = Result.checkoutError("完成时间超过项目限定。", Result.CODE_UPDATEMANAGEITEM);
					result.data = new BasicDBObject("name", project.getName());
					return result;
				}
			}
		}
		// 返回检查结果
		return Result.checkoutSuccess("已通过检查。");
	}

	@Override
	public Result checkin(Workspace workspace) {
		if (workspace.getSpace_id() == null) {
			return Result.checkoutError("提交失败。", Result.CODE_ERROR);
		}

		new ProjectServiceImpl().createBaseline(
				new Baseline().setProject_id(workspace.getProject_id()).setCreationDate(new Date()).setName("修改进度计划"));

		List<ObjectId> workIds = c(Work.class)
				.distinct("_id", new BasicDBObject("space_id", workspace.getSpace_id()), ObjectId.class)
				.into(new ArrayList<ObjectId>());
		workIds.remove(workspace.getWork_id());

		List<ObjectId> workspaceIds = c(WorkInfo.class)
				.distinct("_id", new BasicDBObject("space_id", workspace.getSpace_id()), ObjectId.class)
				.into(new ArrayList<ObjectId>());

		// 获取插入集合
		List<ObjectId> insertIds = new ArrayList<ObjectId>();
		insertIds.addAll(workspaceIds);
		insertIds.removeAll(workIds);

		// 获取删除集合
		List<ObjectId> deleteIds = new ArrayList<ObjectId>();
		deleteIds.addAll(workIds);
		deleteIds.removeAll(workspaceIds);

		// 获取修改集合
		List<ObjectId> updateIds = new ArrayList<ObjectId>();
		updateIds.addAll(workspaceIds);
		updateIds.removeAll(insertIds);

		if (!deleteIds.isEmpty()) {
			// 根据删除集合删除Work
			c(Work.class).deleteMany(new BasicDBObject("_id", new BasicDBObject("$in", deleteIds)));
			// 根据删除集合删除资源计划
			c(ResourcePlan.class).deleteMany(new BasicDBObject("work_id", new BasicDBObject("$in", deleteIds)));
		}

		// 根据插入集合插入Work
		if (!insertIds.isEmpty()) {
			ArrayList<Document> insertDoc = c("workspace")
					.find(new BasicDBObject("_id", new BasicDBObject("$in", insertIds)))
					.into(new ArrayList<Document>());
			c("work").insertMany(insertDoc);
		}

		Project project = c(Project.class).find(new Document("_id", workspace.getProject_id())).first();
		final List<Message> messages = new ArrayList<>();

		c("workspace").find(new BasicDBObject("_id", new BasicDBObject("$in", updateIds))).forEach((Document d) -> {
			// 更新Work
			Object _id = d.get("_id");
			d.remove("_id");
			d.remove("space_id");
			// TODO 下达状态，发送修改通知
			boolean distributed = d.getBoolean("distributed", false);
			if (distributed) {
				Document doc = c("work").find(new Document("_id", _id)).first();
				Date oldActualStart = doc.getDate("actualStart");
				Date newActualStart = d.getDate("actualStart");
				Date oldActualFinish = doc.getDate("actualFinish");
				Date newActualFinish = d.getDate("actualFinish");
				if (!oldActualStart.equals(newActualStart) || !oldActualFinish.equals(newActualFinish)) {
					String chargerId = doc.getString("chargerId");
					messages.add(Message.newInstance("工作计划下达通知", "您负责的项目 " + project.getName() + "，工作 "
							+ doc.getString("fullName") + "，预计从"
							+ new SimpleDateFormat(Util.DATE_FORMAT_DATE).format(doc.getDate("planStart")) + "开始到"
							+ new SimpleDateFormat(Util.DATE_FORMAT_DATE).format(doc.getDate("planFinish")) + "结束",
							workspace.getCheckoutBy(), chargerId, null));
					String assignerId = doc.getString("assignerId");
					messages.add(Message.newInstance("工作计划下达通知", "您指派的项目 " + project.getName() + "，工作 "
							+ doc.getString("fullName") + "，预计从"
							+ new SimpleDateFormat(Util.DATE_FORMAT_DATE).format(doc.getDate("planStart")) + "开始到"
							+ new SimpleDateFormat(Util.DATE_FORMAT_DATE).format(doc.getDate("planFinish")) + "结束",
							workspace.getCheckoutBy(), assignerId, null));
				}
			}
			c("work").updateOne(new BasicDBObject("_id", _id), new BasicDBObject("$set", d));
		});
		if (messages.size() > 0)
			sendMessages(messages);

		List<ObjectId> deleteResourcePlanId = new ArrayList<ObjectId>();
		Set<ObjectId> updateWorksId = new HashSet<ObjectId>();

		List<? extends Bson> pipeline = Arrays
				.asList(new Document("$match", new Document("work_id", new Document("$in", updateIds))),
						new Document("$lookup",
								new Document("from", "workspace").append("localField", "work_id")
										.append("foreignField", "_id").append("as", "workspace")),
						new Document("$unwind", "$workspace"),
						new Document("$addFields",
								new Document("delete", new Document("$or",
										Arrays.asList(new Document("$lt", Arrays.asList("$id", "$workspace.planStart")),
												new Document("$gt", Arrays.asList("$id", "$workspace.planFinish")))))),
						new Document("$match", new Document("delete", true)),
						new Document("$project", new Document("_id", true).append("work_id", true)));
		c("resourcePlan").aggregate(pipeline).forEach((Document d) -> {
			deleteResourcePlanId.add(d.getObjectId("_id"));
			updateWorksId.add(d.getObjectId("work_id"));
		});

		if (!deleteResourcePlanId.isEmpty()) {
			c("resourcePlan").deleteMany(new BasicDBObject("_id", new BasicDBObject("$in", deleteResourcePlanId)));
		}

		updateWorksId.forEach(_id -> updateWorkPlanWorks(_id));

		// 获取worklinksspace中的记录
		List<Document> worklinks = c("worklinksspace").find(new BasicDBObject("space_id", workspace.getSpace_id()))
				.into(new ArrayList<Document>());

		// 删除worklinks中的记录
		if (!workIds.isEmpty()) {
			c("worklinks").deleteMany(new BasicDBObject("source", new BasicDBObject("$in", workIds)).append("target",
					new BasicDBObject("$in", workIds)));
		}

		// 将获取的worklinksspace中的记录插入worklinks
		if (!worklinks.isEmpty()) {
			c("worklinks").insertMany(worklinks);
		}

		if (Result.CODE_WORK_SUCCESS == cleanWorkspace(Arrays.asList(workspace.getSpace_id())).code) {
			if (ProjectStatus.Created.equals(project.getStatus())) {
				sendMessage("项目进度计划编制完成", "您负责的项目" + project.getName() + "已完成进度计划的制定。", workspace.getCheckoutBy(),
						project.getPmId(), null);
			} else {
				List<ObjectId> parentIds = c("obs")
						.distinct("_id", new BasicDBObject("scope_id", workspace.getProject_id()), ObjectId.class)
						.into(new ArrayList<>());
				List<ObjectId> ids = getDesentItems(parentIds, "obs", "parent_id");
				ArrayList<String> memberIds = c("obs")
						.distinct("managerId", new BasicDBObject("_id", new BasicDBObject("$in", ids))
								.append("managerId", new BasicDBObject("$ne", null)), String.class)
						.into(new ArrayList<>());

				sendMessage("项目进度计划已更新", "您参与的项目" + project.getName() + "进度计划已调整。", workspace.getCheckoutBy(),
						memberIds, null);
			}
			return Result.checkoutSuccess("已成功提交。");
		} else {
			return Result.checkoutError("提交失败。", Result.CODE_ERROR);
		}
	}

	private void updateWorkPlanWorks(ObjectId work_id) {
		if (work_id != null) {
			// TODO 修改计算方式
			List<? extends Bson> pipeline = Arrays.asList(new Document("$match", new Document("work_id", work_id)),
					new Document("$addFields",
							new Document("planQty",
									new Document("$sum", Arrays.asList("$planBasicQty", "$planOverTimeQty")))),
					new Document("$group",
							new Document("_id", "$work_id").append("planWorks", new Document("$sum", "$planQty"))));

			double planWorks = Optional.ofNullable(c("resourcePlan").aggregate(pipeline).first())
					.map(d -> (Double) d.get("planWorks")).map(p -> p.doubleValue()).orElse(0d);
			c(Work.class).updateOne(new Document("_id", work_id),
					new Document("$set", new Document("planWorks", planWorks)));
		}
	}

	@Override
	public Result cancelCheckout(Workspace workspace) {
		if (workspace.getSpace_id() == null) {
			return Result.checkoutError("撤销失败。", Result.CODE_ERROR);
		}

		if (Result.CODE_WORK_SUCCESS == cleanWorkspace(Arrays.asList(workspace.getSpace_id())).code) {
			return Result.checkoutSuccess("已成功撤销。");
		} else {
			return Result.checkoutError("撤销失败。", Result.CODE_ERROR);
		}
	}

	private Result cleanWorkspace(List<ObjectId> spaceIds) {
		c(WorkInfo.class).deleteMany(new BasicDBObject("space_id", new BasicDBObject("$in", spaceIds)));

		c(WorkLinkInfo.class).deleteMany(new BasicDBObject("space_id", new BasicDBObject("$in", spaceIds)));

		c("project").updateOne(new BasicDBObject("space_id", new BasicDBObject("$in", spaceIds)),
				new BasicDBObject("$unset", new BasicDBObject("checkoutBy", true).append("space_id", true)));

		c("work").updateMany(new BasicDBObject("space_id", new BasicDBObject("$in", spaceIds)),
				new BasicDBObject("$unset", new BasicDBObject("checkoutBy", true).append("space_id", true)));

		return Result.checkoutSuccess("已完成撤销成功。");
	}

	@Override
	public List<WorkInfo> createComparableWorkDataSet(ObjectId space_id) {
		List<? extends Bson> pipeline = Arrays.asList(
				new Document().append("$match", new Document().append("space_id", space_id)),
				new Document().append("$lookup",
						new Document().append("from", "work").append("localField", "_id").append("foreignField", "_id")
								.append("as", "work")),
				new Document().append("$unwind",
						new Document().append("path", "$work").append("preserveNullAndEmptyArrays", true)),
				new Document().append("$addFields",
						new Document().append("planStart1", "$work.planStart").append("planFinish1", "$work.planFinish")
								.append("actualStart1", "$work.actualStart")
								.append("actualFinish1", "$work.actualFinish")),
				new Document().append("$project", new Document().append("work", false)));

		return c(WorkInfo.class).aggregate(pipeline).into(new ArrayList<WorkInfo>());
	}

	@Override
	public Result updateGanttData(WorkspaceGanttData ganttData) {
		// TODO Auto-generated method stub
		ObjectId space_id = ganttData.getSpace_id();

		c(WorkInfo.class).deleteMany(new Document("space_id", space_id));

		c(WorkLinkInfo.class).deleteMany(new Document("space_id", space_id));

		List<WorkInfo> tasks = ganttData.getTasks();
		List<WorkLinkInfo> links = ganttData.getLinks();
		if (tasks.size() > 0)
			c(WorkInfo.class).insertMany(tasks);

		if (links.size() > 0)
			c(WorkLinkInfo.class).insertMany(links);
		return new Result();
	}
}
