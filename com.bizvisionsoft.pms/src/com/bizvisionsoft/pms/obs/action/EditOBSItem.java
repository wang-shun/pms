package com.bizvisionsoft.pms.obs.action;

import java.util.Optional;

import com.bizvisionsoft.annotations.AUtil;
import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruicommons.model.Assembly;
import com.bizvisionsoft.bruiengine.assembly.IStructuredDataPart;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.bruiengine.ui.Editor;
import com.bizvisionsoft.service.model.OBSItem;

public class EditOBSItem {

	@Inject
	private IBruiService bruiService;

	@Execute
	public void execute(@MethodParam(Execute.CONTEXT) IBruiContext context) {
		context.selected(em -> {
			Assembly assembly;
			if (((OBSItem) em).isRole()) {
				assembly = bruiService.getAssembly("OBS节点编辑器（角色）");
			} else if (((OBSItem) em).isRole()) {
				assembly = bruiService.getAssembly("OBS节点编辑器（根）");
			} else {
				assembly = bruiService.getAssembly("OBS节点编辑器（团队）");
			}
			String message = "编辑 " + Optional.ofNullable(AUtil.readLabel(em)).orElse("");

			Editor<Object> editor = new Editor<Object>(assembly, context).setEditable(true).setTitle(message)
					.setInput(false, em);

			editor.ok((r, o) -> {
				Object part = context.getContent();
				if (part instanceof IStructuredDataPart) {
					((IStructuredDataPart) part).doModify(em, o, r);
				}
			});

		});

	}

}
