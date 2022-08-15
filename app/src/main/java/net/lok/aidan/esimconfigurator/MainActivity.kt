package net.lok.aidan.esimconfigurator

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import net.lok.aidan.esimconfigurator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getAPNs(view: View) {
        Log.d("mydebug", "getting apns")

        // lookup all apns and print error if any error thrown
        try {
            val c: Cursor? = getApplicationContext().getContentResolver()
                .query(Uri.parse("content://telephony/carriers/current"), null, null, null, null)
            Log.d("mydebug", "$c")
        } catch (e: java.lang.Exception) {
            Log.d("mydebug", "$e")
        }
    }

    fun setAPN(view: View) {
        Log.d("mydebug", "setting apn")

        // apn table URIs to access the apns
        val APN_TABLE_URI: Uri = Uri.parse("content://telephony/carriers");
        val PREFERRED_APN_URI: Uri = Uri.parse("content://telephony/carriers/preferapn");

        // prep vars to add an APN
        var id = -1
        val resolver = this.contentResolver
        var values = ContentValues()

        //create value, you can define any other APN properties in the same way
        values.put("name", "Celona APN") //choose APN name, like 3G Orange
        values.put("apn", "celonadns") //choose APN address, like cellcom.wapu.co.il

        //now we have to define APN setting page UI. You have to get operator numeric property
        //you can obtain it from TelephonyManager.getNetworkOperator() method
//        values.put("mcc", "your operator numeric high part") //for example 242
//        values.put("mnc", "your operator numeric low part") //for example 501
//        values.put("numeric", "your operator numeric") //for example 242501

        // insert a new row, with a new apn id and the info specified
        var c: Cursor? = null
        try {
            //insert new row to APN table
            val newRow = resolver.insert(APN_TABLE_URI, values)
            if (newRow != null) {
                c = resolver.query(newRow, null, null, null, null)

                //obtain the APN id
                val index = c!!.getColumnIndex("_id")
                c!!.moveToFirst()
                id = c!!.getShort(index).toInt()
            }
        } catch (e: Exception) {
            Log.d("mydebug", "$e")
        }


        // init vars for 2nd table addition
        values = ContentValues()
        values.put("apn_id", id)

        // i have 0 idea what this does but i think this changes the default apn
        // to the one that we just made above
        try {
            resolver.update(PREFERRED_APN_URI, values, null, null)
        } catch (e: java.lang.Exception) {
            Log.d("mydebug", "$e")
        }
    }
}