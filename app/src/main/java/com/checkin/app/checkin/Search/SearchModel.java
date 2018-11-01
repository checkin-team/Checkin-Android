package com.checkin.app.checkin.Search;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Jogi Miglani on 26-10-2018.
 */

public class SearchModel {


    @JsonProperty("pk") private String pk;
    @JsonProperty("display_name") private String name;
    @JsonProperty("display_pic") private String imageUrl;
    private RESULT_TYPE type;

    public enum RESULT_TYPE {
        RESTAURANT(103), PEOPLE(101);

        final int type;
        RESULT_TYPE(int type) {
            this.type = type;
        }

        public static RESULT_TYPE getByType(int type) {
            for (RESULT_TYPE resultType: RESULT_TYPE.values()) {
                if (resultType.type == type)
                    return resultType;
            }
            return PEOPLE;
        }
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public String getName() {
        return name;
    }

    public RESULT_TYPE getType() {
        return type;
    }

    public String getPk() {
        return pk;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("result_type")
    public void setType(int type) {
        this.type=RESULT_TYPE.getByType(type);
    }

    public void setPk(String pk) {
        this.pk = pk;
    }
}
