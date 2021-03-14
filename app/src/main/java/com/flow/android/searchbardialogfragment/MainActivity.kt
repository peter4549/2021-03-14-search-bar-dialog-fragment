package com.flow.android.searchbardialogfragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class MainActivity : AppCompatActivity(), SearchBarDialogFragment.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            StrDialogFragment().apply {
                show(supportFragmentManager, tag)
            }
        }
    }

    override fun <T> onItemClickListener(): SearchBarListAdapter.OnItemClickListener<T> =
        object : SearchBarListAdapter.OnItemClickListener<T> {
            override fun onClick(item: T) {
                when(item) {
                    is String -> { Toast.makeText(this@MainActivity, item, Toast.LENGTH_SHORT).show() }
                }
            }
        }

    override fun onMenuClickListener(): SearchBarDialogFragment.OnMenuClickListener =
        object: SearchBarDialogFragment.OnMenuClickListener {
            override fun onClick(dialogFragment: DialogFragment) {
                Toast.makeText(this@MainActivity, "DDD", Toast.LENGTH_SHORT).show()
            }
        }
}