package com.checkin.app.checkin.Review.NewReview;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReviewImageModel {
    @JsonProperty("use_case")
    private REVIEW_IMAGE_USE_CASE useCase;

    public ReviewImageModel(REVIEW_IMAGE_USE_CASE useCase) {
        this.useCase = useCase;
    }

    public enum REVIEW_IMAGE_USE_CASE {
        FOOD("review.food"), AMBIENCE("review.ambience");

        String tag;

        REVIEW_IMAGE_USE_CASE(String tag) {
            this.tag = tag;
        }
    }

    @JsonProperty("use_case")
    public String getUseCase() {
        return useCase.tag;
    }

    public void setUseCase(REVIEW_IMAGE_USE_CASE useCase) {
        this.useCase = useCase;
    }
}
