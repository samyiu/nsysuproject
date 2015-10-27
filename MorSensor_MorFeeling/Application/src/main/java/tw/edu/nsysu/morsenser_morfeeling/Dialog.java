package tw.edu.nsysu.morsenser_morfeeling;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sam07 on 2015/10/27.
 */
public class Dialog extends AppCompatActivity implements DialogInterface.OnCancelListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(Dialog.this);
        alertDialog.setTitle("HI~")
        .setMessage("this is a dialog box ")
        .setOnCancelListener(this);
        alertDialog.show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
    public static void createDialog(int dialog, Context context){
        Intent myIntent = new Intent(context, Dialog.class);
        Bundle bundle = new Bundle();
        bundle.putInt("dialog", dialog);
        myIntent.putExtras(bundle);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }


}
