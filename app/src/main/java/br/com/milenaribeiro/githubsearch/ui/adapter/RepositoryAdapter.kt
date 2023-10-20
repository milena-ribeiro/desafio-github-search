package br.com.milenaribeiro.githubsearch.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.milenaribeiro.githubsearch.R
import br.com.milenaribeiro.githubsearch.domain.Repository

class RepositoryAdapter(private val context: Context, private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

   // var carItemLister: (Repository) -> Unit = {}
    //var btnShareLister: (Repository) -> Unit = {}

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repositories[position]

        holder.cardRepository.setOnClickListener {
            openBrowser(context, repository.htmlUrl)
        }
        holder.nameRepository.text = repository.name

        holder.shareIconButton.setOnClickListener {
            shareRepositoryLink(context, repository.htmlUrl)
        }
    }

    // Pega a quantidade de repositorios da lista
    override fun getItemCount(): Int = repositories.size

    @SuppressLint("CutPasteId")
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val cardRepository : CardView
        val nameRepository: TextView
        val shareIconButton : ImageView

        init {
            view.apply {
                cardRepository = findViewById(R.id.cv_repository)
                nameRepository = findViewById(R.id.cv_repository)
                shareIconButton = findViewById(R.id.iv_share)
            }
        }
    }
    fun shareRepositoryLink(context: Context, urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun openBrowser(context: Context, urlRepository: String) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )
    }

}


