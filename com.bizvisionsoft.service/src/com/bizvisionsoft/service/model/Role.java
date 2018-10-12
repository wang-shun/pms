package com.bizvisionsoft.service.model;

import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.mongocodex.Exclude;
import com.bizvisionsoft.annotations.md.mongocodex.PersistenceCollection;
import com.bizvisionsoft.annotations.md.service.Behavior;
import com.bizvisionsoft.annotations.md.service.Label;
import com.bizvisionsoft.annotations.md.service.ReadValue;
import com.bizvisionsoft.annotations.md.service.Structure;
import com.bizvisionsoft.annotations.md.service.WriteValue;
import com.bizvisionsoft.service.OrganizationService;
import com.bizvisionsoft.service.ServicesLoader;

@PersistenceCollection("role")
public class Role {

	public static String ROLE_ID_PD = "项目总监";

	public static String ROLE_ID_SCM = "供应链管理";

	public static String ROLE_ID_FM = "财务管理";

	public static String ROLE_ID_MM = "制造管理";

	public static List<String> ROLES = Arrays.asList(ROLE_ID_PD, ROLE_ID_SCM, ROLE_ID_FM, ROLE_ID_MM);

	public static String ROLE_ID_PMO = "PMO";

	@ReadValue
	@WriteValue
	private ObjectId _id;

	@ReadValue
	@WriteValue
	private String id;

	@ReadValue
	@WriteValue
	private String name;

	@ReadValue
	@WriteValue
	private String description;

	@ReadValue
	@WriteValue
	private ObjectId org_id;

	public ObjectId get_id() {
		return _id;
	}

	public Role setOrg_id(ObjectId org_id) {
		this.org_id = org_id;
		return this;
	}

	@Override
	@Label
	public String toString() {
		return name + " [" + id + "]";
	}

	@ReadValue(ReadValue.TYPE)
	@Exclude
	private String typeName = "角色";

	@Structure("组织角色/list")
	public List<User> getUser() {
		return ServicesLoader.get(OrganizationService.class).queryUsersOfRole(_id);
	}

	@Structure("组织角色/count")
	public long countUser() {
		return ServicesLoader.get(OrganizationService.class).countUsersOfRole(_id);
	}

	@Behavior({ "组织角色/添加用户", "组织角色/编辑角色" }) // 控制action
	@Exclude // 不用持久化
	private boolean behaviours = true;

}
