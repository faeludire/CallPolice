package com.nezspencer.callpolice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_police_contact_list.*

class PoliceContactListFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_police_contact_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProviders.of(
            activity!!,
            MainViewModelFactory(FirebaseDatabase.getInstance().reference)
        )[MainViewModel::class.java]
        mainViewModel.firebaseLiveData.observe(this, Observer {
            it ?: return@Observer
            setupAdapter(it)
        })
    }

    private fun setupAdapter(contactList: List<ContactByState>) {
        elv_police_contact_list.setAdapter(GroupedContactListAdapter(activity!!, contactList) {
            makeDialIntent(it)
        })
        elv_police_contact_list.setOnGroupClickListener { parent, view, groupPosition, l ->
            val index = parent.getFlatListPosition(
                ExpandableListView.getPackedPositionForGroup(groupPosition)
            )
            parent.setItemChecked(index, parent.checkedItemPosition != index)
            return@setOnGroupClickListener false
        }
    }

    // Duplicated method from [MainFragment]
    private fun makeDialIntent(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        if (callIntent.resolveActivity(activity!!.packageManager) != null)
            startActivity(callIntent)

    }
}