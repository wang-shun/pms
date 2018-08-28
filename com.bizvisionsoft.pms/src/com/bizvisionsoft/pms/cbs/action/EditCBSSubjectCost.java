package com.bizvisionsoft.pms.cbs.action;

import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.bruiengine.ui.Editor;
import com.bizvisionsoft.bruiengine.util.EngUtil;
import com.bizvisionsoft.pms.cbs.assembly.CostSubject;
import com.bizvisionsoft.service.model.AccountItem;
import com.bizvisionsoft.service.model.CBSItem;
import com.bizvisionsoft.service.model.CBSSubject;
import com.bizvisionsoft.service.model.ICBSScope;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.service.model.Work;

@Deprecated
public class EditCBSSubjectCost {

	@Inject
	private IBruiService bruiService;

	@Execute
	public void execute(@MethodParam(Execute.PARAM_CONTEXT) IBruiContext context) {
		context.selected(parent -> {
			AccountItem account = (AccountItem) parent;
			CBSSubject period;
			Object cbs = context.getInput();
			if (cbs == null) {
				ICBSScope rootInput = (ICBSScope) context.getRootInput();
				period = new CBSSubject().setCBSItem_id(rootInput.getCBS_id()).setSubjectNumber(account.getId());
			} else if (cbs instanceof Work || cbs instanceof Project) {
				period = new CBSSubject().setCBSItem_id(((ICBSScope) cbs).getCBS_id())
						.setSubjectNumber(account.getId());
			} else {
				period = new CBSSubject().setCBSItem_id(((CBSItem) cbs).get_id()).setSubjectNumber(account.getId());
			}

			EngUtil.ifInstanceThen(context.getRootInput(), ICBSScope.class, r -> period.setRange(r.getCBSRange()));

			Editor.create("�ڼ�ɱ��༭��", context, period, true).setTitle("�༭��Ŀ�ڼ�ɱ�").ok((r, o) -> {
				CostSubject grid = (CostSubject) context.getContent();
				grid.updateCBSSubjectAmount(o);
			});

		});
	}

}