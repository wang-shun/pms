package com.bizvisionsoft.pms.work.action;

import java.util.Date;

import com.bizivisionsoft.widgets.util.Layer;
import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.assembly.GridPart;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.service.WorkService;
import com.bizvisionsoft.service.model.ICommand;
import com.bizvisionsoft.service.model.Work;
import com.bizvisionsoft.serviceconsumer.Services;

public class FinishWork {

	@Inject
	private IBruiService brui;

	@Execute
	public void execute(@MethodParam(Execute.CONTEXT) IBruiContext context) {
		context.selected(e -> {
			if (brui.confirm("完成工作", "请确认完成工作：" + (Work) e + "。")) {
				if (Services.get(WorkService.class)
						.finishWork(brui.command(((Work) e).get_id(), new Date(), ICommand.Finish_Work)).isEmpty()) {
					Layer.message("工作已完成");
					GridPart grid = (GridPart) context.getContent();
					grid.remove(e);
					brui.updateSidebarActionBudget("处理工作");
				}
			}
		});
	}

}
