package com.bms.agendabootcamp.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bms.agendabootcamp.R

class ContactsAdapter(val contactsList : ArrayList<Contact>)
    : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_view,parent, false)
            //layout inflado
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(contactsList[position])
        //posição de cada item
    }

    override fun getItemCount(): Int {
        return contactsList.size
        //tamanho da lista
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindItem(contact: Contact){

            val textName = itemView.findViewById<TextView>(R.id.contact_name)
            val textPhone = itemView.findViewById<TextView>(R.id.contact_phone)

            //referencia o modelo do objeto criado (Contact)com o que aparece na view
            textName.text = contact.name
            textPhone.text = contact.phoneNumber

        }
    }
}