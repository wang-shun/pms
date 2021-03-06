package com.bizvisionsoft.pms.work.action;

import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruiengine.assembly.GridPart;
import com.bizvisionsoft.bruiengine.service.IBruiContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.bruiengine.ui.Selector;
import com.bizvisionsoft.service.ServicesLoader;
import com.bizvisionsoft.service.WorkService;
import com.bizvisionsoft.service.datatools.FilterAndUpdate;
import com.bizvisionsoft.service.model.User;
import com.bizvisionsoft.service.model.Work;
import com.mongodb.BasicDBObject;

public class AssignerWorkACT {

	@Inject
	private IBruiService brui;

	@Execute
	public void execute(@MethodParam(Execute.CONTEXT) IBruiContext context) {
		context.selected(e -> {
			Selector.open("ָ���û�ѡ����", context, (Work) e, l -> {
				ServicesLoader.get(WorkService.class)
						.updateWork(new FilterAndUpdate().filter(new BasicDBObject("_id", ((Work) e).get_id()))
								.set(new BasicDBObject("chargerId", ((User) l.get(0)).getUserId())).bson());

				GridPart view = (GridPart) context.getContent();
				view.refreshAll();
			});
		});
	}
}
