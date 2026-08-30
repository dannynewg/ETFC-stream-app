package com.example.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GitHubRawApiService {

    /**
     * Fetches raw JSON configuration text directly from any GitHub raw URL:
     * e.g. https://raw.githubusercontent.com/{owner}/{repo}/{branch}/{file}.json
     */
    @GET
    @Streaming
    suspend fun fetchRawJsonFromUrl(@Url rawUrl: String): Response<ResponseBody>
}
