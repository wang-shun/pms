package com.bizvisionsoft.pms.project;

import java.text.SimpleDateFormat;
import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.GridTreeViewer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.annotations.ui.common.MethodParam;
import com.bizvisionsoft.annotations.ui.grid.GridRenderUICreated;
import com.bizvisionsoft.annotations.ui.grid.GridRenderUpdateCell;
import com.bizvisionsoft.bruiengine.service.BruiAssemblyContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.bruiengine.util.BruiColors;
import com.bizvisionsoft.bruiengine.util.BruiColors.BruiColor;
import com.bizvisionsoft.service.ProjectService;
import com.bizvisionsoft.service.model.News;
import com.bizvisionsoft.service.model.Project;
import com.bizvisionsoft.service.model.ProjectBoardInfo;
import com.bizvisionsoft.service.tools.Formatter;
import com.bizvisionsoft.service.tools.MetaInfoWarpper;
import com.bizvisionsoft.serviceconsumer.Services;

public class ProjectBoardRender {

	@Inject
	private BruiAssemblyContext context;

	@Inject
	private IBruiService bruiService;

	private GridTreeViewer viewer;

	@GridRenderUICreated
	private void uiCreated() {
		viewer = (GridTreeViewer) context.getContent("viewer");
		viewer.getGrid().setBackground(BruiColors.getColor(BruiColor.Grey_50));
		viewer.getGrid().addListener(SWT.Selection, e -> {
			if (e.text != null) {
				 if (e.text.startsWith("openProject/")) {
					SwitchPage.openProject(bruiService, new ObjectId(e.text.split("/")[1]));
				}
			} else {
				Object element = ((GridItem) e.item).getData();
				if (element instanceof Project) {
					viewer.setExpandedElements(new Object[] { element });
				}
			}
		});
		if (((List<?>) viewer.getInput()).size() > 0) {
			viewer.setExpandedElements(new Object[] { ((List<?>) viewer.getInput()).get(0) });
		}
	}



	@GridRenderUpdateCell
	private void renderCell(@MethodParam(GridRenderUpdateCell.PARAM_CELL) ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof Project) {
			renderTitle(cell, (Project) element);
		} else if (element instanceof ProjectBoardInfo) {
			renderContent(cell, ((ProjectBoardInfo) element).getProject());
		}
	}

	private void renderTitle(ViewerCell cell, Project pj) {
		GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
		gridItem.setHeight(64);
		gridItem.setBackground(BruiColors.getColor(BruiColor.Grey_50));

		StringBuffer sb = new StringBuffer();
		sb.append(
				"<div style='float:right;margin-right:16px;margin-top:0px;'><a class='layui-btn layui-btn-primary layui-btn-sm' href='openProject/"
						+ pj.get_id() + "' target='_rwt'><i class='layui-icon'>&#xe602;</i></a></div>");

		sb.append("<div class='label_title'>" + pj.getName() + "</div>");
		sb.append("<div style='width:100%;margin-top:4px;display:inline-flex;justify-content:space-between;'><div>计划: "
				+ new SimpleDateFormat(Formatter.DATE_FORMAT_DATE).format(pj.getPlanStart()) + " ~ "
				+ new SimpleDateFormat(Formatter.DATE_FORMAT_DATE).format(pj.getPlanFinish()));
		sb.append("  <span class='layui-badge-rim'>" + pj.getStatus() + "</span>");
		sb.append("  " + pj.getOverdueHtml());
		sb.append("</div>");
		sb.append("<div style='margin-right:16px;margin-top:2px;'>项目经理: " + pj.getPmInfo() + "</div></div>");

		cell.setText(sb.toString());
	}

	private void renderContent(ViewerCell cell, Project pj) {
		GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
		// int height = 32;// 顶和底间距
		gridItem.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		StringBuffer sb = new StringBuffer();

		// 如果项目有阶段，显示阶段状态。
		if (pj.isStageEnable()) {
			// height += 46;
			sb.append(getProjectStageInfo(pj));
		}

		// 添加项目时间线
		List<News> news = Services.get(ProjectService.class).getRecentNews(pj.get_id(), 5);
		if (news.size() > 0) {
			// height += 24;
			sb.append("<ul class='layui-timeline' style='margin-top:24px;margin-right:32px;'>");
			for (int i = 0; i < news.size(); i++) {
				sb.append("<li class='layui-timeline-item' style='padding-bottom:8px;'>");
				sb.append("<i class='layui-icon layui-timeline-axis'>&#xe63f;</i>");
				sb.append("<div class='layui-timeline-content layui-text'>");
				sb.append(
						"<div class='layui-timeline-title' style='white-space:nowrap;overflow:hidden;text-overflow:ellipsis;width:100%;'>"
								+ news.get(i).getSummary() + "</div>");
				sb.append("</div></li>");
				// height += 40;
			}
			sb.append("</ul>");
		}

		// 添加指标
		// height += 46;
		// TODO 计算指标

		sb.append(
				"<div style='margin-top:0px;width: 100%;display:inline-flex;justify-content:space-between;padding-right: 36px;'>");
		String ind = Formatter.getString(pj.getWAR(), "#0.0%", null);
		sb.append("<div class='brui_indicator info' style='padding:8px 16px;font-size:14px;font-weight:lighter;width:25%;'>");
		sb.append(MetaInfoWarpper.warpper(ind+"<br>工作量完成率", "反映项目工作量完成情况，<br>项目所有工作累计实际工期与计划工期的比值。"));
		sb.append("</div>");

		
		ind = Formatter.getString(pj.getDAR(), "#0.0%", null);
		sb.append("<div class='brui_indicator info' style='padding:8px 16px;font-size:14px;font-weight:lighter;width:25%;'>");
		sb.append(MetaInfoWarpper.warpper(ind+"<br>工期完成率", "反映项目实际工期情况，<br>项目实际工期与计划工期的比值。"));
		sb.append("</div>");

		ind = Formatter.getString(pj.getCAR(), "#0.0%", null);
		sb.append("<div class='brui_indicator normal' style='padding:8px 16px;font-size:14px;font-weight:lighter;width:25%;'>");
		sb.append(MetaInfoWarpper.warpper(ind+"<br>预算使用率", "反映项目预算执行情况，<br>项目累计发生成本与总预算的比值。"));
		sb.append("</div>");

		sb.append("</div>");

		// gridItem.setHeight(height);
		gridItem.setHeight(352);
		cell.setText(sb.toString());
	}

	private String getProjectStageInfo(Project pj) {
		StringBuffer text = new StringBuffer();
		text.append("<div class='layui-btn-group' "
				+ "style='margin-top:16px;width: 100%;display:inline-flex;justify-content:space-between;padding-right: 36px;'>");
		Services.get(ProjectService.class).listStage(pj.get_id()).forEach(work -> {
			String style;
			if (work.getStartOn() != null && work.getFinishOn() != null) {
				style = "layui-btn layui-btn-sm";
			} else if (work.getStartOn() != null && work.getFinishOn() == null) {
				style = "layui-btn layui-btn-normal layui-btn-sm";
			} else {
				style = "layui-btn layui-btn-primary layui-btn-sm";
			}
			text.append("<div class='" + style + "' style='width: 100%;'>" + work.getText() + "</div>");
		});
		text.append("</div>");
		return text.toString();
	}

}
