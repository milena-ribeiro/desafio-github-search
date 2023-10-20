package br.com.milenaribeiro.githubsearch

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.milenaribeiro.githubsearch.data.GitHubService
import br.com.milenaribeiro.githubsearch.domain.Repository
import br.com.milenaribeiro.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService
    lateinit var carregamento : ProgressBar
    lateinit var icWifiOff : ImageView
    lateinit var txtWifiOff : TextView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        setupListeners()
        //getAllReposByUserName()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {

        nomeUsuario = findViewById(R.id.et_name_user)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }
    //metodo responsavel por configurar os listeners click da tela
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {

            val conexao = isInternetAvailable()

            if (!conexao) {

                icWifiOff.isVisible = true
                txtWifiOff.isVisible = true

            }else {

                icWifiOff.isVisible = false
                txtWifiOff.isVisible = false

                val pesquisarNome = nomeUsuario.text.toString()
                getAllReposByUserName(pesquisarNome)
                saveUserLocal()
                listaRepositories.isVisible = false

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        val usuarioInformado = nomeUsuario.text.toString()
        val sharedPreference = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPreference.edit()) {
            putString("saved_username", usuarioInformado)
            apply()
        }
    }

    private fun showUserName() {
        val sharedPreference = getPreferences(MODE_PRIVATE) ?: return
        val ultimoPesquisado = sharedPreference.getString("saved_username", null)

        if (!ultimoPesquisado.isNullOrEmpty()) {
            nomeUsuario.setText(ultimoPesquisado)
        }
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)
    }


    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName(userName: String) {
        if (userName.isNotEmpty()) {
            carregamento.isVisible = true

            githubApi.getAllRepositoriesByUser(userName)
                .enqueue(object : Callback<List<Repository>> {

                    override fun onResponse(
                        call: Call<List<Repository>>,
                        response: Response<List<Repository>>
                    ) {
                        if (response.isSuccessful) {

                            carregamento.isVisible = false
                            listaRepositories.isVisible = true

                            val repositories = response.body()

                            repositories?.let {
                                setupAdapter(repositories)
                            }
                        } else {

                            carregamento.isVisible = false

                            val context = applicationContext
                            Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<List<Repository>>, t: Throwable) {

                        carregamento.isVisible = false

                        val context = applicationContext
                        Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        val adapter = RepositoryAdapter(
            this, list)

        listaRepositories.adapter = adapter
    }


}