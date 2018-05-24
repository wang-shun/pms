package com.bizvisionsoft.pms.risk;

import java.util.List;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.service.DataSet;
import com.bizvisionsoft.annotations.md.service.ServiceParam;
import com.bizvisionsoft.annotations.ui.common.Init;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.bruiengine.service.BruiAssemblyContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.service.RiskService;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.service.model.RBSItem;
import com.bizvisionsoft.service.model.Work;
import com.bizvisionsoft.serviceconsumer.Services;
import com.mongodb.BasicDBObject;

public class ProjectRiskDS {

	@Inject
	private BruiAssemblyContext context;

	@Inject
	private IBruiService brui;

	private ObjectId project_id;

	@Init
	private void init() {
		Object input = context.getRootInput();
		if (input instanceof Project) {
			this.project_id = ((Project) input).get_id();
		} else if (input instanceof Work) {
			this.project_id = ((Work) input).getProject_id();
		} else {
			throw new RuntimeException("ProjectRiskDS���ݼ�ֻ��������Ŀ�͹�����������");
		}
	}

	@DataSet(DataSet.LIST)
	public List<RBSItem> listRootRBSItems(@ServiceParam(ServiceParam.CONDITION) BasicDBObject condition) {
		BasicDBObject filter = (BasicDBObject) condition.get("filter");
		if (filter == null) {
			filter = new BasicDBObject();
			condition.append("filter", filter);
		}
		condition.append("sort", new BasicDBObject("rbsType.id",1).append("index", 1));
		filter.append("project_id", project_id).append("parent_id", null);
		return Services.get(RiskService.class).listRBSItem(condition);
	}

	@DataSet(DataSet.COUNT)
	public long countRootRBSItem(@ServiceParam(ServiceParam.FILTER) BasicDBObject filter) {
		if (filter == null) {
			filter = new BasicDBObject();
		}
		filter.append("project_id", project_id).append("parent_id", null);
		return Services.get(RiskService.class).countRBSItem(filter);
	}

	@DataSet(DataSet.INSERT)
	public RBSItem insertRBSItem(@ServiceParam(ServiceParam.OBJECT) RBSItem item) {
		return Services.get(RiskService.class).insertRBSItem(item);
	}
	
	@DataSet(DataSet.DELETE)
	private long delete(@ServiceParam(ServiceParam._ID) ObjectId _id) {
		return Services.get(RiskService.class).deleteRBSItem(_id);
	}

	@DataSet(DataSet.UPDATE)
	private long update(BasicDBObject filterAndUpdate) {
		return Services.get(RiskService.class).updateRBSItem(filterAndUpdate);
	}

}