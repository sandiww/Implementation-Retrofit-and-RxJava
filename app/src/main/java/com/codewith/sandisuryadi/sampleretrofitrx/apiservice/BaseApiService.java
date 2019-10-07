package com.codewith.sandisuryadi.sampleretrofitrx.apiservice;

import com.codewith.sandisuryadi.sampleretrofitrx.model.ResponseRepos;

import java.util.List;
import io.reactivex.Observable;

import retrofit2.http.GET;
import retrofit2.http.Path;

/*
  Fungsi @Path disini adalah untuk mengisi value yang sudah kita set.
  Contoh : {username} disini nantinya akan diisi dengan kebutuhan yang disesuaikan.

  Observable disini ialah dari RxJava. Karena pada contoh disini kita akan menggabungkan
  Retrofit dengan RxJava.
   */
public interface BaseApiService {
    @GET("users/{username}/repos")
    Observable<List<ResponseRepos>> requestRepos(@Path("username") String username);

}
