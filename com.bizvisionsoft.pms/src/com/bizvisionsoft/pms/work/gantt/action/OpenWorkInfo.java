package com.bizvisionsoft.pms.work.gantt.action;

import org.eclipse.swt.widgets.Event;

import com.bizivisionsoft.widgets.gantt.GanttEvent;
import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.service.model.ProjectStatus;
import com.bizvisionsoft.service.model.Work;

public class OpenWorkInfo {

	@Inject
	private IBruiService bruiService;

	@Execute
	public void execute(@MethodParam(Execute.PARAM_CONTEXT) IBruiContext context,
			@MethodParam(Execute.PARAM_EVENT) Event event) {
		Work work = ((Work) ((GanttEvent) event).task);
		if (work.isStage()) {
			// TODO ����״̬����������
			if (ProjectStatus.Created.equals(work.getStatus())) {
				bruiService.switchPage("�׶���ҳ��������", work.get_id().toHexString());
			} else if (ProjectStatus.Processing.equals(work.getStatus())) {
				bruiService.switchPage("�׶���ҳ��ִ�У�", work.get_id().toHexString());
			} else if (ProjectStatus.Closing.equals(work.getStatus())) {
				bruiService.switchPage("�׶���ҳ����β��", work.get_id().toHexString());
			} else if (ProjectStatus.Closed.equals(work.getStatus())) {
				bruiService.switchPage("�׶���ҳ���رգ�", work.get_id().toHexString());
			}
		}
	}

}