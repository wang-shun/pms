package com.bizvisionsoft.pms.risk;

import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.assembly.GridPart;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.bruiengine.ui.Editor;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.service.model.RBSItem;

public class CreateRootRBSItemACT {

	@Inject
	private IBruiService brui;

	@Execute
	private void execute(@MethodParam(Execute.PARAM_CONTEXT) IBruiContext context) {
		Project project = (Project) context.getRootInput();
		Editor.open("������༭��", context,
				new RBSItem().setProject_id(project.get_id()).setCreationInfo(brui.operationInfo()), (r, o) -> {
					GridPart grid = (GridPart) context.getContent();
					grid.doCreate(null, o);
				});

	}

}
