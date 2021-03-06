package com.awesometech.leoco.ds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.bizvisionsoft.annotations.md.service.DataSet;
import com.bizvisionsoft.annotations.ui.common.Inject;
import com.bizvisionsoft.bruiengine.service.BruiAssemblyContext;
import com.bizvisionsoft.bruiengine.service.IBruiService;
import com.bizvisionsoft.sqldb.SqlQuery;

public class WFDataset {

	@Inject
	private BruiAssemblyContext context;

	@Inject
	private IBruiService brui;

	@DataSet("node")
	private List<Document> nodes() {
		Document prItem = (Document) context.getInput();
		String inst_ID = prItem.get("INST_ID").toString();
		List<String> curNodeList  = new ArrayList<String>();
		new SqlQuery("ecology").sql("select cur_node_id as CURRENTNODE from V_PMS_wf_inst where id = '" + inst_ID + "'").forEach(d -> {
			if( null != d.get("CURRENTNODE").toString() && !"".equals(d.get("CURRENTNODE").toString())) {
				String curNodes = d.get("CURRENTNODE").toString();
				Arrays.asList(curNodes.split(",")).forEach(n -> {
					curNodeList.add(n.toString());
				});
			}
		});
		
		List<Document> logList = new ArrayList<>();
		String logSql = buildLogSql(inst_ID);
		new SqlQuery("ecology").sql(logSql).into(logList);
		
		List<Document> list = new ArrayList<Document>();
		new SqlQuery("ecology").sql("select inst.wf_id ,node.id as id,node.text as text,'ffffff' as foreground  from V_PMS_wf_node node,V_PMS_wf_inst inst where node.wf_id = inst.wf_id and inst.id = '" + inst_ID + "' ")
			.changeKeyCase(true).forEach(n -> {
			List<String> curList = curNodeList;
			if(null != n.get("id").toString() && curList.contains(n.get("id").toString())) {
				n.append("background", "b0120a");
			}else {
				n.append("background", "455a64");
			}
			n.put("id", n.get("id").toString());
			String operaters = "";
			for(Document log:logList) {
				if(null != log.get("NODE_ID") && log.get("NODE_ID").toString().equals(n.getString("id")) && null != log.get("OPERATOR")) {
					operaters += "|" + log.getString("OPERATOR");
				}
			}
			n.put("text", n.getString("text") + operaters);
			list.add(n);
		});
		return list;
	}
	
	private String buildLogSql(String inst_id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select inst_id,node_id,node_name,opr_dat,operator,tgt,comment from V_PMS_wf_log " );
		sb.append(" where inst_id = '" + inst_id + "' ");
		sb.append("  order by opr_dat desc ");
		return sb.toString();
	}

	@DataSet("link")
	private List<Document> links() {
		Document prItem = (Document) context.getInput();
		String inst_ID = prItem.get("INST_ID").toString();
		List<Document> list = new ArrayList<Document>();
		new SqlQuery("ecology").sql("select inst.wf_id as WF_ID, link.src as SRC, link.tgt as TGT from V_PMS_wf_link link,V_PMS_wf_inst inst where inst.wf_id = link.wf_id and inst.id = '" + inst_ID + "'").changeKeyCase(true)
			.forEach(n ->{
				n.put("src", n.get("src").toString());
				n.put("tgt", n.get("tgt").toString());
				list.add(n);
			});
		return list;
	}

}
