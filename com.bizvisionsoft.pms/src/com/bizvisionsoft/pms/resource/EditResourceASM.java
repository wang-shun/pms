package com.bizvisionsoft.pms.resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import com.bizvisionsoft.annotations.ui.common.CreateUI;
import com.bizvisionsoft.annotations.ui.common.Init;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.bruicommons.model.Action;
import com.bizvisionsoft.bruicommons.model.Column;
import com.bizvisionsoft.bruiengine.assembly.GridPart;
import com.bizvisionsoft.bruiengine.assembly.StickerTitlebar;
import com.bizvisionsoft.bruiengine.service.BruiAssemblyContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.bruiengine.service.UserSession;
import com.bizvisionsoft.bruiengine.ui.ActionMenu;
import com.bizvisionsoft.bruiengine.ui.BruiToolkit;
import com.bizvisionsoft.bruiengine.ui.Selector;
import com.bizvisionsoft.bruiengine.util.BruiColors;
import com.bizvisionsoft.bruiengine.util.BruiColors.BruiColor;
import com.bizvisionsoft.bruiengine.util.Util;
import com.bizvisionsoft.service.WorkService;
import com.bizvisionsoft.service.datatools.FilterAndUpdate;
import com.bizvisionsoft.service.model.ResourceActual;
import com.bizvisionsoft.service.model.ResourceAssignment;
import com.bizvisionsoft.service.model.ResourcePlan;
import com.bizvisionsoft.service.model.ResourceTransfer;
import com.bizvisionsoft.serviceconsumer.Services;

public class EditResourceASM extends GridPart {

	@Inject
	private IBruiService brui;

	@Inject
	private BruiAssemblyContext context;

	private Calendar start;

	private Calendar end;

	private GridTreeViewer viewer;

	private Locale locale;

	private List<Document> resource;

	private ResourceTransfer rt;

	private WorkService workService = Services.get(WorkService.class);

	private Composite content;

	private int showType;

	private boolean showResActual;

	private boolean showResPlan;

	private boolean showResTypeInfo;

	private boolean showDelete;

	private boolean showConflict;

	private boolean showFooter;

	private boolean canEditDateValue;

	private ArrayList<GridColumn> footerCols;

	public EditResourceASM() {
	}

	public EditResourceASM(IBruiService brui, BruiAssemblyContext context, Composite parent) {
		this.brui = brui;
		this.context = context;
		this.content = parent;
	}

	@Override
	public GridTreeViewer getViewer() {
		return viewer;
	}

	@Init
	protected void init() {
		rt = (ResourceTransfer) context.getInput();
	}

	public void setResourceTransfer(ResourceTransfer rt) {
		this.rt = rt;
		start = Calendar.getInstance();
		start.setTime(rt.getFrom());
		end = Calendar.getInstance();
		end.setTime(rt.getTo());
		showType = rt.getShowType();
		showResTypeInfo = rt.isShowResTypeInfo();
		showResPlan = rt.isShowResPlan();
		showResActual = rt.isShowResActual();
		showDelete = rt.isCanDelete();
		showConflict = rt.isShowConflict();
		canEditDateValue = rt.isCanEditDateValue();
		Grid grid = viewer.getGrid();
		GridColumnGroup[] columnGroups = grid.getColumnGroups();
		for (GridColumnGroup gridColumnGroup : columnGroups) {
			String name = (String) gridColumnGroup.getData("name");
			if (!"costRate".equals(name) && !"actual".equals(name) && !"plan".equals(name) && gridColumnGroup != null
					&& !gridColumnGroup.isDisposed()) {
				gridColumnGroup.dispose();
			}
		}
		GridColumn[] columns = grid.getColumns();
		for (GridColumn gridColumn : columns) {
			String name = (String) gridColumn.getData("name");
			if (("rowAction".equals(name) || "delete".equals(name)) && gridColumn != null && !gridColumn.isDisposed()) {
				gridColumn.dispose();
			}
		}
		createDateColumn();

		createRowAction(grid);

		doRefresh();

	}

	@CreateUI
	public void createUI(Composite parent) {
		locale = RWT.getLocale();

		parent.setLayout(new FormLayout());

		Action closeAction = null;
		List<Action> rightActions = null;
		StickerTitlebar bar = null;
		if (rt.isCanClose()) {
			closeAction = new Action();
			closeAction.setName("close");
			closeAction.setImage("/img/close.svg");
		}

		if (rt.isCanAdd()) {
			rightActions = new ArrayList<Action>();
			Action addAction = new Action();
			addAction.setName("add");
			if (ResourceTransfer.TYPE_PLAN == rt.getType())
				addAction.setText("添加资源计划");
			else
				addAction.setText("添加资源用量");
			addAction.setForceText(true);
			addAction.setStyle("normal");
			rightActions.add(addAction);
		}
		if (closeAction != null || closeAction != null) {
			bar = new StickerTitlebar(parent, closeAction, rightActions).setActions(context.getAssembly().getActions());
			bar.addListener(SWT.Selection, e -> {
				Action action = ((Action) e.data);
				if ("close".equals(action.getName())) {
					brui.closeCurrentContent();
				} else if ("add".equals(action.getName())) {
					allocateResource();
				}
			});
			FormData fd = new FormData();
			bar.setLayoutData(fd);
			fd.left = new FormAttachment(0);
			fd.top = new FormAttachment(0);
			fd.right = new FormAttachment(100);
			fd.height = 48;

			String barText;
			if (ResourceTransfer.TYPE_PLAN == rt.getType())
				barText = "资源计划用量 ";
			else
				barText = "资源实际用量 ";
			bar.setText(barText);
		}

		content = UserSession.bruiToolkit().newContentPanel(parent);
		FormData fd = new FormData();
		content.setLayoutData(fd);
		if (bar != null) {
			fd.left = new FormAttachment(0, 8);
			fd.top = new FormAttachment(bar, 8);
			fd.right = new FormAttachment(100, -8);
			fd.bottom = new FormAttachment(100, -8);
		} else {
			fd.left = new FormAttachment(0);
			fd.top = new FormAttachment(0);
			fd.right = new FormAttachment(100);
			fd.bottom = new FormAttachment(100);
		}
		content.setLayout(new FillLayout(SWT.VERTICAL));
		start = Calendar.getInstance();
		if (rt.getFrom() != null)
			start.setTime(rt.getFrom());

		end = Calendar.getInstance();
		if (rt.getTo() != null)
			end.setTime(rt.getTo());
		showType = rt.getShowType();
		showResTypeInfo = rt.isShowResTypeInfo();
		showResPlan = rt.isShowResPlan();
		showResActual = rt.isShowResActual();
		showDelete = rt.isCanDelete();
		showConflict = rt.isShowConflict();
		showFooter = rt.isShowFooter();
		canEditDateValue = rt.isCanEditDateValue();

		//

		viewer = new GridTreeViewer(content, SWT.H_SCROLL | SWT.V_SCROLL);
		Grid grid = viewer.getGrid();
		grid.setHeaderVisible(true);
		grid.setFooterVisible(showFooter);
		grid.setLinesVisible(true);
		UserSession.bruiToolkit().enableMarkup(grid);

		footerCols = new ArrayList<GridColumn>();
		createViewer();

		createRowAction(grid);

		doRefresh();

	}

	private void createRowAction(Grid grid) {
		if (showDelete) {
			int actionColWidth = BruiToolkit.actionMargin + BruiToolkit.actionImgBtnWidth + BruiToolkit.actionMargin;
			GridColumn col = new GridColumn(grid, SWT.NONE);
			col.setData("name", "rowAction");
			col.setWidth(actionColWidth);
			col.setMoveable(false);
			col.setResizeable(false);
			col.setData("fixedRight", true);

			GridViewerColumn vcol = new GridViewerColumn(viewer, col);
			vcol.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return "<a class='cellbutton warning' href='delete' target='_rwt'>"
							+ "<img alter='删除' src='rwt-resources/extres/img/minus_16_w.svg' style='cursor:pointer;' width='16px' height='16px'>"
							+ "</a>";
				}
			});
		}
	}

	public void doRefresh() {
		if (rt.getWorkIds() != null || rt.getResTypeId() != null) {
			resource = workService.getResource(rt);
			viewer.setInput(resource);
			doRefreshFooterText();
		}
	}

	private void doRefreshFooterText() {
		footerCols.forEach(col -> {
			col.setFooterText(getFooterText((String) col.getData("name")));
		});
	}

	private void allocateResource() {
		// 显示资源选择框
		Action hrRes = new Action();
		hrRes.setName("hr");
		hrRes.setText("人力资源");
		hrRes.setImage("/img/team_w.svg");
		hrRes.setStyle("normal");

		Action eqRes = new Action();
		eqRes.setName("eq");
		eqRes.setText("设备资源");
		eqRes.setImage("/img/equipment_w.svg");
		eqRes.setStyle("normal");

		Action typedRes = new Action();
		typedRes.setName("tr");
		typedRes.setText("资源类型");
		typedRes.setImage("/img/resource_w.svg");
		typedRes.setStyle("info");

		// 弹出menu
		new ActionMenu(brui).setActions(Arrays.asList(hrRes, eqRes, typedRes)).handleActionExecute("hr", a -> {
			addResource("人力资源选择器");
			return false;
		}).handleActionExecute("eq", a -> {
			addResource("设备设施选择器");
			return false;
		}).handleActionExecute("tr", a -> {
			addResource("资源类型选择器");
			return false;
		}).open();
	}

	private void addResource(String editorId) {
		Selector.open(editorId, context, null, l -> {
			List<ResourceAssignment> resas = new ArrayList<ResourceAssignment>();
			if (ResourceTransfer.TYPE_PLAN == rt.getType()) {
				l.forEach(o -> {
					rt.getWorkIds().forEach(
							work_id -> resas.add(new ResourceAssignment().setTypedResource(o).setWork_id(work_id)));
				});
				workService.addResourcePlan(resas);
			} else if (ResourceTransfer.TYPE_ACTUAL == rt.getType()) {
				l.forEach(o -> {
					rt.getWorkIds().forEach(work_id -> {
						ResourceAssignment ra = new ResourceAssignment().setTypedResource(o).setWork_id(work_id);
						ra.from = start.getTime();
						ra.to = end.getTime();
						resas.add(ra);
					});
				});
				workService.addResourceActual(resas);
			}
			doRefresh();
		});
	}

	private void delete(Object data) {
		if (data == null)
			return;
		Document doc = (Document) data;
		rt.getWorkIds().forEach(work_id -> {
			if (rt.getType() == ResourceTransfer.TYPE_PLAN) {
				if (doc.get("usedEquipResId") != null)
					workService.deleteEquipmentResourcePlan(work_id, (String) doc.get("usedEquipResId"));
				else if (doc.get("usedHumanResId") != null)
					workService.deleteHumanResourcePlan(work_id, (String) doc.get("usedHumanResId"));
				else if (doc.get("usedTypedResId") != null)
					workService.deleteTypedResourcePlan(work_id, (String) doc.get("usedTypedResId"));
			} else if (rt.getType() == ResourceTransfer.TYPE_ACTUAL) {
				if (doc.get("usedEquipResId") != null)
					workService.deleteEquipmentResourceActual(work_id, (String) doc.get("usedEquipResId"));
				else if (doc.get("usedHumanResId") != null)
					workService.deleteHumanResourceActual(work_id, (String) doc.get("usedHumanResId"));
				else if (doc.get("usedTypedResId") != null)
					workService.deleteTypedResourceActual(work_id, (String) doc.get("usedTypedResId"));
			}
		});
		doRefresh();
	}

	private void openResourceConflict(Object data) {
		if (data == null)
			return;

		Document doc = (Document) data;

		ResourceTransfer newRT = new ResourceTransfer();
		newRT.setWorkIds(rt.getWorkIds());
		newRT.setType(ResourceTransfer.TYPE_PLAN);
		newRT.setShowType(ResourceTransfer.SHOWTYPE_MULTIWORK_ONERESOURCE);
		newRT.setFrom(start.getTime());
		newRT.setTo(end.getTime());
		newRT.setUsedEquipResId(doc.getString("usedEquipResId"));
		newRT.setUsedHumanResId(doc.getString("usedHumanResId"));
		newRT.setUsedTypedResId(doc.getString("usedTypedResId"));
		newRT.setResTypeId(doc.getObjectId("resTypeId"));
		newRT.setCheckTime(true);
		newRT.setCanAdd(false);

		brui.openContent(brui.getAssembly("编辑资源情况"), newRT);
	}

	private void updateQty(String text, Object data) {
		if (data == null)
			return;

		Document doc = (Document) data;

		String dialogTitle = "";
		String dialogMessage = "";
		if (rt.getType() == ResourceTransfer.TYPE_PLAN) {
			dialogTitle = "填写资源计划用量";
		} else if (rt.getType() == ResourceTransfer.TYPE_ACTUAL) {
			dialogTitle = "填写资源实际用量";
		}
		if (text.startsWith("Basic")) {
			dialogMessage = "请填写资源标准用量";
		} else if (text.startsWith("OverTime")) {
			dialogMessage = "请填写资源加班用量";
		}
		InputDialog id = new InputDialog(brui.getCurrentShell(), dialogTitle, dialogMessage, null, t -> {
			if (t.trim().isEmpty())
				return "请输入资源用量";
			double d;
			try {
				d = Double.parseDouble(t);
			} catch (Exception e) {
				return "输入的类型错误";
			}
			try {
				if (text.startsWith("Basic") && d > doc.getDouble("basicWorks")) {
					return "资源标准用量不能大于:" + doc.getDouble("basicWorks");
				} else if (text.startsWith("OverTime") && d > doc.getDouble("overTimeWorks")) {
					return "资源加班用量不能大于:" + doc.getDouble("overTimeWorks");
				}
			} catch (Exception e) {
				return e.getMessage();
			}
			return null;
		});
		if (InputDialog.OK == id.open()) {
			double qty = Double.parseDouble(id.getValue());
			if (text.indexOf("-") > 0) {
				Date period = null;
				try {
					period = new SimpleDateFormat("yyyyMMdd").parse(text.split("-")[1]);
					if (period != null && rt.getType() == ResourceTransfer.TYPE_PLAN) {
						ResourcePlan rp = new ResourcePlan();
						rp.setWork_id(doc.getObjectId("work_id"));
						rp.setUsedEquipResId(doc.getString("usedEquipResId"));
						rp.setUsedHumanResId(doc.getString("usedHumanResId"));
						rp.setUsedTypedResId(doc.getString("usedTypedResId"));
						rp.setResTypeId(doc.getObjectId("resTypeId"));
						rp.setId(period);
						if (text.startsWith("Basic")) {
							rp.setPlanBasicQty(qty);
						} else if (text.startsWith("OverTime")) {
							rp.setPlanOverTimeQty(qty);
						}
						workService.insertResourcePlan(rp);
					} else if (period != null && rt.getType() == ResourceTransfer.TYPE_ACTUAL) {
						ResourceActual ra = new ResourceActual();
						ra.setWork_id(doc.getObjectId("work_id"));
						ra.setUsedEquipResId(doc.getString("usedEquipResId"));
						ra.setUsedHumanResId(doc.getString("usedHumanResId"));
						ra.setUsedTypedResId(doc.getString("usedTypedResId"));
						ra.setResTypeId(doc.getObjectId("resTypeId"));
						ra.setId(period);
						if (text.startsWith("Basic")) {
							ra.setActualBasicQty(qty);
						} else if (text.startsWith("OverTime")) {
							ra.setActualOverTimeQty(qty);
						}
						workService.insertResourceActual(ra);
					}
				} catch (ParseException e) {
				}

			} else {
				ObjectId _id = null;
				String key = "";
				if (text.startsWith("Basic")) {
					_id = new ObjectId(text.replace("Basic", ""));
					key += "BasicQty";
				} else if (text.startsWith("OverTime")) {
					_id = new ObjectId(text.replace("OverTime", ""));
					key += "OverTimeQty";
				}
				if (_id != null && rt.getType() == ResourceTransfer.TYPE_PLAN) {
					workService.updateResourcePlan(new FilterAndUpdate().filter(new Document("_id", _id))
							.set(new Document("plan" + key, qty)).bson());
				} else if (_id != null && rt.getType() == ResourceTransfer.TYPE_ACTUAL) {
					workService.updateResourceActual(new FilterAndUpdate().filter(new Document("_id", _id))
							.set(new Document("actual" + key, qty)).bson());
				}

			}
			doRefresh();
		}
	}

	@Override
	public void setViewerInput() {
	}

	@Override
	public void setViewerInput(List<?> input) {
	}

	private void createViewer() {
		Grid grid = viewer.getGrid();
		int fixecColumns = 0;
		if (showType == ResourceTransfer.SHOWTYPE_MULTIWORK_MULTIRESOURCE)
			fixecColumns = 5;
		else if (showType == ResourceTransfer.SHOWTYPE_ONEWORK_MULTIRESOURCE)
			fixecColumns = 3;
		else if (showType == ResourceTransfer.SHOWTYPE_MULTIWORK_ONERESOURCE)
			fixecColumns = 4;

		if (showConflict)
			fixecColumns += 1;

		if (fixecColumns != 0)
			grid.setData(RWT.FIXED_COLUMNS, fixecColumns);

		createColumn();

	}

	private void createColumn() {
		Column c;
		if (showType == ResourceTransfer.SHOWTYPE_MULTIWORK_MULTIRESOURCE
				|| showType == ResourceTransfer.SHOWTYPE_MULTIWORK_ONERESOURCE) {
			c = new Column();
			c.setName("workName");
			c.setText("工作名称");
			c.setWidth(160);
			c.setAlignment(SWT.LEFT);
			c.setMoveable(false);
			c.setResizeable(true);
			createTitleColumn(c);

			c = new Column();
			c.setName("projectName");
			c.setText("项目名称");
			c.setWidth(160);
			c.setAlignment(SWT.LEFT);
			c.setMoveable(false);
			c.setResizeable(true);
			createTitleColumn(c);
		}

		if (showType == ResourceTransfer.SHOWTYPE_MULTIWORK_ONERESOURCE) {
			c = new Column();
			c.setName("startDate");
			c.setText("开始");
			c.setWidth(96);
			c.setAlignment(SWT.LEFT);
			c.setMoveable(false);
			c.setResizeable(true);
			createTitleColumn(c);

			c = new Column();
			c.setName("endDate");
			c.setText("完成");
			c.setWidth(96);
			c.setAlignment(SWT.LEFT);
			c.setMoveable(false);
			c.setResizeable(true);
			createTitleColumn(c);
		}

		if (showType == ResourceTransfer.SHOWTYPE_MULTIWORK_MULTIRESOURCE
				|| showType == ResourceTransfer.SHOWTYPE_ONEWORK_MULTIRESOURCE) {
			c = new Column();
			c.setName("resId");
			c.setText("资源编号");
			c.setWidth(120);
			c.setAlignment(SWT.LEFT);
			c.setMoveable(false);
			c.setResizeable(true);
			createTitleColumn(c);

			c = new Column();
			c.setName("type");
			c.setText("资源类型");
			c.setWidth(120);
			c.setAlignment(SWT.LEFT);
			c.setMoveable(false);
			c.setResizeable(true);
			createTitleColumn(c);

			c = new Column();
			c.setName("name");
			c.setText("名称");
			c.setWidth(120);
			c.setAlignment(SWT.LEFT);
			c.setMoveable(false);
			c.setResizeable(true);
			createTitleColumn(c);
		}

		if (showConflict) {
			c = new Column();
			c.setName("conflict");
			c.setText("");
			c.setWidth(55);
			c.setAlignment(SWT.CENTER);
			c.setMoveable(false);
			c.setResizeable(true);
			createTitleColumn(c);
		}

		if (showResTypeInfo) {
			GridColumnGroup grp = new GridColumnGroup(viewer.getGrid(), SWT.CENTER);
			grp.setData("name", "costRate");
			grp.setText("费率（元）");
			grp.setExpanded(true);

			GridColumn col = new GridColumn(grp, SWT.CENTER);
			col.setText("标准");
			col.setData("name", "basicRate");
			col.setWidth(80);
			col.setMoveable(false);
			col.setResizeable(false);
			col.setAlignment(SWT.RIGHT);
			col.setResizeable(true);
			col.setDetail(true);
			col.setSummary(true);

			GridViewerColumn vcol = new GridViewerColumn(viewer, col);
			vcol.setLabelProvider(getTitleLabelProvider("basicRate"));

			col = new GridColumn(grp, SWT.CENTER);
			col.setData("name", "overtimeRate");
			col.setText("加班");
			col.setWidth(80);
			col.setMoveable(false);
			col.setResizeable(false);
			col.setAlignment(SWT.RIGHT);
			col.setResizeable(true);
			col.setDetail(true);
			col.setSummary(true);
			vcol = new GridViewerColumn(viewer, col);
			vcol.setLabelProvider(getTitleLabelProvider("overtimeRate"));
		}
		if (showResPlan) {
			GridColumnGroup grp = new GridColumnGroup(viewer.getGrid(), SWT.CENTER);
			grp.setData("name", "plan");
			grp.setText("计划");
			grp.setExpanded(true);

			GridColumn col = new GridColumn(grp, SWT.CENTER);
			col.setText("标准用量");
			col.setData("name", "planBasicQty");
			col.setWidth(80);
			col.setMoveable(false);
			col.setResizeable(false);
			col.setAlignment(SWT.RIGHT);
			col.setResizeable(true);
			col.setDetail(true);
			col.setSummary(true);

			if (showFooter)
				footerCols.add(col);

			GridViewerColumn vcol = new GridViewerColumn(viewer, col);
			vcol.setLabelProvider(getTitleLabelProvider("planBasicQty"));

			col = new GridColumn(grp, SWT.CENTER);
			col.setData("name", "planOverTimeQty");
			col.setText("加班用量");
			col.setWidth(80);
			col.setMoveable(false);
			col.setResizeable(false);
			col.setAlignment(SWT.RIGHT);
			col.setResizeable(true);
			col.setDetail(true);
			col.setSummary(true);

			if (showFooter)
				footerCols.add(col);

			vcol = new GridViewerColumn(viewer, col);
			vcol.setLabelProvider(getTitleLabelProvider("planOverTimeQty"));

			col = new GridColumn(grp, SWT.CENTER);
			col.setData("name", "planAmount");
			col.setText("金额（元）");
			col.setWidth(100);
			col.setMoveable(false);
			col.setResizeable(false);
			col.setAlignment(SWT.RIGHT);
			col.setResizeable(true);
			col.setDetail(true);
			col.setSummary(true);

			if (showFooter)
				footerCols.add(col);

			vcol = new GridViewerColumn(viewer, col);
			vcol.setLabelProvider(getTitleLabelProvider("planAmount"));
		}
		if (showResActual) {
			GridColumnGroup grp = new GridColumnGroup(viewer.getGrid(), SWT.CENTER);
			grp.setData("name", "actual");
			grp.setText("实际");
			grp.setExpanded(true);

			GridColumn col = new GridColumn(grp, SWT.CENTER);
			col.setText("标准用量");
			col.setData("name", "actualBasicQty");
			col.setWidth(80);
			col.setMoveable(false);
			col.setResizeable(false);
			col.setAlignment(SWT.RIGHT);
			col.setResizeable(true);
			col.setDetail(true);
			col.setSummary(true);

			if (showFooter)
				footerCols.add(col);

			GridViewerColumn vcol = new GridViewerColumn(viewer, col);
			vcol.setLabelProvider(getTitleLabelProvider("actualBasicQty"));

			col = new GridColumn(grp, SWT.CENTER);
			col.setData("name", "actualOverTimeQty");
			col.setText("加班用量");
			col.setWidth(80);
			col.setMoveable(false);
			col.setResizeable(false);
			col.setAlignment(SWT.RIGHT);
			col.setResizeable(true);
			col.setDetail(true);
			col.setSummary(true);

			if (showFooter)
				footerCols.add(col);

			vcol = new GridViewerColumn(viewer, col);
			vcol.setLabelProvider(getTitleLabelProvider("actualOverTimeQty"));

			col = new GridColumn(grp, SWT.CENTER);
			col.setData("name", "actualAmount");
			col.setText("金额（元）");
			col.setWidth(100);
			col.setMoveable(false);
			col.setResizeable(false);
			col.setAlignment(SWT.RIGHT);
			col.setResizeable(true);
			col.setDetail(true);
			col.setSummary(true);

			if (showFooter)
				footerCols.add(col);

			vcol = new GridViewerColumn(viewer, col);
			vcol.setLabelProvider(getTitleLabelProvider("actualAmount"));
		}
		createDateColumn();

		viewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public Object[] getElements(Object value) {
				if (value instanceof List) {
					return ((List<?>) value).toArray();
				} else if (value instanceof Object[]) {
					return (Object[]) value;
				}
				return new Object[0];
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}

		});
		viewer.getGrid().addListener(SWT.Selection, l -> {
			if (l.text != null)
				if ("delete".equals(l.text))
					delete(l.item.getData());
				else if ("conflict".equals(l.text))
					openResourceConflict(l.item.getData());
				else
					updateQty(l.text, l.item.getData());
		});
	}

	private String getFooterText(String key) {
		double value = 0d;
		for (Document doc : resource) {
			value += getDoubleValue(doc.get(key));
		}
		return Util.getFormatText(value, "#,##0.0", locale);
	}

	private void createDateColumn() {
		Calendar now = Calendar.getInstance();
		now.setTime(start.getTime());
		createDateColumn(now.getTime());
		while (now.before(end)) {
			now.add(Calendar.DATE, 1);
			createDateColumn(now.getTime());
		}

		Column c = new Column();
		c.setName("delete");
		c.setText("");
		c.setWidth(0);
		c.setAlignment(SWT.CENTER);
		c.setMoveable(false);
		c.setResizeable(true);
		createTitleColumn(c);
	}

	private void createDateColumn(Date now) {
		String name = Util.getFormatText(now, null, locale);
		GridColumnGroup grp = new GridColumnGroup(viewer.getGrid(), SWT.CENTER);

		grp.setData("name", name);
		grp.setText(name);
		grp.setExpanded(true);

		GridColumn col = new GridColumn(grp, SWT.CENTER);
		col.setText("标准");
		col.setData("name", "Basic");
		col.setWidth(48);
		col.setMoveable(false);
		col.setAlignment(SWT.RIGHT);
		col.setResizeable(true);
		col.setDetail(true);
		col.setSummary(true);

		GridViewerColumn vcol = new GridViewerColumn(viewer, col);
		vcol.setLabelProvider(getColumnLabelProvider(now, "Basic"));

		col = new GridColumn(grp, SWT.CENTER);
		col.setData("name", "OverTime");
		col.setText("加班");
		col.setWidth(48);
		col.setMoveable(false);
		col.setAlignment(SWT.RIGHT);
		col.setResizeable(true);
		col.setDetail(true);
		col.setSummary(true);

		vcol = new GridViewerColumn(viewer, col);
		vcol.setLabelProvider(getColumnLabelProvider(now, "OverTime"));
	}

	@SuppressWarnings("unchecked")
	private ColumnLabelProvider getColumnLabelProvider(Date now, String key) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String id = sdf.format(now);

		ColumnLabelProvider labelProvider = new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Object obj = ((Document) element).get("resource");
				if (obj instanceof List) {
					List<Document> list = (List<Document>) obj;
					for (Document doc : list) {
						if (id.equals(sdf.format(doc.get("id")))) {
							Object value;
							if (ResourceTransfer.TYPE_PLAN == rt.getType()) {
								value = doc.get("plan" + key + "Qty");
							} else {
								value = doc.get("actual" + key + "Qty");
							}
							String text = Util.getFormatText(value, null, locale);
							if (canEditDateValue && isWorkDay((Document) element))
								return "<a href='" + key + doc.get("_id").toString()
										+ "' target='_rwt' style='width: 100%;'>"
										+ ("0.0".equals(text)
												? ("<button class='layui-btn layui-btn-xs layui-btn-primary' style='bottom:0px;right:0px;'>"
														+ "<i class='layui-icon  layui-icon-edit'></i></button>")
												: text)
										+ "</a>";
							else
								return "0.0".equals(text) ? "" : text;
						}
					}

				}

				if (canEditDateValue && ("OverTime".equals(key) || isWorkDay((Document) element)))
					return "<a href='" + key + "-" + id
							+ "' target='_rwt' style='width: 100%;'><button class='layui-btn layui-btn-xs layui-btn-primary' style='bottom:0px;right:0px;'>"
							+ "<i class='layui-icon  layui-icon-edit'></i></button></a>";

				return "";
			}

			private Boolean isWorkDay = null;

			private boolean isWorkDay(Document doc) {
				if (isWorkDay == null) {
					isWorkDay = false;
					Calendar cal = Calendar.getInstance();
					cal.setTime(now);
					cal.setFirstDayOfWeek(Calendar.SUNDAY);
					String week;
					switch (cal.get(Calendar.DAY_OF_WEEK)) {
					case 1:
						week = "周日";
						break;
					case 2:
						week = "周一";
						break;
					case 3:
						week = "周二";
						break;
					case 4:
						week = "周三";
						break;
					case 5:
						week = "周四";
						break;
					case 6:
						week = "周五";
						break;
					default:
						week = "周六";
						break;
					}
					boolean result = false;

					Document calendar = (Document) doc.get("calendar");
					Object obj = calendar.get("workTime");
					if (obj instanceof List) {
						List<Document> list = (List<Document>) obj;
						for (Document workTime : list) {
							if (workTime.getBoolean("workingDay", false)) {
								Object date = workTime.get("date");
								if (date != null) {
									if (id.equals(sdf.format(date)))
										isWorkDay = true;
								} else {
									Object day = workTime.get("day");
									if (day instanceof List) {
										result = ((List<?>) day).contains(week);
									}
								}
							}
						}
					}

					isWorkDay = result;
				}
				return isWorkDay;
			}

			@Override
			public Color getBackground(Object element) {
				if (rt.isCheckTime()) {
					double workTime = 0;
					Iterator<Document> iter = resource.iterator();
					while (iter.hasNext()) {
						Object obj = iter.next().get("resource");
						if (obj instanceof List) {
							List<Document> list = (List<Document>) obj;
							for (Document doc : list) {
								if (id.equals(sdf.format(doc.get("id")))) {
									Object value;
									if (ResourceTransfer.TYPE_PLAN == rt.getType())
										value = doc.get("plan" + key + "Qty");
									else
										value = doc.get("actual" + key + "Qty");

									if (value instanceof Number) {
										workTime += ((Number) value).doubleValue();
									}
								}
							}
						}
					}
					return workTime > 8 ? BruiColors.getColor(BruiColor.Red_400) : null;
				}

				if (!isWorkDay((Document) element))
					return BruiColors.getColor(BruiColor.Grey_50);

				return null;
			}
		};
		return labelProvider;
	}

	private void createTitleColumn(Column c) {
		GridColumn col = new GridColumn(viewer.getGrid(), SWT.NONE);
		col.setText(c.getText());
		col.setWidth(c.getWidth());
		col.setMoveable(c.isMoveable());
		col.setResizeable(c.isResizeable());

		GridViewerColumn vcol = new GridViewerColumn(viewer, col);
		vcol.setLabelProvider(getTitleLabelProvider(c.getName()));

	}

	private ColumnLabelProvider getTitleLabelProvider(String name) {
		return new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String format = null;
				Object value;
				if ("startDate".equals(name)) {
					if (((Document) element).get("actualStart") != null) {
						value = ((Document) element).get("actualStart");
					} else {
						value = ((Document) element).get("planStart");
					}
				} else if ("endDate".equals(name)) {
					if (((Document) element).get("actualFinish") != null) {
						value = ((Document) element).get("actualFinish");
					} else {
						value = ((Document) element).get("planFinish");
					}
				} else if ("planAmount".equals(name)) {
					format = "#,###.0";
					value = ((Document) element).get(name);
				} else if ("actualAmount".equals(name)) {
					format = "#,###.0";
					value = ((Document) element).get(name);
				} else if ("conflict".equals(name)) {
					if (Boolean.TRUE.equals(((Document) element).get("conflict")))
						if (canEditDateValue)
							value = "<a class='layui-badge layui-bg-red' href='conflict' target='_rwt'>冲突</a>";
						else
							value = "<span class='layui-badge layui-bg-red' >冲突</span>";
					else
						value = "";
				} else {
					value = ((Document) element).get(name);
					if (value instanceof Number && ((Number) value).doubleValue() == 0.0)
						value = "";
				}
				return Util.getFormatText(value, format, locale);
			}
		};
	}

	private double getDoubleValue(Object object) {
		if (object instanceof Number)
			return ((Number) object).doubleValue();

		return 0;
	}
}
