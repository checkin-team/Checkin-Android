package com.checkin.app.checkin.Review;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CustomerReviewImage {

        public CustomerReviewImage(RequestBody use_case, MultipartBody.Part image){
            this.use_case = use_case;
            this.image = image;
        }
        public RequestBody use_case;
        public MultipartBody.Part image;

}
