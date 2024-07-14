package endpoints;

public enum Routes {
	
	UsersApi("/users"),
	TodosApi("/todos");
	
	private String resource;
	
	Routes(String resource){
		this.resource = resource;
	}
	
	public String getResource() {
		return resource;
	}

}
