package nc.vo.mdm.frame;

import java.io.Serializable;

import nc.pub.mdm.frame.tool.Toolkit;

/**
 * @since 2012-03-29
 */
public class PageParam implements Serializable {
	
	private static final long serialVersionUID = -4321770791791189357L;
	public static Integer PARAM_PAGE_DEFAULT = new Integer(1);
	public static Integer PARAM_PAGESIZE_DEFAULT = new Integer(20);

	public Integer Page = null;
	public Integer PageSize = null;
	public Object[] params = null;
	public String SQL = null;
	public Integer Total = null;
	public Integer TotalPage = null;
	@SuppressWarnings("rawtypes")
	public Class voClass = null;
	public String voWhere = null;
	public String webWhere = null;

	public Integer getPage() {
		if (Page == null) {
			Page = PARAM_PAGE_DEFAULT;
		}
		return Page;
	}

	public Integer getPageSize() {
		if (PageSize == null) {
			PageSize = PARAM_PAGESIZE_DEFAULT;
		}
		return PageSize;
	}

	public Object[] getParams() {
		return params;
	}

	public String getSQL() {
		return SQL;
	}

	public Integer getTotal() {
		if (Total == null) {
			Total = new Integer(0);
		}
		return Total;
	}

	public Integer getTotalPage() {
		if (TotalPage == null) {
			TotalPage = new Integer(0);
		}
		return TotalPage;
	}

	@SuppressWarnings("rawtypes")
	public Class getVoClass() {
		return voClass;
	}

	public String getVoWhere() {
		return voWhere;
	}

	public String getWebWhere() {
		if (Toolkit.isNull(webWhere)) {
			webWhere = "";
		}
		return webWhere;
	}

	public void setPage(Integer page) {
		if (Page == null) {
			Page = PARAM_PAGE_DEFAULT;
		}
		Page = page;
	}

	public void setPageSize(Integer pageSize) {
		PageSize = pageSize;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public void setSQL(String sQL) {
		SQL = sQL;
	}

	public void setTotal(Integer total) {
		Total = total;
	}

	public void setTotalPage(Integer totalPage) {
		TotalPage = totalPage;
	}

	@SuppressWarnings("rawtypes")
	public void setVoClass(Class voClass) {
		this.voClass = voClass;
	}

	public void setVoWhere(String voWhere) {
		this.voWhere = voWhere;
	}

	public void setWebWhere(String webWhere) {
		this.webWhere = webWhere;
	}

}
