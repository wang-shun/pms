package com.bizvisionsoft.pms.obs.action;

import java.util.Optional;

import org.eclipse.swt.widgets.Event;

import com.bizvisionsoft.annotations.AUtil;
import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;

public class CreateOBSItemSelectRole extends AbstractCreateOBSItem {

	@Inject
	private IBruiService bruiService;

	@Execute
	public void execute(@MethodParam(value = Execute.PARAM_CONTEXT) IBruiContext context,
			@MethodParam(value = Execute.PARAM_EVENT) Event event) {
		context.selected(em -> {

			String message = Optional.ofNullable(AUtil.readLabel(em)).orElse("");
			message = "添加角色到" + message;
			String editor = "OBS节点编辑器（角色）";
			open(context, em, message, editor,true);

		});
	}

}
