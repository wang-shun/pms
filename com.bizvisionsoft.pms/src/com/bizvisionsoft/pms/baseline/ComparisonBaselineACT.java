package com.bizvisionsoft.pms.baseline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bson.types.ObjectId;

import com.bizivisionsoft.widgets.util.Layer;
import com.bizvisionsoft.annotations.ui.common.Execute;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.bruicommons.model.Assembly;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.service.model.Baseline;
import com.bizvisionsoft.service.model.Project;

/**
 * 
 * @author gdiyang
 * @date 2018/10/27
 *
 */
public class ComparisonBaselineACT {

	@Inject
	private IBruiService brui;

	@Execute
	private void execute(@MethodParam(Execute.ROOT_CONTEXT_INPUT_OBJECT) Project project,
			@MethodParam(Execute.CONTEXT_SELECTION) List<Baseline> list) {
		if (list.size() > 2) {
			Layer.message("只允许对两个基线进行对比", Layer.ICON_CANCEL);
			return;
		} else if (list.size() == 0) {
			Layer.message("最少选择一个基线与当前进度进行对比", Layer.ICON_CANCEL);
			return;
		}

		List<ObjectId> projectIds = new ArrayList<ObjectId>();
		String title = "";
		if (list.size() < 2) {
			projectIds.add(project.get_id());
			title += "上 : " + project.getName();
		}
		Collections.sort(list, new Comparator<Baseline>() {
			@Override
			public int compare(Baseline o1, Baseline o2) {
				return o1.get_id().compareTo(o2.get_id());
			}

		});
		for (Baseline l : list) {
			if ("".equals(title))
				title += "上 : " + l.getName();
			else
				title += " 下 : " + l.getName();

			projectIds.add(l.get_id());
		}
		// brui.openContent(brui.getAssembly("项目进度跟踪甘特图"), null);
		// TODO 不能直接设置Assembly，应将其作为参数传入到content中
		Assembly assembly = brui.getAssembly("项目基线比较甘特图");
		assembly.setTitle(title);
		assembly.setStickerTitle(title);
		brui.openContent(assembly, projectIds.toArray(new ObjectId[0]));
	}
}
