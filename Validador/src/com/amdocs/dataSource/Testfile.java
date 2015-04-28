package com.amdocs.dataSource;

import java.nio.file.Path;

public class Testfile {
	private String Name;
	private Path path;
	private String Interface;
	private String Order;
	private String ServiceType;
	private String ServcieAction;
	private String ServcieName;
	private String Filter;
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	public String getInterface() {
		return Interface;
	}
	public void setInterface(String interface1) {
		Interface = interface1;
	}
	public String getOrder() {
		return Order;
	}
	public void setOrder(String order) {
		Order = order;
	}
	public String getServiceType() {
		return ServiceType;
	}
	public void setServiceType(String serviceType) {
		ServiceType = serviceType;
	}
	public String getServiceAction() {
		return ServcieAction;
	}
	public void setServcieAction(String servcieAction) {
		ServcieAction = servcieAction;
	}
	public String getServcieName() {
		return ServcieName;
	}
	public void setServiceName(String servcieName) {
		ServcieName = servcieName;
	}
	public String getFilter() {
		return Filter;
	}
	public void setFilter(String filter) {
		Filter = filter;
	}	

}
