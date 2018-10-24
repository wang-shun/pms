package com.bizvisionsoft.pms.resource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import com.bizivisionsoft.widgets.gantt.GanttEvent;
import com.bizivisionsoft.widgets.gantt.GanttEventCode;
import com.bizivisionsoft.widgets.util.Layer;
import com.bizvisionsoft.annotations.ui.common.CreateUI;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.bruicommons.model.Action;
import com.bizvisionsoft.bruiengine.assembly.GanttPart;
import com.bizvisionsoft.bruiengine.assembly.StickerTitlebar;
import com.bizvisionsoft.bruiengine.service.BruiAssemblyContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.bruiengine.ui.AssemblyContainer;
import com.bizvisionsoft.bruiengine.util.Controls;
import com.bizvisionsoft.service.model.ResourceTransfer;
import com.bizvisionsoft.service.model.Work;

public class ReadonlyResourcePlanASM {

	@Inject
	private IBruiService brui;

	@Inject
	private BruiAssemblyContext context;

	private GanttPart gantt;

	private EditResourceASM grid;

	private Work work;

	@CreateUI
	public void createUI(Composite parent) {
		parent.setLayout(new FormLayout());

		StickerTitlebar bar = new StickerTitlebar(parent, null, null).setText("资源计划")
				.setActions(context.getAssembly().getActions());
		FormData fd = new FormData();
		bar.setLayoutData(fd);
		fd.left = new FormAttachment(0);
		fd.top = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.height = 48;

		Composite content = Controls.contentPanel(parent).mLoc().mTop(bar).layout(new FillLayout(SWT.VERTICAL)).get();

		gantt = (GanttPart) new AssemblyContainer(content, context).setAssembly(brui.getAssembly("项目甘特图（资源计划分配）"))
				.setServices(brui).create().getContext().getContent();

		ResourceTransfer rt = new ResourceTransfer();
		rt.setType(ResourceTransfer.TYPE_PLAN);
		rt.setShowType(ResourceTransfer.SHOWTYPE_ONEWORK_MULTIRESOURCE);
		rt.setCheckTime(true);
		rt.setCanAdd(false);
		rt.setCanClose(false);
		rt.setCanEditDateValue(false);
		rt.setShowResPlan(true);
		rt.setShowResTypeInfo(true);
		rt.setShowConflict(true);
		rt.setShowFooter(true);

		grid = (EditResourceASM) new AssemblyContainer(content, context).setAssembly(brui.getAssembly("编辑资源情况"))
				.setInput(rt).setServices(brui).create().getContext().getContent();
		// 侦听gantt的selection
		gantt.addGanttEventListener(GanttEventCode.onTaskSelected.name(), l -> select((Work) ((GanttEvent) l).task));

		bar.addListener(SWT.Selection, l -> {
			if ("分配资源".equals(((Action) l.data).getName())) {
				if (this.work == null) {
					Layer.message("请先选择将要分配资源的工作");
					return;
				} else if (this.work.isSummary()) {
					Layer.message("无需对总成型工作分配资源");
					return;
				}
			}
		});

	}

	private void select(Work work) {
		if (this.work != null && this.work.get_id().equals(work.get_id())) {
			return;
		}
		this.work = work;
		// 查询
		ResourceTransfer rt = new ResourceTransfer();
		rt.addWorkIds(work.get_id());
		rt.setType(ResourceTransfer.TYPE_PLAN);
		rt.setShowType(ResourceTransfer.SHOWTYPE_ONEWORK_MULTIRESOURCE);
		rt.setFrom(work.getPlanStart());
		rt.setTo(work.getPlanFinish());
		rt.setCanAdd(false);
		rt.setCanClose(false);
		rt.setCanEditDateValue(false);
		rt.setShowResPlan(true);
		rt.setShowResTypeInfo(true);
		rt.setShowConflict(true);
		rt.setShowFooter(true);
		rt.setTitle(work.getFullName() + "工作资源计划用量");

		grid.setResourceTransfer(rt);
	}

}
