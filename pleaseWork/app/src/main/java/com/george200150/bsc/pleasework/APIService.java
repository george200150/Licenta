package com.george200150.bsc.pleasework;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {
    @POST("/posts")
    @FormUrlEncoded
    Call<Post> savePost(@Field("title") String title,
                        @Field("body") String body,
                        @Field("userId") long userId);


    ////////////////////////////////////////////////
    @POST("/data/simple")
    Call<Simple> simple(@Body String id);

    @POST("/data/simple")
    Call<Simple> complex(@Body Simple id);
    ////////////////////////////////////////////////

    @POST("/create")
    @FormUrlEncoded
    Call<Employee> createWithHTTP(@Field("name") String name,
                        @Field("salary") String salary,
                        @Field("age") String age);

    @POST("/bitmap")
    @FormUrlEncoded
    Call<Plant> sendMyDataViaPOST(@Field("bitmap") Bitmap bitmap);

    @POST("/posts")
    Call<Post> savePost(@Body Post post);

    @PUT("/posts/{id}")
    @FormUrlEncoded
    Call<Post> updatePost(@Path("id") long id,
                          @Field("title") String title,
                          @Field("body") String body,
                          @Field("userId") long userId);

    @DELETE("/posts/{id}")
    Call<Post> deletePost(@Path("id") long id);
}