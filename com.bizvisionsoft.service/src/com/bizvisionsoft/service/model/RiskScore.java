package com.bizvisionsoft.service.model;

import java.util.List;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.mongocodex.PersistenceCollection;
import com.bizvisionsoft.annotations.md.service.Label;
import com.bizvisionsoft.annotations.md.service.ReadValue;
import com.bizvisionsoft.annotations.md.service.WriteValue;
import com.bizvisionsoft.service.tools.Formatter;

@PersistenceCollection("riskScoreInd")
public class RiskScore {

	@ReadValue
	@WriteValue
	private ObjectId _id;

	@ReadValue
	@WriteValue
	private String name;

	@ReadValue
	private double score;
	
	@Override
	@Label
	public String toString() {
		return name +" ["+score+"]";
	}

	@WriteValue("score")
	private void writeScroe(String _input) {
		double _value;
		try {
			_value = Double.parseDouble(_input.trim());
			if (_value < -1 || _value > 1) {
				throw new Exception();
			}
			score = _value;
		} catch (Exception e) {
			throw new RuntimeException("分值要求输入-1~ 1之间的浮点数");
		}
	}

	@ReadValue
	private Integer maxTimeImpact;

	@WriteValue("maxTimeImpact")
	private void setMaxTimeImpact(String _maxTimeImpact) {
		if (_maxTimeImpact.trim().isEmpty()) {
			maxTimeImpact = null;
		} else {
			try {
				maxTimeImpact = Integer.parseInt(_maxTimeImpact.trim());
			} catch (Exception e) {
				throw new RuntimeException("最大时间影响要求输入整数的天数。");
			}
		}
	}

	@ReadValue
	private Integer minTimeImpact;

	@WriteValue("minTimeImpact")
	private void setMinTimeImpact(String _minTimeImpact) {
		if (_minTimeImpact.trim().isEmpty()) {
			minTimeImpact = null;
		} else {
			try {
				minTimeImpact = Integer.parseInt(_minTimeImpact.trim());
			} catch (Exception e) {
				throw new RuntimeException("最小时间影响要求输入整数的天数。");
			}
		}
	}

	@ReadValue
	private Double maxCostImpact;

	@WriteValue("maxCostImpact")
	private void writeMaxCostImpact(String _maxCostImpact) {
		if (_maxCostImpact.trim().isEmpty()) {
			maxCostImpact = null;
		} else {
			double _value;
			try {
				_value = Double.parseDouble(_maxCostImpact.trim());
				maxCostImpact = _value;
			} catch (Exception e) {
				throw new RuntimeException("最大成本影响（万元）要求输入浮点数");
			}
		}
	}

	@ReadValue
	private Double minCostImpact;

	@WriteValue("minCostImpact")
	private void writeMinCostImpact(String _minCostImpact) {
		if (_minCostImpact.trim().isEmpty()) {
			minCostImpact = null;
		} else {
			double _value;
			try {
				_value = Double.parseDouble(_minCostImpact.trim());
				minCostImpact = _value;
			} catch (Exception e) {
				throw new RuntimeException("最小成本影响（万元）要求输入浮点数");
			}
		}
	}

	@ReadValue
	@WriteValue
	private List<QuanlityInfInd> quanlityImpact;

	@ReadValue("timeImpact")
	private String getTimeImpact() {
		String text = "";
		if (minTimeImpact != null) {
			text += minTimeImpact;
		}
		text += " ~ ";
		if (maxTimeImpact != null) {
			text += maxTimeImpact;
		}
		return text;
	}

	@ReadValue("costImpact")
	private String getCostImpact() {
		String text = "";
		if (minCostImpact != null) {
			text += Formatter.getString(minCostImpact);
		}
		text += " ~ ";
		if (maxCostImpact != null) {
			text += Formatter.getString(maxCostImpact);
		}
		return text;
	}


	
	public Double getMaxCostImpact() {
		return maxCostImpact;
	}
	
	public Integer getMaxTimeImpact() {
		return maxTimeImpact;
	}
	
	public Double getMinCostImpact() {
		return minCostImpact;
	}
	
	public Integer getMinTimeImpact() {
		return minTimeImpact;
	}
	
	public List<QuanlityInfInd> getQuanlityImpact() {
		return quanlityImpact;
	}
	
	public double getScore() {
		return score;
	}
}
