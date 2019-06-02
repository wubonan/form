package com.daymooc.fcms.common.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;


@SuppressWarnings("serial")
public abstract class BaseReptile<M extends BaseReptile<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setContext(java.lang.Integer context) {
		set("context", context);
	}

	public java.lang.String getcontext() {
		return get("context");
	}
}