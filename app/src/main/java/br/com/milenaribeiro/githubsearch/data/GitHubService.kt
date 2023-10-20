package br.com.milenaribeiro.githubsearch.data

import br.com.milenaribeiro.githubsearch.domain.Repository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("users/{user}/repos")
    fun getAllRepositoriesByUser(@Path("user") user: String): Call<List<Repository>>
}