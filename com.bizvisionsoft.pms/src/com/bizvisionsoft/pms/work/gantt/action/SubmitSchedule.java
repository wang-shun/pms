package com.bizvisionsoft.pms.work.gantt.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;

import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.service.WorkSpaceService;
import com.bizvisionsoft.service.model.IWBSScope;
import com.bizvisionsoft.service.model.Result;
import com.bizvisionsoft.service.model.Workspace;
import com.bizvisionsoft.serviceconsumer.Services;

public class SubmitSchedule {
	@Inject
	private IBruiService brui;

	@Execute
	public void execute(@MethodParam(value = Execute.PARAM_CONTEXT) IBruiContext context,
			@MethodParam(value = Execute.PARAM_EVENT) Event event) {
		IWBSScope rootInput = (IWBSScope) context.getRootInput();
		if (rootInput != null) {
			Workspace workspace = rootInput.getWorkspace();
			if (workspace != null) {
				Boolean checkManageItem = true;
				Result result = Services.get(WorkSpaceService.class).schedulePlanCheck(workspace, checkManageItem);

				if (Result.CODE_SUCCESS == result.code) {
					result = Services.get(WorkSpaceService.class).checkin(workspace);

					if (Result.CODE_SUCCESS == result.code) {
						MessageDialog.openInformation(brui.getCurrentShell(), "�ύ�ƻ�", result.message);
						brui.switchContent("��Ŀ����ͼ", null);
					}
				} else {
					MessageDialog.openError(brui.getCurrentShell(), "�ύ�ƻ�", result.message);
				}
			}
		}
	}
}