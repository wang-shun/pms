package com.bizvisionsoft.pms.obs;

import com.bizivisionsoft.widgets.util.Layer;
import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.assembly.TreePart;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.bruiengine.ui.Selector;
import com.bizvisionsoft.service.ProjectService;
import com.bizvisionsoft.service.model.OBSItem;
import com.bizvisionsoft.service.model.OBSModule;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.serviceconsumer.Services;

public class AddOBSModuleACT {
	@Inject
	private IBruiService brui;

	@Execute
	public void execute(@MethodParam(Execute.CONTEXT) IBruiContext context) {
		Project project = (Project) context.getRootInput();
		context.selected(o -> {
			Selector.open("组织模板选择器", context, project, l -> {
				boolean cover = false;
				// 检查重复的角色
				boolean check = Services.get(ProjectService.class).checkOBSModuleRole(((OBSModule) l.get(0)).get_id(),
						project.get_id());
				if (!check || brui.confirm("添加组织模块", "存在编号相同的角色，请确认重新生成导入模块中角色编号.")) {
					cover = true;
				}
				// 添加模块
				Services.get(ProjectService.class).addOBSModule(((OBSModule) l.get(0)).get_id(), ((OBSItem) o).get_id(),
						cover);
				// TODO 刷新节点
				TreePart tree = (TreePart) context.getContent();
				tree.refresh(o);
				Layer.message("组织模块已添加到本项目团队，");
			});
		});

	}
}
