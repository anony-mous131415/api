package io.revx.api.enums;

public enum AstUpdateStatus {
	UPDATE_ALL(0),
	UPDATE_WITH_FBX_TAG(1),
	UPATE_WITH_TWITTER_TAG(2);
	
	private Integer id;
	
	private AstUpdateStatus(Integer id){
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
