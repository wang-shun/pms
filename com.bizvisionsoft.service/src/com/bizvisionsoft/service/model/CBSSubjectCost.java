package com.bizvisionsoft.service.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.bizvisionsoft.annotations.md.mongocodex.Exclude;
import com.bizvisionsoft.annotations.md.service.Behavior;
import com.bizvisionsoft.annotations.md.service.ReadValue;
import com.bizvisionsoft.annotations.md.service.Structure;
import com.bizvisionsoft.service.CBSService;
import com.bizvisionsoft.service.ServicesLoader;

public class CBSSubjectCost implements Comparable<CBSSubjectCost> {

	private CBSItem cbsItem;

	@ReadValue
	private String id;

	@ReadValue
	private String name;

	private CBSSubjectCost parent;

	private List<CBSSubjectCost> children = new ArrayList<CBSSubjectCost>();

	public CBSSubjectCost setCBSItem(CBSItem cbsItem) {
		this.cbsItem = cbsItem;
		return this;
	}

	public CBSSubjectCost setAccountItem(AccountItem accountItem) {
		this.id = accountItem.getId();
		this.name = accountItem.getName();

		accountItem.listSubAccountItems().forEach(a -> {
			children.add(new CBSSubjectCost().setCBSItem(cbsItem).setParent(this).setAccountItem(a));
		});

		return this;
	}

	public CBSSubjectCost setParent(CBSSubjectCost parent) {
		this.parent = parent;
		return this;
	}

	@Structure("list")
	public List<CBSSubjectCost> listSubAccountItems() {
		Collections.sort(children);
		return children;
	}

	@Structure("count")
	public long countSubAccountItems() {
		return children.size();
	}

	@Override
	public int compareTo(CBSSubjectCost o) {
		return id.compareTo(o.id);
	}

	@Exclude
	private List<CBSSubject> cbsSubjects;

	public Double getCostSummary() {
		Double summary = 0d;
		if (children.size() > 0) {
			Iterator<CBSSubjectCost> iter = children.iterator();
			while (iter.hasNext()) {
				summary += iter.next().getCostSummary();
			}
		} else {
			List<CBSSubject> cbsSubjects = listCBSSubjects();
			if (cbsSubjects.size() > 0) {
				for (CBSSubject cbsSubject : cbsSubjects) {
					summary += Optional.ofNullable(cbsSubject.getCost()).orElse(0d);
				}
			}
		}
		return summary;
	}

	public List<CBSSubject> listCBSSubjects() {
		if (cbsSubjects == null) {
			cbsSubjects = ServicesLoader.get(CBSService.class).getCBSSubjectByNumber(cbsItem.get_id(), id);
		}
		return cbsSubjects;
	}

	public Double getCost(String period) {
		Double summary = 0d;
		if (children.size() > 0) {
			Iterator<CBSSubjectCost> iter = children.iterator();
			while (iter.hasNext()) {
				summary += iter.next().getCost(period);
			}
		} else {
			List<CBSSubject> cbsSubjects = listCBSSubjects();
			if (cbsSubjects.size() > 0) {
				for (CBSSubject cbsSubject : cbsSubjects) {
					if (period.equals(cbsSubject.getId())) {
						summary += Optional.ofNullable(cbsSubject.getCost()).orElse(0d);
					}
				}
			}
		}
		return summary;
	}

	public Double getCost(String startPeriod, String endPeriod) {
		Double summary = 0d;
		if (children.size() > 0) {
			Iterator<CBSSubjectCost> iter = children.iterator();
			while (iter.hasNext()) {
				summary += iter.next().getCost(startPeriod, endPeriod);
			}
		} else {
			List<CBSSubject> cbsSubjects = listCBSSubjects();
			if (cbsSubjects.size() > 0) {
				for (CBSSubject cbsSubject : cbsSubjects) {
					if (startPeriod.compareTo(cbsSubject.getId()) <= 0
							&& endPeriod.compareTo(cbsSubject.getId()) >= 0) {
						summary += Optional.ofNullable(cbsSubject.getCost()).orElse(0d);
					}
				}
			}
		}
		return summary;
	}

	@Behavior("��Ŀ�ɱ�����/�༭�ɱ�")
	private boolean behaviourEdit() {
		return children.size() == 0;
	}

}