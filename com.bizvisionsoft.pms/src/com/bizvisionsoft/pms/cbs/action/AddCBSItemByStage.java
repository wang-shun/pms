package com.bizvisionsoft.pms.cbs.action;

import java.util.List;

import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.pms.cbs.assembly.BudgetCBS;
import com.bizvisionsoft.service.CBSService;
import com.bizvisionsoft.service.model.CBSItem;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.serviceconsumer.Services;

public class AddCBSItemByStage {

	@Inject
	private IBruiService bruiService;

	@Execute
	public void execute(@MethodParam(Execute.PARAM_CONTEXT) IBruiContext context) {
		Object rootInput = context.getRootInput();
		if (rootInput instanceof Project) {
			Project project = (Project) rootInput;
			List<CBSItem> cbsItems = Services.get(CBSService.class).addCBSItemByStage(project.getCBS_id(),
					project.get_id());
			BudgetCBS budgetCBS = (BudgetCBS) context.getChildContextByName("cbs").getContent();
			CBSItem cbsRoot = (CBSItem) budgetCBS.getViewerInput().get(0);
			cbsRoot.addChild(cbsItems);
			budgetCBS.refresh(cbsRoot);
			budgetCBS.expandToLevel(cbsRoot, -1);
		}
	}
}
