package com.bizvisionsoft.pms.filecabinet;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.assembly.GridPart;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.bruiengine.ui.Editor;
import com.bizvisionsoft.bruiengine.ui.Selector;
import com.bizvisionsoft.service.DocumentService;
import com.bizvisionsoft.service.WorkService;
import com.bizvisionsoft.service.model.Docu;
import com.bizvisionsoft.service.model.Folder;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.service.model.WorkPackage;
import com.bizvisionsoft.serviceconsumer.Services;

public class CreateDocuOfPackageACT {

	@Inject
	private IBruiService brui;

	// 已经把模板和项目的分开，没有必要用Behavior设置，所以注释以下代码
	// @Behavior("输出文档/创建文档")
	// private boolean behaviour(@MethodParam(MethodParam.ROOT_CONTEXT_INPUT_OBJECT)
	// Object input) {
	// return !(input instanceof ProjectTemplate);
	// }

	@Execute
	public void execute(final @MethodParam(Execute.PARAM_CONTEXT) IBruiContext context) {
		WorkPackage wp = (WorkPackage) context.getInput();
		ObjectId project_id = Services.get(WorkService.class).getProjectId(wp.getWork_id());
		// 选择文件夹
		Selector.open("项目文件夹选择", context, new Project().set_id(project_id), em -> {
			Docu docu = new Docu()//
					.setCreationInfo(brui.operationInfo())//
					.addWorkPackageId(wp.get_id())//
					.setFolder_id(((Folder) em.get(0)).get_id())//
					.setName(wp.description);
			Editor.open("通用文档编辑器", context, docu, (r, t) -> {
				((GridPart) context.getContent()).insert(Services.get(DocumentService.class).createDocument(t));
			});
		});
	}

}
