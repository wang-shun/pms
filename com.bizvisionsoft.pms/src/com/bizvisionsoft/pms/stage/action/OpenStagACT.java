package com.bizvisionsoft.pms.stage.action;

import org.eclipse.swt.widgets.Event;

import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.pms.work.action.OpenWorkPackage;
import com.bizvisionsoft.service.WorkService;
import com.bizvisionsoft.service.model.ProjectStatus;
import com.bizvisionsoft.service.model.Work;
import com.bizvisionsoft.serviceconsumer.Services;

public class OpenStagACT extends OpenWorkPackage {

	@Inject
	private IBruiService bruiService;

	@Execute
	public void execute(@MethodParam(Execute.PARAM_CONTEXT) IBruiContext context,
			@MethodParam(Execute.PARAM_EVENT) Event event) {
		context.selected(em -> {
			Work stage = Services.get(WorkService.class).getOpenStage(((Work) em).get_id(),
					bruiService.getCurrentUserId());
			if (stage != null && stage.isStage()) {
				if (ProjectStatus.Created.equals(stage.getStatus())) {
					bruiService.switchPage("�׶���ҳ��������", stage.get_id().toHexString());
				} else if (ProjectStatus.Processing.equals(stage.getStatus())) {
					bruiService.switchPage("�׶���ҳ��ִ�У�", stage.get_id().toHexString());
				} else if (ProjectStatus.Closing.equals(stage.getStatus())) {
					bruiService.switchPage("�׶���ҳ����β��", stage.get_id().toHexString());
				} else if (ProjectStatus.Closed.equals(stage.getStatus())) {
					bruiService.switchPage("�׶���ҳ���رգ�", stage.get_id().toHexString());
				} else if (ProjectStatus.Suspended.equals(stage.getStatus())) {
					bruiService.switchPage("�׶���ҳ����ͣ��", stage.get_id().toHexString());
				} else if (ProjectStatus.Terminated.equals(stage.getStatus())) {
					bruiService.switchPage("�׶���ҳ����ֹ��", stage.get_id().toHexString());
				}
			}

		});
	}

}
