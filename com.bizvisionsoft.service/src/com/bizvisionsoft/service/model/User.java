package com.bizvisionsoft.service.model;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

import com.bizvisionsoft.annotations.md.mongocodex.Exclude;
import com.bizvisionsoft.annotations.md.mongocodex.Persistence;
import com.bizvisionsoft.annotations.md.mongocodex.PersistenceCollection;
import com.bizvisionsoft.annotations.md.mongocodex.SetValue;
import com.bizvisionsoft.annotations.md.service.ImageURL;
import com.bizvisionsoft.annotations.md.service.Label;
import com.bizvisionsoft.annotations.md.service.ReadValue;
import com.bizvisionsoft.annotations.md.service.WriteValue;
import com.bizvisionsoft.service.CommonService;
import com.bizvisionsoft.service.OrganizationService;
import com.bizvisionsoft.service.ServicesLoader;

@PersistenceCollection("user")
public class User implements IResourceAssignment {

	@Persistence
	private ObjectId _id;

	@Persistence
	@ReadValue({ "userId", "组织角色/id", "资源类型/id" })
	@WriteValue({ "userId", "组织角色/id" })
	private String userId;

	@ImageURL({ "userId", "组织角色/id", "资源类型/id" })
	@Exclude
	private String logo = "/img/user_c.svg";

	@ReadValue
	@WriteValue
	@Persistence
	private String password;

	@WriteValue("password2")
	private void setPassword2(String password) {
		if (this.password != null && !this.password.isEmpty() && !password.equals(this.password))
			throw new RuntimeException("两次输入的密码不一致。");
	}

	@Persistence
	@ReadValue({ "name", "部门工作日程表/label" })
	@WriteValue
	private String name;
	
	@Persistence
	@ReadValue
	@WriteValue
	private String position;

	@Persistence
	@ReadValue
	@WriteValue
	private String email;

	@Persistence
	@ReadValue
	@WriteValue
	private String tel;

	@Persistence
	@ReadValue
	@WriteValue
	private String mobile;

	@Persistence
	@ReadValue
	@WriteValue
	private String weixin;

	@Persistence
	@ReadValue
	@WriteValue
	private boolean activated;

	@Persistence
	@ReadValue
	@WriteValue
	private List<RemoteFile> headPics;

	@Persistence("org_id")
	private ObjectId organizationId;

	@SetValue
	@ReadValue
	private String orgFullName;

	@Persistence
	@ReadValue
	@WriteValue
	private List<String> certificates;

	@Persistence
	@ReadValue
	@WriteValue
	private ObjectId resourceType_id;

	@WriteValue
	@ReadValue
	private boolean admin;

	@WriteValue
	@ReadValue
	private boolean buzAdmin;
	
	@Persistence
	private boolean changePSW;

	public String getUserId() {
		return userId;
	}

	public String getHeadpicURL() {
		if (isSU()) {
			return "/bvs/svg?text=SU&color=ffffff";
		} else if (headPics != null && headPics.size() > 0)
			return headPics.get(0).getURL(ServicesLoader.url);
		return null;
	}

	@WriteValue("organization ")
	public void setOrganization(Organization org) {
		this.organizationId = Optional.ofNullable(org).map(o -> o.get_id()).orElse(null);
		this.orgFullName = Optional.ofNullable(org).map(o -> o.getFullName()).orElse(null);
	}

	@ReadValue("organization ")
	public Organization getOrganization() {
		return Optional.ofNullable(organizationId).map(_id -> ServicesLoader.get(OrganizationService.class).get(_id))
				.orElse(null);
	}

	public ObjectId getOrganizationId() {
		return organizationId;
	}

	@WriteValue("resourceType ")
	public void setResourceType(ResourceType rt) {
		this.resourceType_id = Optional.ofNullable(rt).map(o -> o.get_id()).orElse(null);
	}

	@ReadValue("resourceType ")
	public ResourceType getResourceType() {
		return Optional.ofNullable(resourceType_id)
				.map(_id -> ServicesLoader.get(CommonService.class).getResourceType(_id)).orElse(null);
	}

	@Override
	@Label
	@ReadValue({ "部门工作日程表/label", "项目团队/label" })
	public String toString() {
		return name;// + " [" + userId + "]";
	}

	@ReadValue(ReadValue.TYPE)
	@Exclude
	private String typeName = "用户";

	@ReadValue("部门工作日程表/key")
	private String getSectionKey() {
		return userId;
	}

	public boolean isActivated() {
		return activated;
	}

	public String getName() {
		return name;
	}

	public List<RemoteFile> getHeadPics() {
		return headPics;
	}

	public User setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String getResourceId() {
		return userId;
	}

	public ObjectId getResourceType_id() {
		return resourceType_id;
	}

	public boolean isAdmin() {
		return admin;
	}

	public boolean isBuzAdmin() {
		return buzAdmin;
	}

	public boolean isSU() {
		return "su".equals(userId);
	}

	public static User SU() {
		return new User().setName("超级用户").setUserId("su");
	}

	private List<String> roles;

	@ReadValue
	@WriteValue
	private String consigner;

	@ReadValue
	@WriteValue
	private Boolean trace;

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	public User setConsigner(String consigner) {
		this.consigner = consigner;
		return this;
	}

	public Boolean getTrace() {
		return trace;
	}

	public User setTrace(Boolean trace) {
		this.trace = trace;
		return this;
	}
	
	public String getConsigner() {
		return consigner;
	}

	public String getTel() {
		return tel;
	}

	public String getEmail() {
		return email;
	}

	public String getPosition() {
		return position;
	}
	
	public boolean isChangePSW() {
		return changePSW;
	}

}
