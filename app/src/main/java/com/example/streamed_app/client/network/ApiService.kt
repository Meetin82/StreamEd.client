package com.example.streamed_app.client.network

import com.example.streamed_app.client.models.AddCommentRequest
import com.example.streamed_app.client.models.AddCourseRequest
import com.example.streamed_app.client.models.AddSubscribe
import com.example.streamed_app.client.models.AddWebinarRequest
import com.example.streamed_app.client.models.LoginRequest
import com.example.streamed_app.client.models.RegisterRequest
import com.example.streamed_app.client.models.ResetPassRequest
import com.example.streamed_app.client.models.UpdUserRequest
import com.example.streamed_app.client.models.VerifyAndUpdPassRequest
import com.example.streamed_app.client.network.response.BaseResponse
import com.example.streamed_app.client.network.response.CommentResponse
import com.example.streamed_app.client.network.response.CourseResponse
import com.example.streamed_app.client.network.response.UserInfoResponse
import com.example.streamed_app.client.network.response.WebinarResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/v1/signup")
    fun signUpUser(@Body user: RegisterRequest): Call<BaseResponse>

    @POST("api/v1/login")
    fun loginUser(@Body user: LoginRequest): Call<BaseResponse>

    @GET("api/v1/get-user-info")
    fun getUserInfo(@Header("Authorization") jwt: String): Call<UserInfoResponse>

    @POST("api/v1/upd-user")
    fun updateUser(@Header("Authorization") jwt: String, @Body user: UpdUserRequest): Call<BaseResponse>
    @POST("api/v1/delete-user")
    fun deleteUser(@Header("Authorization") jwt: String): Call<String>

    @POST("api/v1/reset_password_req")
    fun resetPasswordRequest(@Body resetPassRequest: ResetPassRequest): Call<BaseResponse>

    @POST("api/v1/reset_password")
    fun resetPassword(@Body verifyAndUpdPassRequest: VerifyAndUpdPassRequest): Call<BaseResponse>

    @GET("api/v1/get-all-courses")
    fun getAllCourses(): Call<List<CourseResponse>>

    @GET("media")
    fun getVideoFile(@Query("prefix") prefix: String): Call<List<String>>

    @POST("api/v1/create-course")
    fun createCourse(@Header("Authorization") jwt: String, @Body courseRequest: AddCourseRequest): Call<BaseResponse>

    @POST("api/v1/get-my-courses")
    fun getMyCourses(@Header("Authorization") jwt: String): Call<List<CourseResponse>>

    @DELETE("api/v1/delete-course")
    fun deleteCourse(@Query("id") courseId: Int, @Header("Authorization") jwt: String): Call<BaseResponse>

    @POST("api/v1/subscribe-user")
    fun subscribeUser(@Body subscribeRequest: AddSubscribe): Call<BaseResponse>

    @DELETE("api/v1/unsubscribe-user")
    fun unsubscribeUser(@Query("id") courseId: Int, @Header("Authorization") jwt: String): Call<BaseResponse>

    @GET("api/v1/get-all-sub-courses")
    fun getAllSubCourses(): Call<List<Int>>

    @GET("api/v1/get-all-webinars-for-prof")
    fun getAllWebinarsForProf(): Call<List<WebinarResponse>>

    @GET("api/v1/get-webinar-by-code")
    fun getWebinarByCode(@Query("code") code: String, @Header("Authorization") jwt: String): Call<List<WebinarResponse>>

    @GET("api/v1/get-all-webinars-for-sub")
    fun getAllWebinarsForSub(): Call<List<WebinarResponse>>

    @POST("api/v1/create-webinar")
    fun createWebinar(@Header("Authorization") jwt: String, @Body webinarRequest: AddWebinarRequest): Call<String>

    @POST("api/v1/update-webinar")
    fun updateWebinar(@Header("Authorization") jwt: String, @Body webinarRequest: AddWebinarRequest): Call<BaseResponse>

    @DELETE("api/v1/delete-webinar")
    fun deleteWebinar(@Query("id") webinarId: Int, @Header("Authorization") jwt: String): Call<BaseResponse>

    @POST("api/v1/add-comment")
    fun addComment(@Header("Authorization") jwt: String, @Body commentRequest: AddCommentRequest): Call<BaseResponse>

    @DELETE("api/v1/delete-comment")
    fun deleteComment(@Query("commentId") commentId: Int, @Header("Authorization") jwt: String): Call<BaseResponse>

    @GET("api/v1/get-all-comments")
    fun getAllComments(@Query("webinarId") webinarId: Int, @Header("Authorization") jwt: String): Call<List<CommentResponse>>
}
