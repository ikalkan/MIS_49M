package com.ibrahim.istanbuledia

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val itemNameList = ArrayList<String>()
        val itemIdList = ArrayList<Int>()




        try {

            val database = this.openOrCreateDatabase("Items", Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM items",null)
            val itemNameIx = cursor.getColumnIndex("itemname")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val itemname = cursor.getString(itemNameIx)
                val index = cursor.getInt(idIx)

                itemNameList.add(itemname)
                itemIdList.add(index)
            }

            cursor.close()

            val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,itemNameList)
            listView.adapter = arrayAdapter

            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val intent = Intent(this,DetailsActivity::class.java)
                intent.putExtra("id",itemIdList.get(position))
                intent.putExtra("info","old")

                startActivity(intent)
            }

            //arrayAdapter.notifyDataSetChanged()

            //cursor.close()


        } catch (e: Exception) {
            e.printStackTrace()
        }




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.adding_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.ekle) {
            val intent = Intent(this,DetailsActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

}