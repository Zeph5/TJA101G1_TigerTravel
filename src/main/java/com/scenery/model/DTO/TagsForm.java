package com.scenery.model.DTO;

public class TagsForm {
		
		private Integer tagsId;
	    private Integer tagsdbId;
	    private Integer sceneryId;
	    private String sceneryName;
	    private String tags;
	    
	    

	    public Integer getTagsId() {
			return tagsId;
		}

		public void setTagsId(Integer tagsId) {
			this.tagsId = tagsId;
		}

		public Integer getTagsdbId() {
	        return tagsdbId;
	    }

	    public void setTagsdbId(Integer tagsdbId) {
	        this.tagsdbId = tagsdbId;
	    }

	    public Integer getSceneryId() {
	        return sceneryId;
	    }

	    public void setSceneryId(Integer sceneryId) {
	        this.sceneryId = sceneryId;
	    }
	    
	    public String getSceneryName() {
	        return sceneryName;
	    }
	    public void setSceneryName(String sceneryName) {
	        this.sceneryName = sceneryName;
	    }

		public void setTags(String tags) {
		    this.tags = tags;
		}
		
		public String getTags() {
		    return tags;
		}
}
