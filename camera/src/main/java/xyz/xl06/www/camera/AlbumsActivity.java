package xyz.xl06.www.camera;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsActivity extends AppCompatActivity {

    @BindView(R.id.gd)
    GridView gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        ButterKnife.bind(this);
        init();


    }

    private void init() {



        final File[] files =setdata();
        final gridadapter gridadapter = new gridadapter(AlbumsActivity.this, files);

        gd.setAdapter(gridadapter);
        // File jpgFile = new File(fileFolder)
        // gridadapter
        gd.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                //Toast.makeText(AlbumsActivity.this, "点击了", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(AlbumsActivity.this);

                builder.setMessage("想好要删除吗?");
                //点击对话框以外的区域是否让对话框消失
                builder.setCancelable(true);
                //设置正面按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AlbumsActivity.this, "你点击了是的", Toast.LENGTH_SHORT).show();
                        File name = files[i];
                        if(name!=null){
                            name.delete();
                            //gridadapter.notifyDataSetChanged();
                            if(gridadapter!=null){
                                 //重新获取数据
                                File[] setdata = setdata();
                                gridadapter.setdatas(setdata);
                               // gridadapter.notifyDataSetChanged();
                            }
                        }
                        dialog.dismiss();

                    }


                });
                //设置反面按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AlbumsActivity.this, "你点击了不是", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            }


        });



    }

    private File[] setdata() {

        final File fileFolder = new File(Environment.getExternalStorageDirectory()
                + "/xll/");
        File[] files1 = fileFolder.listFiles();

        return files1;
    }
}
